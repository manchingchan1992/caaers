/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.migrator.adverseevent;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.dao.CtcTermDao;
import gov.nih.nci.cabig.caaers.dao.meddra.LowLevelTermDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventCtcTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventMeddraLowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.AeTerminology;
import gov.nih.nci.cabig.caaers.domain.Attribution;
import gov.nih.nci.cabig.caaers.domain.Ctc;
import gov.nih.nci.cabig.caaers.domain.CtcGrade;
import gov.nih.nci.cabig.caaers.domain.CtcTerm;
import gov.nih.nci.cabig.caaers.domain.ExternalAdverseEvent;
import gov.nih.nci.cabig.caaers.domain.Grade;
import gov.nih.nci.cabig.caaers.domain.Hospitalization;
import gov.nih.nci.cabig.caaers.domain.Outcome;
import gov.nih.nci.cabig.caaers.domain.TimeValue;
import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;
import gov.nih.nci.cabig.caaers.integration.schema.adverseevent.AdverseEventMeddraLowLevelTermType;
import gov.nih.nci.cabig.caaers.integration.schema.adverseevent.AdverseEventType;
import gov.nih.nci.cabig.caaers.integration.schema.adverseevent.HospitalizationType;
import gov.nih.nci.cabig.caaers.integration.schema.adverseevent.OutcomeType;
import gov.nih.nci.cabig.caaers.utils.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;

public class AdverseEventConverter {
	private CtcTermDao ctcTermDao = null;
	private LowLevelTermDao lowLevelTermDao = null;
	private MessageSource messageSource;

    public AdverseEvent convert(AdverseEventType adverseEventDto){
        AdverseEvent adverseEvent = new AdverseEvent();
        if (adverseEventDto.getVerbatim() != null) {
            adverseEvent.setDetailsForOther(adverseEventDto.getVerbatim());
        }
        adverseEvent.setExternalId(adverseEventDto.getExternalId());
        populateHospitalization(adverseEventDto,adverseEvent);
        populateGrade(adverseEventDto,adverseEvent);
        adverseEvent.setExpected(adverseEventDto.isExpected());
        populateAttributionSummary(adverseEventDto,adverseEvent);
        adverseEvent.setComments(adverseEventDto.getComments());
        adverseEvent.setReporterEmail(adverseEventDto.getReporterEmail());

        if(adverseEventDto.getStartDate() != null) adverseEvent.setStartDate(adverseEventDto.getStartDate().toGregorianCalendar().getTime());
        if(adverseEventDto.getEndDate() != null) adverseEvent.setEndDate(adverseEventDto.getEndDate().toGregorianCalendar().getTime());


        if(adverseEventDto.getDateFirstLearned() != null) {
            // Convert the timezone Server timezone but user can  send it any timezone.
            TimeZone timeZone = TimeZone.getDefault();
            adverseEventDto.getDateFirstLearned().toGregorianCalendar().setTimeZone(timeZone);
            adverseEvent.setGradedDate(adverseEventDto.getDateFirstLearned().toGregorianCalendar().getTime());
        }  else {
            adverseEvent.setGradedDate(new Date()); // Set the default date as now.
        }
        
        if(adverseEventDto.getAdverseEventCtepTerm() != null) {
        	adverseEvent.setOtherSpecify(adverseEventDto.getAdverseEventCtepTerm().getOtherSpecify());
        }
        		
        if(adverseEventDto.getAdverseEventCtepTerm() != null && adverseEventDto.getAdverseEventCtepTerm().getCtepCode() != null){
            AdverseEventCtcTerm ctcTerm = new AdverseEventCtcTerm();
            adverseEvent.setAdverseEventCtcTerm(ctcTerm);
            ctcTerm.setCtcTerm(new CtcTerm());
            ctcTerm.getCtcTerm().setCtepCode(adverseEventDto.getAdverseEventCtepTerm().getCtepCode());
            ctcTerm.getCtcTerm().setOtherRequired(adverseEventDto.getAdverseEventCtepTerm().getOtherSpecify() != null);
            if(adverseEventDto.getAdverseEventCtepTerm().getOtherMeddra() != null){
                LowLevelTerm lowLevelTerm = new LowLevelTerm();
                lowLevelTerm.setMeddraCode(adverseEventDto.getAdverseEventCtepTerm().getOtherMeddra().getMeddraCode());
              //  lowLevelTerm.setMeddraTerm(adverseEventDto.getAdverseEventCtepTerm().getOtherMeddra().getMeddraTerm());
                adverseEvent.setMeddraTerm(lowLevelTerm); adverseEvent.setLowLevelTerm(lowLevelTerm);
            }
        }

        if(adverseEventDto.getAdverseEventMeddraLowLevelTerm() != null && adverseEventDto.getAdverseEventMeddraLowLevelTerm().getMeddraCode() != null){
            LowLevelTerm lowLevelTerm = new LowLevelTerm();
            lowLevelTerm.setMeddraCode(adverseEventDto.getAdverseEventMeddraLowLevelTerm().getMeddraCode());
          //  lowLevelTerm.setMeddraTerm(adverseEventDto.getAdverseEventMeddraLowLevelTerm().getMeddraTerm());
            AdverseEventMeddraLowLevelTerm aeMeddraLowLevelTerm = new AdverseEventMeddraLowLevelTerm();
            aeMeddraLowLevelTerm.setTerm(lowLevelTerm);
            adverseEvent.setAdverseEventMeddraLowLevelTerm(aeMeddraLowLevelTerm);
        }

        if (adverseEventDto.getEventApproximateTime() != null) {
            TimeValue tv = new TimeValue();
            tv.setHour(adverseEventDto.getEventApproximateTime().getHour().intValue());
            tv.setMinute(adverseEventDto.getEventApproximateTime().getMinute().intValue());
            if (adverseEventDto.getEventApproximateTime().getAmpm().equals("AM")) {
                tv.setType(0);
            } else {
                tv.setType(1);
            }
            adverseEvent.setEventApproximateTime(tv);
        }

        if (adverseEventDto.getEventLocation() != null) {
            adverseEvent.setEventLocation(adverseEventDto.getEventLocation());
        }


        if (adverseEventDto.getOutcome() != null) {
            populateOutcomes(adverseEventDto,adverseEvent);
        }
        
        if ( adverseEventDto.getHospitalization() != null && adverseEventDto.getHospitalization().equals(HospitalizationType.YES)) {

            Outcome hOutcome = new Outcome();
            hOutcome.setOutcomeType(gov.nih.nci.cabig.caaers.domain.OutcomeType.HOSPITALIZATION);
            adverseEvent.addOutComeIfNecessary(hOutcome);

        }

        if(adverseEventDto.isIncreasedRisk() != null){
            adverseEvent.setParticipantAtRisk(adverseEventDto.isIncreasedRisk());
        }

        populateOutcomes(adverseEventDto, adverseEvent);

        return adverseEvent;
    }

