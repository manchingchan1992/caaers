package gov.nih.nci.cabig.caaers.api.impl;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.api.AdverseEventManagementService;
import gov.nih.nci.cabig.caaers.dao.AdverseEventDao;
import gov.nih.nci.cabig.caaers.dao.AdverseEventReportingPeriodDao;
import gov.nih.nci.cabig.caaers.dao.CtcTermDao;
import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.StudyParticipantAssignmentDao;
import gov.nih.nci.cabig.caaers.dao.TreatmentAssignmentDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventCtcTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventMeddraLowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.Epoch;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.StudyPersonnel;
import gov.nih.nci.cabig.caaers.domain.TreatmentAssignment;
import gov.nih.nci.cabig.caaers.rules.business.service.AdverseEventEvaluationService;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.migrator.adverseevent.AdverseEventConverter;
import gov.nih.nci.cabig.caaers.service.migrator.adverseevent.ParticipantCriteriaConverter;
import gov.nih.nci.cabig.caaers.service.migrator.adverseevent.StudyCriteriaConverter;
import gov.nih.nci.cabig.caaers.tools.mail.CaaersJavaMailSender;
import gov.nih.nci.cabig.caaers.utils.DateUtils;
import gov.nih.nci.cabig.caaers.validation.ValidationError;
import gov.nih.nci.cabig.caaers.validation.ValidationErrors;
import gov.nih.nci.cabig.caaers.webservice.adverseevent.AdverseEventType;
import gov.nih.nci.cabig.caaers.webservice.adverseevent.AdverseEvents;
import gov.nih.nci.cabig.caaers.webservice.adverseevent.CaaersServiceResponse;
import gov.nih.nci.cabig.caaers.webservice.adverseevent.Criteria;
import gov.nih.nci.cabig.caaers.webservice.adverseevent.ImportAdverseEvents;
import gov.nih.nci.cabig.caaers.webservice.adverseevent.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.semanticbits.rules.impl.BusinessRulesExecutionServiceImpl;

