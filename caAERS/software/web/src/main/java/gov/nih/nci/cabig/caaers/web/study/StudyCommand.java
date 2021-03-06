/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.study;

import gov.nih.nci.cabig.caaers.dao.InvestigationalNewDrugDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.query.StudyQuery;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.repository.StudyRepository;
import gov.nih.nci.cabig.caaers.security.SecurityUtils;
import gov.nih.nci.cabig.caaers.service.AgentSpecificAdverseEventListService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections15.ListUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ion C. Olaru
 * Date: Dec 22, 2008 - 10:44:55 AM
 * @author Biju Joseph
 */

public class StudyCommand {

    private static final Log log = LogFactory.getLog(StudyCommand.class);

    private Study study;
    private int studySiteIndex = -1; 
    private String[] diseaseTermIds;
    private String diseaseCategoryAsText;

    private String diseaseLlt;
    private String condition;
    protected List<ConfigProperty> allPersonnelRoles;
    protected List<ConfigProperty> allInvestigatorRoles;
    protected Map<String, String> studyPersonnelRoles;
    protected Map<String, String> studyInvestigatorRoles;

    private StudyRepository studyRepository;

    private Integer primaryStudyDisease;
    private Integer primaryStudyIdenifier;

    // ----------------------------------------------------------------------------------------------------------------
    
    public static final String STATUS_ADMINISTRATIVELY_COMPLETE = "Administratively Complete";
    public static final String STATUS_ACTIVE = "Active - Trial is open to accrual";

    private String shortTitle;
    private String longTitle;
    private String description;
    private String precis;
    private String phaseCode;
    private AeTerminology aeTerminology;
    private DiseaseTerminology diseaseTerminology;
    private String status;



    private Boolean drugAdministrationTherapyType = Boolean.FALSE;
    private Boolean radiationTherapyType = Boolean.FALSE;
    private Boolean deviceTherapyType = Boolean.FALSE;
    private Boolean surgeryTherapyType = Boolean.FALSE;
    private Boolean behavioralTherapyType = Boolean.FALSE;
    private Boolean biologicalTherapyType = Boolean.FALSE;
    private Boolean geneticTherapyType = Boolean.FALSE;
    private Boolean diaterySupplementTherapyType = Boolean.FALSE;
    private Boolean otherTherapyType = Boolean.FALSE;
    private Integer loadStatus = LoadStatus.COMPLETE.getCode();

    // Used to facilitate import of a coordinating center / funding sponsor
    private FundingSponsor fundingSponsor;
    private CoordinatingCenter coordinatingCenter;

    // DCP specific properties
    private Design design;
    private List<Epoch> epochs=new ArrayList<Epoch>();
    private boolean workflowEnabled;
    private StudyDao studyDao;
    
    private InvestigationalNewDrugDao investigationalNewDrugDao;

    protected StudyCoordinatingCenter prevCC;
    protected StudyFundingSponsor prevFS;

    // SECURITY STUFF
    boolean isSupplementalInfoManager = SecurityUtils.checkAuthorization(UserGroupType.supplemental_study_information_manager);
    boolean isStudyQAManager = SecurityUtils.checkAuthorization(UserGroupType.study_qa_manager);
    boolean isStudyCreator = SecurityUtils.checkAuthorization(UserGroupType.study_creator);

    private boolean mustFireEvent;

    public StudyCommand(StudyDao studyDao, InvestigationalNewDrugDao investigationalNewDrugDao) {
    	this.studyDao = studyDao;
    	this.investigationalNewDrugDao = investigationalNewDrugDao;
        this.studyPersonnelRoles = new HashMap<String, String>();
        this.studyInvestigatorRoles = new HashMap<String, String>();
    }

    // TODO: this stuff should really, really not be in here. It's web-view/entry specific.

    public String getDescription() {
        return description;
    }

    public void setDescription(final String descriptionText) {
        description = descriptionText;
    }

    public String getLongTitle() {
        return longTitle;
    }

    public void setLongTitle(final String longTitleText) {
        longTitle = longTitleText;
    }

    public String getPhaseCode() {
        return phaseCode;
    }