	public void convertAdverseEventDtoToAdverseEventDomain(AdverseEventType adverseEventDto, 
			AdverseEvent adverseEvent, AeTerminology terminology  , Date startDateOfFirstCourse, String operation) throws CaaersSystemException{
		if(adverseEvent == null){
			adverseEvent = new AdverseEvent();
		}
		
		try{
			if (adverseEventDto.getVerbatim() != null) { 
				adverseEvent.setDetailsForOther(adverseEventDto.getVerbatim());
			}
			adverseEvent.setExternalId(adverseEventDto.getExternalId());
			populateGrade(adverseEventDto,adverseEvent);
			populateHospitalization(adverseEventDto,adverseEvent);
			adverseEvent.setExpected(adverseEventDto.isExpected());
	
			populateAttributionSummary(adverseEventDto,adverseEvent);
			adverseEvent.setComments(adverseEventDto.getComments());

            Date today = new Date();

            if(adverseEventDto.getStartDate() != null){
                adverseEvent.setStartDate(adverseEventDto.getStartDate().toGregorianCalendar().getTime());
                //start date cannot be in future.
                if(DateUtils.compareDate(today, adverseEvent.getStartDate()) < 0){
                    throw new CaaersSystemException (messageSource.getMessage("WS_AEMS_031", new String[]{adverseEventDto.getStartDate()+""},"",Locale.getDefault()));
                }
                //start date cannot be earlier than start date of the course.
                if(startDateOfFirstCourse != null && (DateUtils.compareDate(adverseEvent.getStartDate(), startDateOfFirstCourse) < 0) ){
                    throw new CaaersSystemException (messageSource.getMessage("WS_AEMS_059", new String[]{adverseEventDto.getStartDate()+"",startDateOfFirstCourse+""},"",Locale.getDefault()));
                }
            }
            if(adverseEventDto.getEndDate() != null){
                adverseEvent.setEndDate(adverseEventDto.getEndDate().toGregorianCalendar().getTime());
                //end date cannot be in future
                if(DateUtils.compareDate(today, adverseEvent.getEndDate()) < 0){
                    throw new CaaersSystemException (messageSource.getMessage("WS_AEMS_031", new String[]{adverseEventDto.getEndDate()+""},"",Locale.getDefault()));
                }
                //end date cannot be earlier than start date.
                if(adverseEvent.getStartDate() != null && (DateUtils.compareDate(adverseEvent.getEndDate(), adverseEvent.getStartDate()) < 0)){
                    throw new CaaersSystemException (messageSource.getMessage("WS_AEMS_075", new String[]{adverseEventDto.getEndDate()+""},"",Locale.getDefault()));
                }
            }

            if(adverseEventDto.getDateFirstLearned() != null){
                adverseEvent.setGradedDate(adverseEventDto.getDateFirstLearned().toGregorianCalendar().getTime());
                //awareness date cannot be in future.
                if(DateUtils.compareDate(today, adverseEvent.getGradedDate()) < 0){
                    throw new CaaersSystemException (messageSource.getMessage("WS_AEMS_031", new String[]{adverseEventDto.getDateFirstLearned()+""},"",Locale.getDefault()));
                }

                if(adverseEvent.getStartDate() != null && DateUtils.compareDate(adverseEvent.getStartDate(), adverseEvent.getGradedDate()) < 0){
                    throw new CaaersSystemException(messageSource.getMessage("WS_AEMS_073", new String[]{adverseEventDto.getDateFirstLearned().toString()}, "Awareness date should not be after Start date", Locale.getDefault()));
                }

                if(adverseEvent.getEndDate() != null && DateUtils.compareDate(adverseEvent.getEndDate(), adverseEvent.getGradedDate()) < 0){
                    throw new CaaersSystemException(messageSource.getMessage("WS_AEMS_074", new String[]{adverseEventDto.getDateFirstLearned().toString()}, "Awareness date should not be after End date", Locale.getDefault()));
                }
            }


			if (terminology.getCtcVersion() != null && adverseEventDto.getAdverseEventCtepTerm() != null && adverseEventDto.getAdverseEventCtepTerm().getCtepCode()!=null) {
				populateCtcTerm(adverseEventDto,adverseEvent,terminology.getCtcVersion());
			}
			
			if (terminology.getMeddraVersion() != null && adverseEventDto.getAdverseEventMeddraLowLevelTerm() != null) {
				populateLowLevelTerm(adverseEventDto.getAdverseEventMeddraLowLevelTerm() ,adverseEvent); 
			}
			
			if (adverseEventDto.getEventApproximateTime() != null) { 
				TimeValue tv = new TimeValue();
				tv.setHour(adverseEventDto.getEventApproximateTime().getHour().intValue());
				tv.setMinute(adverseEventDto.getEventApproximateTime().getMinute().intValue());
				if (adverseEventDto.getEventApproximateTime().getAmpm().equals("AM")) {
					tv.setType(0);
				} else {
					tv.setType(1);
				}
				adverseEvent.setEventApproximateTime(tv);
			}

			if (adverseEventDto.getEventLocation() != null) { 
				adverseEvent.setEventLocation(adverseEventDto.getEventLocation());
			}
			if (adverseEventDto.getOutcome() != null) {
				populateOutcomes(adverseEventDto,adverseEvent);
			}
			
			if(adverseEventDto.isIncreasedRisk() != null){
				adverseEvent.setParticipantAtRisk(adverseEventDto.isIncreasedRisk());
			}		
			
		}catch(Exception e){
			throw new CaaersSystemException(e);
		}
	}
	
	
	public void convertAdverseEventDtoToExternalAdverseEventDTO(AdverseEventType adverseEventDto, 
			ExternalAdverseEvent adverseEvent, AeTerminology terminology  , Date startDateOfFirstCourse) throws CaaersSystemException{
		if(adverseEvent == null){
			throw new IllegalArgumentException("adverse event cannot be null");
		}
		
		try{
			// populate verbatim
			String verbatim = adverseEventDto.getVerbatim();
			if(verbatim != null){
				// Remove the spaces which is causing CAAERS-6077				 
				verbatim = verbatim.trim(); 
			}
			adverseEvent.setVerbatim(verbatim);
			
			// populate attribution
			if (adverseEventDto.getAttributionSummary() != null) {
                Attribution attribution = Attribution.valueOf(adverseEventDto.getAttributionSummary().name());
				adverseEvent.setAttribution(attribution.getDisplayName());
			}
			
			// populate howSerious
			StringBuilder howSeriousSb = new StringBuilder();
			
			for (OutcomeType xmlOutcome:adverseEventDto.getOutcome()) {
				String xmlOc = xmlOutcome.getOutComeEnumType().name();
				gov.nih.nci.cabig.caaers.domain.OutcomeType oct = gov.nih.nci.cabig.caaers.domain.OutcomeType.valueOf(xmlOc);
				howSeriousSb.append(oct.getShortName()).append(",");
			}
			String howSerious = howSeriousSb.toString();
			
			if(StringUtils.isNotEmpty(howSerious)) {
				//remove the last char ','
				howSerious = howSerious.substring(0, howSerious.length()-1);
				adverseEvent.setHowSerious(howSerious);
			}
				
			// populate grade
			Grade grade = Grade.getByCode(adverseEventDto.getGrade());
			adverseEvent.setGrade(grade);	
			
			// populate start date
			if(adverseEventDto.getStartDate() != null){
				adverseEvent.setStartDate(adverseEventDto.getStartDate().toGregorianCalendar().getTime());
			}
			
			// populate end date
			if(adverseEventDto.getEndDate() != null){
				adverseEvent.setEndDate(adverseEventDto.getEndDate().toGregorianCalendar().getTime());
			}	

			// populate adverse event term, code and other value

			if (terminology.getCtcVersion() != null && adverseEventDto.getAdverseEventCtepTerm()!=null && adverseEventDto.getAdverseEventCtepTerm().getCtepCode() != null) {
                adverseEvent.setAdverseEventTermCode(adverseEventDto.getAdverseEventCtepTerm().getCtepCode());
                if(adverseEventDto.getAdverseEventCtepTerm().getOtherMeddra() != null) {
                	adverseEvent.setAdverseEventTermOtherValue(adverseEventDto.getAdverseEventCtepTerm().getOtherMeddra().getMeddraCode());
                }else if (adverseEventDto.getAdverseEventCtepTerm().getOtherSpecify()!= null){
                	adverseEvent.setOtherSpecify(adverseEventDto.getAdverseEventCtepTerm().getOtherSpecify());
                }
			}
			
			adverseEvent.setExternalId(adverseEventDto.getExternalId());
			
		}catch(Exception e){
			throw new CaaersSystemException(e);
		}
	}
	