@WebService(endpointInterface="gov.nih.nci.cabig.caaers.api.AdverseEventManagementService", serviceName="AdverseEventManagementService")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class AdverseEventManagementServiceImpl implements AdverseEventManagementService , ApplicationContextAware{
	
	protected BusinessRulesExecutionServiceImpl executionService;
	protected AdverseEventDao adverseEventDao;
	protected ParticipantDao participantDao;
	protected StudyDao studyDao;
	protected StudyParticipantAssignmentDao studyParticipantAssignmentDao;
	protected AdverseEventReportingPeriodDao adverseEventReportingPeriodDao;
	protected TreatmentAssignmentDao treatmentAssignmentDao;
	protected CtcTermDao ctcTermDao;
	
	private ParticipantCriteriaConverter participantCriteriaConverter;
	private StudyCriteriaConverter studyCriteriaConverter;
	private AdverseEventConverter adverseEventConverter;
	private AdverseEventEvaluationService adverseEventEvaluationService;
	private CaaersJavaMailSender caaersJavaMailSender;
	private ApplicationContext applicationContext;
	
	private static Log logger = LogFactory.getLog(AdverseEventManagementServiceImpl.class);

	private CaaersServiceResponse saveAdverseEvent(ImportAdverseEvents importAdverseEvents,String operation) {

		CaaersServiceResponse caaersServiceResponse = new CaaersServiceResponse();
		Response adverseEventResponse = new Response();
		DomainObjectImportOutcome<AdverseEvent> adverseEventImportOutcome = null;
		//add participant to criteria
		Participant dbParticipant = null;
		Study dbStudy = null;
		List messages = new ArrayList();
		Criteria criteria = importAdverseEvents.getCriteria();
		AdverseEvents xmlAdverseEvents = importAdverseEvents.getAdverseEvents();
		if (criteria.getParticipantIdentifier() != null) {
			//if (criteria.getParticipant() != null) {
			try {
				//Participant participant = processParticipantCriteria(criteria.getParticipantIdentifier());
				dbParticipant = fetchParticipant(criteria.getParticipantIdentifier());
        		if(dbParticipant != null){
        			logger.info("Participant Exists in caAERS");
        		}else{
        			logger.error("Participant Does not exist ");
    				adverseEventImportOutcome.addErrorMessage("Participant Does not exist in caAERS " , DomainObjectImportOutcome.Severity.ERROR);
    				adverseEventResponse.setResponsecode("1");
    				adverseEventResponse.setDescription("Participant Does not exist in caAERS ");
        		}
			} catch (CaaersSystemException e){
				adverseEventImportOutcome = new DomainObjectImportOutcome<AdverseEvent>();
				logger.error("Participant Criteria to ParticipantDomain Conversion Failed " , e);
				adverseEventImportOutcome.addErrorMessage("Participant Criteria to ParticipantDomain Conversion Failed " , DomainObjectImportOutcome.Severity.ERROR);
				adverseEventResponse.setResponsecode("1");
				adverseEventResponse.setDescription("Participant Criteria to ParticipantDomain Conversion Failed ");
			}
			
		}		
		//add criteria to criteria
		if (criteria.getStudyIdentifier() != null) {
			//if (criteria.getStudy() != null) {
			try {
				//Study study = processStudyCriteria(criteria.getStudy());
				dbStudy = fetchStudy(criteria.getStudyIdentifier());
        		if(dbStudy != null){
        			logger.info("Study Exists in caAERS");
        		}else{
        			logger.error("Study Does not exist ");
    				adverseEventImportOutcome.addErrorMessage("Study Does not exist in caAERS " , DomainObjectImportOutcome.Severity.ERROR);
    				adverseEventResponse.setResponsecode("1");
    				adverseEventResponse.setDescription("Study Does not exist in caAERS ");
    				caaersServiceResponse.setResponse(adverseEventResponse);
    				return caaersServiceResponse;
        		}
			} catch (CaaersSystemException e){
				adverseEventImportOutcome = new DomainObjectImportOutcome<AdverseEvent>();
				logger.error("Study Criteria to StudyDomain Conversion Failed " , e);
				adverseEventImportOutcome.addErrorMessage("Study Criteria to StudyDomain Conversion Failed " , DomainObjectImportOutcome.Severity.ERROR);
				adverseEventResponse.setResponsecode("1");
				adverseEventResponse.setDescription("Study Criteria to StudyDomain Conversion Failed ");
				caaersServiceResponse.setResponse(adverseEventResponse);
				return caaersServiceResponse;
			}
		}
		StudyParticipantAssignment assignment = null;
		
		if (dbParticipant != null && dbStudy != null) {
			// process adverse events ...
			// check for assignment .
			assignment = studyParticipantAssignmentDao.getAssignment(dbParticipant, dbStudy);
			if(assignment != null){
    			logger.info("Participant is  assigned to Study");
    		}else{
    			logger.error("Participant is not assigned to Study ");
				adverseEventImportOutcome.addErrorMessage("Participant is not assigned to Study " , DomainObjectImportOutcome.Severity.ERROR);
				adverseEventResponse.setResponsecode("1");
				adverseEventResponse.setDescription("Participant is not assigned to Study ");
				caaersServiceResponse.setResponse(adverseEventResponse);
				return caaersServiceResponse;
    		}
			AdverseEventReportingPeriod adverseEventReportingPeriod = null;
			if (criteria.getCourse() != null) {
				try {
					adverseEventReportingPeriod = getReportingPeriod(criteria, assignment, operation);
				} catch (CaaersSystemException e) {
		    		messages.add(e.getMessage());
		    		adverseEventResponse.setMessage(messages);
					caaersServiceResponse.setResponse(adverseEventResponse);
					return caaersServiceResponse;
				}
			}
			
			//
			ExpeditedAdverseEventReport aeReport = new ExpeditedAdverseEventReport();
			for (AdverseEventType adverseEventType:xmlAdverseEvents.getAdverseEvent()) {
				try {
					AdverseEvent adverseEvent = processAdverseEvent(adverseEventType,adverseEventReportingPeriod,operation);

					aeReport.addAdverseEvent(adverseEvent);
				} catch (CaaersSystemException e) {
					messages.add(e.getMessage());
					adverseEventResponse.setMessage(messages);
					caaersServiceResponse.setResponse(adverseEventResponse);
					return caaersServiceResponse;
				}
			}
			ValidationErrors errors = fireRules(aeReport,"gov.nih.nci.cabig.caaers.rules.reporting_basics_section");
			/* FIRE SAE RULES ..
			Study initializedStudy = studyDao.initialize(dbStudy);
			for (AdverseEvent ae:aeReport.getAdverseEvents()) {
				try {
					String reportDefinition = adverseEventEvaluationService.assesAdverseEvent(ae, initializedStudy);
					messages.add(reportDefinition);
				} catch (Exception e) {
					messages.add("Error in firing SAE rules . "+e.getMessage());
					adverseEventResponse.setMessage(messages);
					caaersServiceResponse.setResponse(adverseEventResponse);
					return caaersServiceResponse;
				}
			}
			*/
			try {
				notifyStudyPersonnel(assignment);
			} catch (Exception e) {
				messages.add(e.getMessage());
			}
			
			
			if (errors.getErrorCount() > 0) {
				for (ValidationError error:errors.getErrors()) {
					messages.add(error.getMessage());
				}
			} else {
				//SAVE AEs
				
				for (AdverseEvent ae:aeReport.getAdverseEvents()){
					adverseEventReportingPeriod.addAdverseEvent(ae);
				}
				
				//if(operation.equals(DELETE)) {						
					//dbAdverseEvents.remove(ae);	
					//adverseEventDao.
				//}	else {	
				if (operation.equals(CREATE) || operation.equals(UPDATE)) {
					adverseEventReportingPeriodDao.save(adverseEventReportingPeriod);
				}
				List<AdverseEvent> dbAdverseEvents = adverseEventReportingPeriod.getAdverseEvents();

				if(operation.equals(DELETE)) {	
					for (AdverseEvent ae:aeReport.getAdverseEvents()){
						for (int i=0;i<adverseEventReportingPeriod.getAdverseEvents().size();i++) {
							AdverseEvent dbAE = adverseEventReportingPeriod.getAdverseEvents().get(i);
							if (dbAE.getId() == ae.getId()) {
								dbAdverseEvents.remove(i);
							}
						}
					}					
					adverseEventReportingPeriod.setAdverseEvents(dbAdverseEvents);
					adverseEventReportingPeriodDao.save(adverseEventReportingPeriod);		 

				}
//				 adding messages for tests
				for (AdverseEvent ae:adverseEventReportingPeriod.getAdverseEvents()){
					messages.add(ae.getId()+"");
				//	messages.add("Adverse Event Added Succyfully " + ae.getAdverseEventTerm().getTerm());
				}
				
			}
			adverseEventResponse.setMessage(messages);
			//System.out.println(errors.getgetErrors().get(0).getMessage());
			//adverseEventResponse.s
		}
		caaersServiceResponse.setResponse(adverseEventResponse);
		return caaersServiceResponse;
	}
	private void notifyStudyPersonnel(StudyParticipantAssignment assignment) throws Exception {
		List<String> emails = new ArrayList<String>();
		List<StudyPersonnel> studyPersonnel = assignment.getStudySite().getStudyPersonnels();
		for (StudyPersonnel sp:studyPersonnel) {
			String email = sp.getResearchStaff().getEmailAddress();
			emails.add(email);
		}
		String content = "report email test ... ";
		caaersJavaMailSender.sendMail(emails.toArray(new String[0]), "Reporting required for exported Adverse Events", content,new String[0]);
		
	}
	private AdverseEventReportingPeriod getReportingPeriod(Criteria criteria,StudyParticipantAssignment assignment,String operation) throws CaaersSystemException {
		AdverseEventReportingPeriod adverseEventReportingPeriod = null;

		if (criteria.getCourse() != null) {
	        Date xmlStartDate = criteria.getCourse().getStartDateOfThisCourse().toGregorianCalendar().getTime();
	        Date xmlEndDate = null;
	        if (criteria.getCourse().getEndDateOfThisCourse() != null) {
	        	xmlEndDate = criteria.getCourse().getEndDateOfThisCourse().toGregorianCalendar().getTime();
	        }
	        
			List<AdverseEventReportingPeriod> rPeriodList = adverseEventReportingPeriodDao.getByAssignment(assignment);
			for (AdverseEventReportingPeriod aerp : rPeriodList) {
	        	Date sDate = aerp.getStartDate();
	            Date eDate = aerp.getEndDate();
	            if (xmlStartDate.equals(sDate) ) {
		            if ((xmlEndDate != null && xmlEndDate == eDate) || (xmlEndDate == null && eDate == null)) {
	            			adverseEventReportingPeriod = aerp;
	            			break;
	            	}
	            }
	            /*
	            if (xmlStartDate.equals(sDate) ) {
	            	if (xmlEndDate != null) {
	            		if (xmlEndDate.equals(eDate)) {
	            			adverseEventReportingPeriod = aerp;
	            			break;
	            		}
	            	} else {
	            		if (eDate == null) {
	            			adverseEventReportingPeriod = aerp;
	            			break;	            			
	            		}
	            	}
	            }*/
			}
			
			// incase of update , the reporting period shud be existing in database . 
			
			Study study = assignment.getStudySite().getStudy();
			if (adverseEventReportingPeriod == null) {
				
				if (operation.equals(UPDATE) || operation.equals(DELETE)) {
					throw new CaaersSystemException("Course/ Cycle doesn't exist .." );
				}
				
				//create one 
				adverseEventReportingPeriod = new AdverseEventReportingPeriod();
		    	//reportingPeriod.setDescription(criteria.getCourse().getd);
		    	adverseEventReportingPeriod.setStartDate(xmlStartDate);
		    	adverseEventReportingPeriod.setEndDate(xmlEndDate);
		    	if (criteria.getCourse().getTreatmentAssignment() == null) {
		    		adverseEventReportingPeriod.setTreatmentAssignmentDescription(criteria.getCourse().getOtherTreatmentAssignmentDescription());
		    	} else {
		    		TreatmentAssignment ta = treatmentAssignmentDao.getAssignmentsByStudyIdExactMatch(criteria.getCourse().getTreatmentAssignment().getCode(), study.getId());
		    		if (ta != null) {
		    			adverseEventReportingPeriod.setTreatmentAssignment(ta);
			    	} else {
			    		throw new CaaersSystemException("TreatmentAssignment is not valid - " + criteria.getCourse().getTreatmentAssignment().getCode());
			    	}
		    	}
		    	
		    	
		    	List<Epoch> epochs = study.getEpochs();
		    	Epoch epochToSave = null;
		    	for (Epoch epoch:epochs) {
		    		if (epoch.getName().equals(criteria.getCourse().getTreatmentType())) {
		    			epochToSave = epoch;
		    			break;
		    		}
		    	}
		    	if (epochToSave == null) {
		    		throw new CaaersSystemException("TreatmentType is not valid - " + criteria.getCourse().getTreatmentType());
		    	} else {
		    		adverseEventReportingPeriod.setEpoch(epochToSave);
		    	}
		    	
		    	adverseEventReportingPeriod.setAssignment(assignment);
		    	List<String> errors = validateRepPeriodDates(adverseEventReportingPeriod,rPeriodList,assignment.getStartDateOfFirstCourse());
		    	if (errors.size() == 0) {
		    		adverseEventReportingPeriodDao.save(adverseEventReportingPeriod);
		    	} else {
		    		throw new CaaersSystemException(errors.get(0));
		    	}
			}			
		}	
		return adverseEventReportingPeriod;
	}

 
	private AdverseEvent processAdverseEvent(AdverseEventType xmlAdverseEvent,
					AdverseEventReportingPeriod adverseEventReportingPeriod, String operation) throws CaaersSystemException{
		logger.info("Entering processAdverseEvent() in AdverseEventManagementServiceImpl");		
		
		AdverseEvent adverseEvent = null;
		
		//if update get the adverse event to update ..
		List<AdverseEvent> dbAdverseEvents = getAdverseEventDao().getByAssignment(adverseEventReportingPeriod.getAssignment());
		if (operation.equals(UPDATE) || operation.equals(DELETE)) {			
			for (AdverseEvent dbAdverseEvent:dbAdverseEvents) {
				if (adverseEventReportingPeriod.getStartDate() == dbAdverseEvent.getReportingPeriod().getStartDate()) {
					
					if ((adverseEventReportingPeriod.getEndDate() != null && adverseEventReportingPeriod.getEndDate() == dbAdverseEvent.getReportingPeriod().getEndDate())
							|| (adverseEventReportingPeriod.getEndDate() == null && dbAdverseEvent.getReportingPeriod().getEndDate() == null) ) {
							//ctc or meddra
							if (dbAdverseEvent.getAdverseEventTerm() instanceof AdverseEventCtcTerm) {
								AdverseEventCtcTerm aeCtcTerm = (AdverseEventCtcTerm)dbAdverseEvent.getAdverseEventTerm();
								if (aeCtcTerm.getCtcTerm().getTerm().equals(xmlAdverseEvent.getAdverseEventCtcTerm().getCtepTerm())) {
									adverseEvent = dbAdverseEvent;
									break;
								}
							} else if (dbAdverseEvent.getAdverseEventTerm() instanceof AdverseEventMeddraLowLevelTerm) {
								AdverseEventMeddraLowLevelTerm aeMeddraLowLevelTerm = (AdverseEventMeddraLowLevelTerm)dbAdverseEvent.getAdverseEventTerm();
								if (aeMeddraLowLevelTerm.getLowLevelTerm().getMeddraCode().equals(xmlAdverseEvent.getAdverseEventMeddraLowLevelTerm().getMeddraCode()) &&
										aeMeddraLowLevelTerm.getLowLevelTerm().getMeddraTerm().equals(xmlAdverseEvent.getAdverseEventMeddraLowLevelTerm().getMeddraTerm()) ) {
									adverseEvent = dbAdverseEvent;
									break;
								}								
							}					
					}
					/*
					if (adverseEventReportingPeriod.getEndDate() != null ) {
						if (adverseEventReportingPeriod.getEndDate().equals(dbAdverseEvent.getReportingPeriod().getEndDate())) {
							if (dbAdverseEvent.getAdverseEventTerm() instanceof AdverseEventCtcTerm) {
								AdverseEventCtcTerm aeCtcTerm = (AdverseEventCtcTerm)dbAdverseEvent.getAdverseEventTerm();
								if (aeCtcTerm.getCtcTerm().getTerm().equals(xmlAdverseEvent.getAdverseEventCtcTerm().getCtepTerm())) {
									adverseEvent = dbAdverseEvent;
									break;
								}
							} else if (dbAdverseEvent.getAdverseEventTerm() instanceof AdverseEventMeddraLowLevelTerm) {
								AdverseEventMeddraLowLevelTerm aeMeddraLowLevelTerm = (AdverseEventMeddraLowLevelTerm)dbAdverseEvent.getAdverseEventTerm();
								if (aeMeddraLowLevelTerm.getLowLevelTerm().getMeddraCode().equals(xmlAdverseEvent.getAdverseEventMeddraLowLevelTerm().getMeddraCode()) &&
										aeMeddraLowLevelTerm.getLowLevelTerm().getMeddraTerm().equals(xmlAdverseEvent.getAdverseEventMeddraLowLevelTerm().getMeddraTerm()) ) {
									adverseEvent = dbAdverseEvent;
									break;
								}								
							}
						}
					} else {
						if (dbAdverseEvent.getReportingPeriod().getEndDate() == null) {
							if (dbAdverseEvent.getAdverseEventTerm() instanceof AdverseEventCtcTerm) {
								AdverseEventCtcTerm aeCtcTerm = (AdverseEventCtcTerm)dbAdverseEvent.getAdverseEventTerm();
								if (aeCtcTerm.getCtcTerm().getTerm().equals(xmlAdverseEvent.getAdverseEventCtcTerm().getCtepTerm())) {
									adverseEvent = dbAdverseEvent;
									break;
								}
							} else if (dbAdverseEvent.getAdverseEventTerm() instanceof AdverseEventMeddraLowLevelTerm) {
								AdverseEventMeddraLowLevelTerm aeMeddraLowLevelTerm = (AdverseEventMeddraLowLevelTerm)dbAdverseEvent.getAdverseEventTerm();
								if (aeMeddraLowLevelTerm.getLowLevelTerm().getMeddraCode().equals(xmlAdverseEvent.getAdverseEventMeddraLowLevelTerm().getMeddraCode()) &&
										aeMeddraLowLevelTerm.getLowLevelTerm().getMeddraTerm().equals(xmlAdverseEvent.getAdverseEventMeddraLowLevelTerm().getMeddraTerm()) ) {
									adverseEvent = dbAdverseEvent;
									break;
								}								
							}						
						}
					}*/
				}
			}
		} else if (operation.equals(CREATE)) {
			adverseEvent = new AdverseEvent();
			adverseEvent.setReportingPeriod(adverseEventReportingPeriod);
		} 
		if (adverseEvent == null) {
			throw new CaaersSystemException("Unable to update Adverse Event , AE not found  ") ;
		}
		if (operation.equals(DELETE)) {
			return adverseEvent;			
		}
		
		try{			
			adverseEventConverter.convertAdverseEventDtoToAdverseEventDomain(xmlAdverseEvent, adverseEvent, operation);
        }catch(CaaersSystemException caEX){
         	throw new CaaersSystemException(caEX);
        }
        return adverseEvent;
 	}
	
	   private List<String> validateRepPeriodDates(AdverseEventReportingPeriod rPeriod, List<AdverseEventReportingPeriod> rPeriodList, Date firstCourseDate) {

	        Date startDate = rPeriod.getStartDate();
	        Date endDate = rPeriod.getEndDate();
	        List<String> errors = new ArrayList<String>();

	        // Check if the start date is equal to or before the end date.
	        if (firstCourseDate != null && startDate != null && (firstCourseDate.getTime() - startDate.getTime() > 0)) {
	            errors.add("Start date of this course/cycle cannot be earlier than the Start date of first course/cycle");
	        }

	        if (startDate != null && endDate != null && (endDate.getTime() - startDate.getTime() < 0)) {
	            errors.add("Course End date cannot be earlier than Start date");
	        }

	        // Check if the start date is equal to end date.
	        // This is allowed only for Baseline reportingPeriods and not for other reporting periods.
	        if (!rPeriod.getEpoch().getName().equals("Baseline")) {
	            if (endDate != null && startDate.equals(endDate)) {
	                errors.add("For Non-Baseline treatment type Start date cannot be equal to End date");
	            }

	        }

	        // Check if the start date - end date for the reporting Period overlaps with the
	        // date range of an existing Reporting Period.
	        for (AdverseEventReportingPeriod aerp : rPeriodList) {
	        	Date sDate = aerp.getStartDate();
	            Date eDate = aerp.getEndDate();
	            
	            if (!aerp.getId().equals(rPeriod.getId())) {
	                
	                //we should make sure that no existing Reporting Period, start date falls, in-between these dates.
	                if(startDate != null && endDate != null){
	                	if(DateUtils.compareDate(sDate, startDate) >= 0 && DateUtils.compareDate(sDate, endDate) < 0){
	                		errors.add("Course/cycle cannot overlap with an existing course/cycle.");
	                		break;
	                	}
	                }else if(startDate != null && DateUtils.compareDate(sDate, startDate) == 0){
	                		errors.add("Course/cycle cannot overlap with an existing course/cycle.");
	                		break;
	                }
	                
	                //newly created reporting period start date, should not fall within any other existing reporting periods
	                if(sDate != null && eDate != null){
	                	if(DateUtils.compareDate(sDate, startDate) <=0 && DateUtils.compareDate(startDate, eDate) < 0){
	                		errors.add("Course/cycle cannot overlap with an existing course/cycle.");
	                		break;
	                	}
	                }else if(sDate != null && DateUtils.compareDate(sDate, startDate) == 0){
	                	errors.add("Course/cycle cannot overlap with an existing course/cycle.");
	            		break;
	                }
	            }
	            
	            // If the epoch of reportingPeriod is not - Baseline , then it cannot be earlier than a Baseline
	            if (rPeriod.getEpoch().getName().equals("Baseline")) {
	                if (!aerp.getEpoch().getName().equals("Baseline")) {
	                    if (DateUtils.compareDate(sDate, startDate) < 0) {
	                        errors.add("Baseline treatment type cannot start after an existing Non-Baseline treatment type.");
	                        return errors;
	                    }
	                }
	            } else {
	                if (aerp.getEpoch().getName().equals("Baseline")) {
	                    if (DateUtils.compareDate(startDate, sDate) < 0) {
	                        errors.add("Non-Baseline treatment type cannot start before an existing Baseline treatment type.");
	                        return errors;
	                    }
	                }
	            }
	            
	        }
	        return errors;
	      
	    }

	    
/*	
	private Study processStudyCriteria(StudyType xmlStudy) throws CaaersSystemException{
		logger.info("Entering processStudyCriteria() in AdverseEventManagementServiceImpl");		
		
		Study study = new Study();
		
		try{
			studyCriteriaConverter.convertStudyDtoToStudyDomain(xmlStudy, study);
        }catch(CaaersSystemException caEX){
         	throw new CaaersSystemException("Study Criteria to StudyDomain Conversion Failed ", caEX);
        }
        return study;
 	}	
	private Participant processParticipantCriteria(ParticipantType xmlParticipant) throws CaaersSystemException{
		logger.info("Entering processParticipant() in AdverseEventManagementServiceImpl");		
		
		Participant participant = new Participant();
		
		try{
			participantCriteriaConverter.convertParticipantDtoToParticipantDomain(xmlParticipant, participant);
        }catch(CaaersSystemException caEX){
         	throw new CaaersSystemException("Participant Criteria to ParticipantDomain Conversion Failed ", caEX);
        }
        return participant;
 	}*/
	public CaaersServiceResponse deleteAdverseEvent(ImportAdverseEvents importAdverseEvents) {
		// TODO Auto-generated method stub
		return saveAdverseEvent(importAdverseEvents,DELETE);
	}

	public CaaersServiceResponse updateAdverseEvent(ImportAdverseEvents importAdverseEvents) {
		return saveAdverseEvent(importAdverseEvents,UPDATE);
	}
	public CaaersServiceResponse createAdverseEvent(ImportAdverseEvents importAdverseEvents) {
		return saveAdverseEvent(importAdverseEvents,CREATE);
	}
	private Participant fetchParticipant(String identifier){
		Participant dbParticipant = null;
		Identifier pi = new Identifier();
		pi.setValue(identifier);
		dbParticipant = participantDao.getByIdentifier(pi);
		if(dbParticipant == null){
			return null;
		}
		participantDao.evict(dbParticipant);		
		/*
		for(Identifier identifier : participant.getIdentifiers()){
			dbParticipant = participantDao.getParticipantDesignByIdentifier(identifier);
			if(dbParticipant != null){
				break;
			}
			participantDao.evict(dbParticipant);
		}
		*/
		return dbParticipant;
	}	
	
	private Study fetchStudy(String identifier){
		Study dbStudy = null;
		Identifier si = new Identifier();
		si.setValue(identifier);
		dbStudy = studyDao.getByIdentifier(si);
        if(dbStudy == null){
        	return null;
        }
        studyDao.evict(dbStudy);

		return dbStudy;
	}

	
	public void setAdverseEventDao(AdverseEventDao adverseEventDao) {
		this.adverseEventDao = adverseEventDao;
	}

	public void setExecutionService(
			BusinessRulesExecutionServiceImpl executionService) {
		this.executionService = executionService;
	}

	public void setParticipantDao(ParticipantDao participantDao) {
		this.participantDao = participantDao;
	}

	public void setStudyDao(StudyDao studyDao) {
		this.studyDao = studyDao;
	}

	public AdverseEventDao getAdverseEventDao() {
		return adverseEventDao;
	}
	
	public BusinessRulesExecutionServiceImpl getExecutionService() {
		return executionService;
	}
	public void setStudyParticipantAssignmentDao(
			StudyParticipantAssignmentDao studyParticipantAssignmentDao) {
		this.studyParticipantAssignmentDao = studyParticipantAssignmentDao;
	}
	private ValidationErrors fireRules(ExpeditedAdverseEventReport aeReport  , String bindUri){

        List<Object> input = new ArrayList<Object>();
        input.add(aeReport);
        ValidationErrors errors = new ValidationErrors();
        input.add(errors);
        
        
        List<Object> output = executionService.fireRules(bindUri, input);
        errors = retrieveValidationErrors(output);
        return errors;
	}
    private ValidationErrors retrieveValidationErrors(List<Object> list) {
        for (Object o : list)
            if (o instanceof ValidationErrors) return (ValidationErrors) o;
        return null;
    }

	public void setAdverseEventConverter(AdverseEventConverter adverseEventConverter) {
		this.adverseEventConverter = adverseEventConverter;
	}

	public void setAdverseEventReportingPeriodDao(
			AdverseEventReportingPeriodDao adverseEventReportingPeriodDao) {
		this.adverseEventReportingPeriodDao = adverseEventReportingPeriodDao;
	}

	public void setTreatmentAssignmentDao(
			TreatmentAssignmentDao treatmentAssignmentDao) {
		this.treatmentAssignmentDao = treatmentAssignmentDao;
	}

	public void setAdverseEventEvaluationService(
			AdverseEventEvaluationService adverseEventEvaluationService) {
		this.adverseEventEvaluationService = adverseEventEvaluationService;
	}
	public void setCaaersJavaMailSender(CaaersJavaMailSender caaersJavaMailSender) {
		this.caaersJavaMailSender = caaersJavaMailSender;
	}
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		this.applicationContext = applicationContext;
		
	}

}