    public void setPhaseCode(final String phaseCode) {
        this.phaseCode = phaseCode;
    }

    public String getPrecis() {
        return precis;
    }

    public void setPrecis(final String precisText) {
        precis = precisText;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(final String shortTitleText) {
        shortTitle = shortTitleText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Integer getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(Integer loadStatus) {
        this.loadStatus = loadStatus;
    }

    @Deprecated
    public Boolean getDrugAdministrationTherapyType() {
        return false;
    }

    @Deprecated
    public void setDrugAdministrationTherapyType(final Boolean drugAdministrationTherapyType) {
        this.drugAdministrationTherapyType = drugAdministrationTherapyType;
    }

    @Deprecated
    public Boolean getRadiationTherapyType() {
        return radiationTherapyType;
    }

    @Deprecated
    public void setRadiationTherapyType(final Boolean radiationTherapyType) {
        this.radiationTherapyType = radiationTherapyType;
    }

    @Deprecated
    public Boolean getDeviceTherapyType() {
        return deviceTherapyType;
    }

    @Deprecated
    public void setDeviceTherapyType(final Boolean deviceTherapyType) {
        this.deviceTherapyType = deviceTherapyType;
    }

    @Deprecated
    public Boolean getSurgeryTherapyType() {
        return surgeryTherapyType;
    }

    @Deprecated
    public void setSurgeryTherapyType(final Boolean surgeryTherapyType) {
        this.surgeryTherapyType = surgeryTherapyType;
    }

    @Deprecated
    public Boolean getBehavioralTherapyType() {
        return behavioralTherapyType;
    }

    @Deprecated
    public void setBehavioralTherapyType(final Boolean behavioralTherapyType) {
        this.behavioralTherapyType = behavioralTherapyType;
    }
    
    @Deprecated
    public Boolean getBiologicalTherapyType() {
		return biologicalTherapyType;
	}

    @Deprecated
    public void setBiologicalTherapyType(Boolean biologicalTherapyType) {
		this.biologicalTherapyType = biologicalTherapyType;
	}

    @Deprecated
    public Boolean getGeneticTherapyType() {
		return geneticTherapyType;
	}

    @Deprecated
    public void setGeneticTherapyType(Boolean geneticTherapyType) {
		this.geneticTherapyType = geneticTherapyType;
	}

    @Deprecated
    public Boolean getDiaterySupplementTherapyType() {
		return diaterySupplementTherapyType;
	}

    @Deprecated
    public void setDiaterySupplementTherapyType(Boolean diaterySupplementTherapyType) {
		this.diaterySupplementTherapyType = diaterySupplementTherapyType;
	}
    
    @Deprecated
    public Boolean getOtherTherapyType() {
		return otherTherapyType;
	}

    @Deprecated
    public void setOtherTherapyType(Boolean otherTherapyType) {
		this.otherTherapyType = otherTherapyType;
	}
    
    @Deprecated
    public boolean isTherapyTypeSelected(StudyTherapyType therapyType){
    	switch (therapyType) {
		case DRUG_ADMINISTRATION:
			return BooleanUtils.isTrue(drugAdministrationTherapyType);
		case BEHAVIORAL:
			return BooleanUtils.isTrue(behavioralTherapyType);
		case BIOLOGICAL_VACCINE:
			return BooleanUtils.isTrue(biologicalTherapyType);
		case DEVICE:
			return BooleanUtils.isTrue(deviceTherapyType);
		case DIETARY_SUPPLEMENT:
			return BooleanUtils.isTrue(diaterySupplementTherapyType);
		case GENETIC:
			return BooleanUtils.isTrue(geneticTherapyType);
		case OTHER:
			return BooleanUtils.isTrue(otherTherapyType);
		case RADIATION:
			return BooleanUtils.isTrue(radiationTherapyType);
		case SURGERY:
			return BooleanUtils.isTrue(surgeryTherapyType);
		default : 
			return false;
		}
    }
    
    public CoordinatingCenter getCoordinatingCenter() {
        return coordinatingCenter;
    }

    public FundingSponsor getFundingSponsor() {
        return fundingSponsor;
    }

    public void setCoordinatingCenter(CoordinatingCenter coordinatingCenter) {
        this.coordinatingCenter = coordinatingCenter;
    }

    public void setFundingSponsor(FundingSponsor fundingSponsor) {
        this.fundingSponsor = fundingSponsor;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
    	this.study= study;
    	//this.study=remoteObjectBinder.bindStudy(study);

    }

    @Transient
    public int getStudySiteIndex() {
        return studySiteIndex; 
    }

    public void setStudySiteIndex(final int studySiteIndex) {
        this.studySiteIndex = studySiteIndex;
    }

    @Transient
    public String[] getDiseaseTermIds() {
        return diseaseTermIds;
    }

    public void setDiseaseTermIds(final String[] diseaseTermIds) {
        this.diseaseTermIds = diseaseTermIds;
    }

    @Transient
    public String getDiseaseCategoryAsText() {
        return diseaseCategoryAsText;
    }

    public void setDiseaseCategoryAsText(final String diseaseCategoryAsText) {
        this.diseaseCategoryAsText = diseaseCategoryAsText;
    }

    @Transient
    public String getDiseaseLlt() {
        return diseaseLlt;
    }

    public void setDiseaseLlt(final String diseaseLlt) {
        this.diseaseLlt = diseaseLlt;
    }

    @Transient
    public String getCondition() {
        return condition;
    }

    public void setCondition(final String condition) {
        this.condition = condition;
    }
    public boolean isWorkflowEnabled() {
		return workflowEnabled;
	}

    
    public void setWorkflowEnabled(boolean workflowEnabled) {
		this.workflowEnabled = workflowEnabled;
	}
    
    /**
     * This will mark datacomplete the study
     */
    public void openStudy(){
    	study.setDataEntryStatus(true);
    	save();
    }
    
    public boolean isDataEntryComplete(){
    	return BooleanUtils.isTrue(study.getDataEntryStatus()); 
    }
    
    public String getDataEntryStatus(){
    	if(isDataEntryComplete()) return "Complete";
    	return "Inprogress";
    }
    
    public StudyDao getStudyDao() {
		return studyDao;
	}
    public void setStudyDao(StudyDao studyDao) {
		this.studyDao = studyDao;
	}
    
    public InvestigationalNewDrugDao getInvestigationalNewDrugDao() {
		return investigationalNewDrugDao;
	}
    
    public void setInvestigationalNewDrugDao(
			InvestigationalNewDrugDao investigationalNewDrugDao) {
		this.investigationalNewDrugDao = investigationalNewDrugDao;
	}
    
    //will fetch the default IND for CTEP 
    public InvestigationalNewDrug fetchDefaultInvestigationalNewDrugForCTEP(){
    	return investigationalNewDrugDao.fetchCtepInd();
    }
    
    //will fetch the default IND for DCP
    public InvestigationalNewDrug fetchDefaultInvestigationalNewDrugForDCP(){
    	return investigationalNewDrugDao.fetchDcpInd();
    }
    
    /**
     * Will delete or mark as retired the study agent object mentioned.
     * @param index
     */
    public StudyAgent deleteStudyAgentAtIndex(int index){
    	StudyAgent studyAgent = getStudy().getStudyAgents().get(index);
        studyAgent.retire();
    	return studyAgent;
    }
    
    /**
     * Will delete or mark as retired the treatment assignment object mentioned.
     * @param index
     */
    public void deleteTreatmentAssignmentAtIndex(int index){
        List<TreatmentAssignment> treatmentAssignments = study.getTreatmentAssignments();
        if(treatmentAssignments.size() > index){
            treatmentAssignments.get(index).retire();
        }
    }
    
    /**
     * Will delete or mark as retired the study site at specified index.
     * @param index
     */
    public void deleteStudySiteAtIndex(int index){
        List<StudySite> sites = study.getStudySites();
        if(sites.size() > index){
            StudySite ss = sites.get(index);
            ss.retire();

            if(ss.getOrganization() == null || ss.getId() == null)  {
            	sites.remove(index);
            }
        }
    }
    
    /**
     * Will delete or mark as retired the {@link CtepStudyDisease} at specified index.
     * @param index
     */
    public void deleteCtepStudyDiseaseAtIndex(int index){
    	delete(study.getActiveCtepStudyDiseases(), index);
    }
    

    /**
     * Will delete or mark as retired the {@link MeddraStudyDisease} at specified index.
     * @param index
     */
    public void deleteMeddraStudyDiseaseAtIndex(int index){
    	delete(study.getActiveMeddraStudyDiseases(), index);
    }

    /**
     * Will delete or mark as retired the {@link StudyCondition} at specified index.
     * @param index
     */
    public void deleteStudyConditionAtIndex(int index){
    	delete(study.getActiveStudyConditions(), index);
    }
    
    /**
     * Will delete or mark as retired the {@link SiteInvestigator} at specified index.
     * @param index
     */
    public void deleteSiteInvestigatorAtIndex(int studyOrgIndex, int index){
        List<StudyInvestigator> investigators = study.getActiveStudyOrganizations().get(studyOrgIndex).getStudyInvestigators();
        if(investigators.size() > index){
            investigators.remove(index);
        }
    }
    
    /**
     * Will delete or mark as retired the {@link StudyPersonnel} at specified index.
     * @param studyOrgIndex
     * @param index
     */
    public void deleteStudyPersonAtIndex(int studyOrgIndex, int index){
        List<StudyPersonnel> personnelList = study.getActiveStudyOrganizations().get(studyOrgIndex).getStudyPersonnels();
        if(personnelList.size() > index){
            personnelList.remove(index);
        }
    }
    
    /**
     * This base method will delete or mark retire a retire-able object. 
     * @param source
     * @param index
     */
    public void delete(List<? extends Retireable> source, int index){
    	int size = source.size();
    	int actualIndex = index;
    	
    	for(int i = 0; i < size & actualIndex < size; i++){
    		Retireable o = source.get(i);
    		if(actualIndex == i){
    			if(o.getId() == null) {
    				source.remove(actualIndex);
    			}else{
    				o.retire();
    			}
    			return;
    		}else{
    			if(o.isRetired()) actualIndex++;
    		}
    		
    	}
    	
    }
   
   
    public void reloadStudy(){
    	Integer oldStudyId = study.getId();
        Study loaded = studyDao.getById(oldStudyId);
    	setStudy(loaded);
    }
    
    private TreatmentAssignment getTreatmentAssignmentFromStudy(Study study, TreatmentAssignment treatmentAssignment){
    	if(treatmentAssignment.getId() == null) return treatmentAssignment;
    	for(TreatmentAssignment ta : study.getTreatmentAssignments()){
    		if(ta.getId().equals(treatmentAssignment.getId())) return ta;
    	}
    	return null;
    }
    
    private StudyIntervention getStudyInterventionFromStudy(Study study, StudyIntervention studyIntervention){
    	List<? extends StudyIntervention> studyInterventions = null;
    	if (studyIntervention instanceof StudyAgent) {
    		studyInterventions = study.getStudyAgents();
		}else if (studyIntervention instanceof StudyDevice) {
    		studyInterventions = study.getStudyDevices();
		}else{
			studyInterventions = study.getOtherInterventions();
		}
    	for(StudyIntervention si : studyInterventions){
    		if(si.getId().equals(studyIntervention.getId())) return si;
    	}
    	return null;
    }
    
    /**
     * This method will save a study into the DB
     */
    public void save(){
        //save the study by merging it.
        studyRepository.save(study);
    }

    public List<ConfigProperty> getAllPersonnelRoles() {
        return allPersonnelRoles;
    }

    public void setAllPersonnelRoles(List<ConfigProperty> allPersonnelRoles) {
        this.allPersonnelRoles = allPersonnelRoles;
    }

    public List<ConfigProperty> getAllInvestigatorRoles() {
        return allInvestigatorRoles;
    }

    public void setAllInvestigatorRoles(List<ConfigProperty> allInvestigatorRoles) {
        this.allInvestigatorRoles = allInvestigatorRoles;
    }

    public void populateRoleNamesMap() {
        for (ConfigProperty pr : getAllPersonnelRoles()) {
            this.studyPersonnelRoles.put(pr.getCode(), pr.getName());
        }
        for (ConfigProperty pr : getAllInvestigatorRoles()) {
            this.studyInvestigatorRoles.put(pr.getCode(), pr.getName());
        }
    }

    public Map<String, String> getStudyPersonnelRoles() {
        return studyPersonnelRoles;
    }

    public void setStudyPersonnelRoles(Map<String, String> studyPersonnelRoles) {
        this.studyPersonnelRoles = studyPersonnelRoles;
    }

    public Map<String, String> getStudyInvestigatorRoles() {
        return studyInvestigatorRoles;
    }

    public void setStudyInvestigatorRoles(Map<String, String> studyInvestigatorRoles) {
        this.studyInvestigatorRoles = studyInvestigatorRoles;
    }
    
    public Study checkForDuplicateStudyByIdentifier(Identifier identifier){
    	StudyQuery query = new StudyQuery();
        query.joinIdentifier();
        query.filterByIdentifier(identifier);
        
        if(study.getId() != null){
    	   query.ignoreStudyById(study.getId());
        }
        List<Study> studies = studyDao.find(query);
        
        if(CollectionUtils.isNotEmpty(studies)){
        	return studies.get(0);
        }
        return null;
    }

    public void synchronizeStudyWithAgentAEList(AgentSpecificAdverseEventListService service, Study s, boolean deleted) {

        if (s.hasLeadCTEPInds()) {

            // delete all not needed
            for (StudyAgent sa : s.getStudyAgents()) {
                if (sa == null || sa.getAgent() == null || sa.isRetired()) continue;
                if (!sa.isCTEPLead())
                    service.synchronizeStudyWithAgent(sa, AgentSpecificTerm.EXPTECTED_AE_DELETED);
            }

            // synchronzie the remaining agents
            for (StudyAgent sa : s.getStudyAgents()) {
                if (sa == null || sa.getAgent() == null || sa.isRetired()) continue;
                if (sa.isCTEPLead())
                	service.synchronizeStudyWithAgent(sa, AgentSpecificTerm.EXPTECTED_AE_ADDED);
            }

        } else {
            // synchronzie all agents
            for (StudyAgent sa : s.getStudyAgents()) {
                if (sa == null || sa.getAgent() == null || sa.isRetired()) continue;
                service.synchronizeStudyWithAgent(sa, AgentSpecificTerm.EXPTECTED_AE_ADDED);
            }
        }
    }

    public List<StudyIntervention> getAllActiveInterventions(){
        List<StudyIntervention>  activeInterventions = new ArrayList<StudyIntervention>();
        activeInterventions.addAll(getStudy().getActiveStudyAgents());
        activeInterventions.addAll(getStudy().getActiveStudyDevices());
        activeInterventions.addAll(getStudy().getActiveOtherInterventions());
        return activeInterventions;
    }
    public void populateTreatmentAssignmentSelectedStudyInterventionIds(){
       if(getStudy() == null) return;
       for(TreatmentAssignment ta : getStudy().getActiveTreatmentAssignments()) {
           ta.regenerateAllInterventionIdList();
       }
    }

    public void synchronizeTreatmentAssignmentSelectedInterventions(){
        for(TreatmentAssignment treatmentAssignment : study.getActiveTreatmentAssignments()){
            //all selected IDs
            List<Integer> studyAgentIds = new ArrayList<Integer>(treatmentAssignment.getSelectedStudyAgentInterventionIds());
            List<Integer> studyDeviceIds =  new ArrayList<Integer>(treatmentAssignment.getSelectedStudyDeviceInterventionIds());
            List<Integer> otherInterventionIds =  new ArrayList<Integer>(treatmentAssignment.getSelectedOtherInteterventionIds());

            //exiting association ids
            treatmentAssignment.regenerateAllInterventionIdList();
            List<Integer> existingStudyAgentIds = treatmentAssignment.getSelectedStudyAgentInterventionIds();
            List<Integer> existingStudyDeviceIds = treatmentAssignment.getSelectedStudyDeviceInterventionIds();
            List<Integer> existingOtherInterventionIds = treatmentAssignment.getSelectedOtherInteterventionIds();

            //object ids to remove
            List<Integer> unwantedStudyAgentIds = ListUtils.subtract(existingStudyAgentIds, studyAgentIds);
            List<Integer> unwantedStudyDeviceIds = ListUtils.subtract(existingStudyDeviceIds, studyDeviceIds);
            List<Integer> unwantedOtherInterventionIds = ListUtils.subtract(existingOtherInterventionIds, otherInterventionIds);

            //object ids to add
            List<Integer> newStudyAgentIds = ListUtils.subtract(studyAgentIds,existingStudyAgentIds);
            List<Integer> newStudyDeviceIds = ListUtils.subtract(studyDeviceIds,existingStudyDeviceIds);
            List<Integer> newOtherInterventionIds = ListUtils.subtract(otherInterventionIds, existingOtherInterventionIds);

            //remove the unwanted ones 
            for(Integer id : unwantedStudyAgentIds){
                StudyAgent studyAgent = study.findStudyAgentById(id);
                treatmentAssignment.removeInterventionFromTreatmentAssignment(studyAgent);
            }
            for(Integer id : unwantedStudyDeviceIds){
                StudyDevice studyDevice = study.findStudyDeviceById(id);
                treatmentAssignment.removeInterventionFromTreatmentAssignment(studyDevice);
            }
            for(Integer id : unwantedOtherInterventionIds){
                OtherIntervention otherIntervention = study.findOtherInterventionById(id);
                treatmentAssignment.removeInterventionFromTreatmentAssignment(otherIntervention);
            }


            //add the new ones
            for(Integer id : newStudyAgentIds) {
                StudyAgent studyAgent = study.findStudyAgentById(id);
                if(studyAgent != null) treatmentAssignment.addInterventionToTreatmentAssignment(studyAgent);
            }
            for(Integer id : newStudyDeviceIds){
                StudyDevice studyDevice = study.findStudyDeviceById(id);
                if(studyDevice != null) treatmentAssignment.addInterventionToTreatmentAssignment(studyDevice);
            }
            for(Integer id : newOtherInterventionIds){
                OtherIntervention otherIntervention = study.findOtherInterventionById(id);
                if(otherIntervention != null) treatmentAssignment.addInterventionToTreatmentAssignment(otherIntervention);
            }
        }
    }


    public StudyRepository getStudyRepository() {
        return studyRepository;
    }

    public void setStudyRepository(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    public Integer getPrimaryStudyDisease() {
        return primaryStudyDisease;
    }

    public void setPrimaryStudyDisease(Integer primaryStudyDisease) {
        this.primaryStudyDisease = primaryStudyDisease;
    }

    public StudyCoordinatingCenter getPrevCC() {
        return prevCC;
    }

    public void setPrevCC(StudyCoordinatingCenter prevCC) {
        this.prevCC = prevCC;
    }

    public StudyFundingSponsor getPrevFS() {
        return prevFS;
    }

    public void setPrevFS(StudyFundingSponsor prevFS) {
        this.prevFS = prevFS;
    }

    public boolean getSupplementalInfoManager() {
        return isSupplementalInfoManager;
    }

    public void setSupplementalInfoManager(boolean supplementalInfoManager) {
        isSupplementalInfoManager = supplementalInfoManager;
    }

    public boolean getStudyQAManager() {
        return isStudyQAManager;
    }

    public void setStudyQAManager(boolean studyQAManager) {
        isStudyQAManager = studyQAManager;
    }

    public boolean getStudyCreator() {
        return isStudyCreator;
    }

    public void setStudyCreator(boolean studyCreator) {
        isStudyCreator = studyCreator;
    }

    public boolean isMustFireEvent() {
        return mustFireEvent;
    }

    public void setMustFireEvent(boolean mustFireEvent) {
        this.mustFireEvent = mustFireEvent;
    }

    public Integer getPrimaryStudyIdenifier() {
        return primaryStudyIdenifier;
    }

    public void setPrimaryStudyIdenifier(Integer primaryStudyIdenifier) {
        this.primaryStudyIdenifier = primaryStudyIdenifier;
    }

}