	private void populateGrade(AdverseEventType adverseEventDto, AdverseEvent adverseEvent){		
		if(adverseEventDto.getGrade() != null) {
			Grade grade = Grade.getByCode(adverseEventDto.getGrade());
			adverseEvent.setGrade(grade);
		} else {
			adverseEvent.setGrade(null);
		}
	}
	private void populateHospitalization(AdverseEventType adverseEventDto, AdverseEvent adverseEvent){		
		if (adverseEventDto.getHospitalization() != null){
			Hospitalization hospitalization = Hospitalization.valueOf(adverseEventDto.getHospitalization().name());
			adverseEvent.setHospitalization(hospitalization);			
		}
		
	}	
	private void populateAttributionSummary(AdverseEventType adverseEventDto, AdverseEvent adverseEvent){
		if (adverseEventDto.getAttributionSummary() != null){
			Attribution attributionSummary = Attribution.valueOf(adverseEventDto.getAttributionSummary().name());
			adverseEvent.setAttributionSummary(attributionSummary);
		}
	}
	private LowLevelTerm getLowLevelTerm(String code) {
		LowLevelTerm lowLevelTerm = null;
		List<LowLevelTerm> lowLevelTerms = lowLevelTermDao.getByMeddraCode(code);
		lowLevelTerm=lowLevelTerms.isEmpty() ? null : lowLevelTerms.get(0);
		return lowLevelTerm;
	}
	
	private void populateLowLevelTerm(AdverseEventMeddraLowLevelTermType adverseEventMeddraLowLevelTermType, AdverseEvent adverseEvent) {
		if (adverseEventMeddraLowLevelTermType != null) {
			AdverseEventMeddraLowLevelTermType xmlLowLevelTerm = adverseEventMeddraLowLevelTermType;
			
			LowLevelTerm lowLevelTerm = getLowLevelTerm(xmlLowLevelTerm.getMeddraCode());
			if (lowLevelTerm == null) {
				throw new CaaersSystemException (messageSource.getMessage("WS_AEMS_021", new String[]{xmlLowLevelTerm.getMeddraCode().toString()},"",Locale.getDefault()));
			} else {	
				//for update, lowlevelterm already exists
				AdverseEventMeddraLowLevelTerm adverseEventMeddraLowLevelTerm = adverseEvent.getAdverseEventMeddraLowLevelTerm();
				if( adverseEventMeddraLowLevelTerm == null ) {
					adverseEventMeddraLowLevelTerm = new AdverseEventMeddraLowLevelTerm();	
				}
				
				adverseEventMeddraLowLevelTerm.setLowLevelTerm(lowLevelTerm);
				adverseEventMeddraLowLevelTerm.setAdverseEvent(adverseEvent);
				adverseEvent.setAdverseEventMeddraLowLevelTerm(adverseEventMeddraLowLevelTerm);
			}
		}
	}
	private void populateCtcTerm(AdverseEventType adverseEventDto, AdverseEvent adverseEvent,Ctc ctc) throws CaaersSystemException{
		if (adverseEventDto.getAdverseEventCtepTerm() != null && adverseEventDto.getAdverseEventCtepTerm().getCtepCode() != null) {
			//CtcTerm ctcTerm = ctcTermDao.getCtcTerm(new String[]{adverseEventDto.getCtepCode()});//getByCtepCodeandVersion(adverseEventDto.getAdverseEventCtcTerm().getCtepCode(), adverseEventDto.getAdverseEventCtcTerm().getCtcVersion());
			CtcTerm ctcTerm = ctcTermDao.getByCtepCodeandVersion(adverseEventDto.getAdverseEventCtepTerm().getCtepCode(), ctc);

			if (ctcTerm == null) {
				throw new CaaersSystemException (messageSource.getMessage("WS_AEMS_020", new String[]{adverseEventDto.getAdverseEventCtepTerm().getCtepCode()},"",Locale.getDefault()));
			} else {
				if (ctcTerm.isOtherRequired()) {
					if (adverseEventDto.getAdverseEventCtepTerm().getOtherMeddra() != null) {
						LowLevelTerm lowLevelTerm = getLowLevelTerm(adverseEventDto.getAdverseEventCtepTerm().getOtherMeddra().getMeddraCode());
						if (lowLevelTerm == null) {
							throw new CaaersSystemException (messageSource.getMessage("WS_AEMS_021", new String[]{adverseEventDto.getAdverseEventCtepTerm().getOtherMeddra().getMeddraCode().toString()},"",Locale.getDefault()));
							
						} else {
							adverseEvent.setLowLevelTerm(lowLevelTerm);
						}
					} else {
						throw new CaaersSystemException (messageSource.getMessage("WS_AEMS_022", new String[]{},"",Locale.getDefault()));
					}
				} else {
					if (adverseEventDto.getAdverseEventCtepTerm().getOtherMeddra() != null) {
						throw new CaaersSystemException (messageSource.getMessage("WS_AEMS_023", new String[]{adverseEventDto.getAdverseEventCtepTerm().getCtepCode()},"",Locale.getDefault()));
					}
				}
				List<CtcGrade> ctcGrades = ctcTerm.getContextualGrades();
				boolean gradeAllowed = false;
				for (CtcGrade ctcGrade:ctcGrades) {
					if (ctcGrade.getGrade().getCode() == adverseEventDto.getGrade()) {
						gradeAllowed = true;
						break;
					}
				}
				if (!gradeAllowed) {
					throw new CaaersSystemException (messageSource.getMessage("WS_AEMS_030", new String[]{adverseEventDto.getGrade()+"",adverseEventDto.getAdverseEventCtepTerm().getCtepCode()},"",Locale.getDefault()));
				}
				//for update AECtcTerm already exists
				AdverseEventCtcTerm adverseEventCtcTerm = (AdverseEventCtcTerm) adverseEvent.getAdverseEventTerm();
				if(adverseEventCtcTerm == null) {
					adverseEventCtcTerm = new AdverseEventCtcTerm();
				}				
				adverseEventCtcTerm.setCtcTerm(ctcTerm);
				adverseEventCtcTerm.setAdverseEvent(adverseEvent);
				adverseEvent.setAdverseEventTerm(adverseEventCtcTerm);
			}

		}
	}

	public void populateOutcomes(AdverseEventType adverseEventDto, AdverseEvent adverseEvent) {
		for (OutcomeType xmlOutcome:adverseEventDto.getOutcome()) {
			String xmlOutcomeTypeName = xmlOutcome.getOutComeEnumType().name();

            Outcome outcome = new Outcome();
            outcome.setOutcomeType(gov.nih.nci.cabig.caaers.domain.OutcomeType.valueOf(xmlOutcomeTypeName));
            if(outcome.getOutcomeType() == gov.nih.nci.cabig.caaers.domain.OutcomeType.OTHER_SERIOUS) {
                outcome.setOther(xmlOutcome.getOther());
            }
            adverseEvent.addOutComeIfNecessary(outcome);
		}
	}

	public void setCtcTermDao(CtcTermDao ctcTermDao) {
		this.ctcTermDao = ctcTermDao;
	}


	public void setLowLevelTermDao(LowLevelTermDao lowLevelTermDao) {
		this.lowLevelTermDao = lowLevelTermDao;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}




}
