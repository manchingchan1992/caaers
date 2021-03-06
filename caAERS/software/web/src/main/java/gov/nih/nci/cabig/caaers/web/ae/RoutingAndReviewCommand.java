/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.ae;

import edu.emory.mathcs.backport.java.util.Collections;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.domain.ReviewStatus;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.dto.RoutingAndReviewSearchResultsDTO;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Sameer Sawant
 * @author Biju Joseph
 */
public class RoutingAndReviewCommand{
	private Study study;

    private Participant participant;
    
    private Organization organization;
    
    private ReviewStatus reviewStatus;
    
    private ReportStatus reportStatus;
    
    private boolean workflowEnabled = false;
    
    private HashMap<Object, Object> reviewStatusOptionsMap = new LinkedHashMap<Object, Object>();
    private HashMap<Object, Object> reportStatusOptionsMap = new LinkedHashMap<Object, Object>();
    
    private List<ReviewStatus> reviewStatusList = new ArrayList<ReviewStatus>();
    protected final Collection<ReportStatus> REPORT_STATUS = new ArrayList<ReportStatus>(8);
    
    private RoutingAndReviewSearchResultsDTO searchResultsDTO; 
    
    public RoutingAndReviewCommand() {
        REPORT_STATUS.addAll(Arrays.asList(ReportStatus.values()));
        Collections.sort((List<ReportStatus>)REPORT_STATUS, new Comparator<ReportStatus>() {
			public int compare(ReportStatus o1, ReportStatus o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		});
        
        REPORT_STATUS.remove(ReportStatus.AMENDED);
        REPORT_STATUS.remove(ReportStatus.WITHDRAWN);
        REPORT_STATUS.remove(ReportStatus.REPLACED);
        REPORT_STATUS.remove(ReportStatus.WITHDRAW_FAILED);
        reportStatusOptionsMap.put("", "Please select");
        reportStatusOptionsMap.putAll(WebUtils.collectCustomOptions(REPORT_STATUS, "name", "code", "displayName", ":  "));
        reportStatusOptionsMap.putAll(WebUtils.collectOptions(REPORT_STATUS, "name", "displayName"));
    }
    
    public Study getStudy() {
		return study;
	}
    
    public void setStudy(Study study) {
		this.study = study;
	}
    
    public Participant getParticipant() {
		return participant;
	}
    
    public void setParticipant(Participant participant) {
		this.participant = participant;
	}
    
    public ReviewStatus getReviewStatus(){
    	return reviewStatus;
    }
    
    public void setReviewStatus(ReviewStatus reviewStatus){
    	this.reviewStatus = reviewStatus;
    }
    
    public ReportStatus getReportStatus(){
    	return reportStatus;
    }
    
    public void setReportStatus(ReportStatus reportStatus){
    	this.reportStatus = reportStatus;
    }
    
    public boolean isSearchCriteriaStudyCentric(){
    	return study != null;
    }
    
    public boolean isSearchCriteriaParticipantCentric() {
    	return participant != null && study == null;
    }
    
    public boolean isSearchCriteriaNeitherStudyNorParticipantCentric() {
    	return study == null && participant == null;
    }
    
    public boolean criteriaHasParticipant() {
    	return participant != null;
    }
    
    public boolean criteriaHasStudy(){
    	return study != null;
    }
    
    public boolean criteriaHasSite(){
    	return organization != null;
    }
    
    public boolean criteriaHasReviewStatus(){
    	return reviewStatus != null;
    }
    
    public boolean criteriaHasReportStatus(){
    	return reportStatus != null;
    }
    
    public boolean criteriaPopulated(){
    	return criteriaHasParticipant() || criteriaHasStudy() || criteriaHasSite() || criteriaHasReviewStatus() || criteriaHasReportStatus();
    }
    
    public HashMap<Object, Object> getReviewStatusOptionsMap() {
		return reviewStatusOptionsMap;
	}
    
    public void setReviewStatusOptionsMap(
			HashMap<Object, Object> reviewStatusOptionsMap) {
		this.reviewStatusOptionsMap = reviewStatusOptionsMap;
	}
    
    public HashMap<Object, Object> getReportStatusOptionsMap(){
    	return reportStatusOptionsMap;
    }
    
    public void setReportStatusOptionsMap(HashMap<Object, Object> reportStatusOptionsMap){
    	this.reportStatusOptionsMap = reportStatusOptionsMap;
    }
    
    public boolean getIgnoreCompletedStudy() {
        return false;
    }
    
    public RoutingAndReviewSearchResultsDTO getSearchResultsDTO() {
		return searchResultsDTO;
	}
    
    public void setSearchResultsDTO(
			RoutingAndReviewSearchResultsDTO searchResultsDTO) {
		this.searchResultsDTO = searchResultsDTO;
	}
    
    public boolean getWorkflowEnabled(){
    	return workflowEnabled;
    }
    
    public void setWorkflowEnabled(boolean workflowEnabled){
    	this.workflowEnabled = workflowEnabled;
    }

	public List<ReviewStatus> getReviewStatusList() {
		return reviewStatusList;
	}

	public void setReviewStatusList(List<ReviewStatus> reviewStatusList) {
		this.reviewStatusList = reviewStatusList;
		if(this.reviewStatusList != null && !this.reviewStatusList.isEmpty()){
			Collections.sort(this.reviewStatusList, new Comparator<ReviewStatus>() {
				public int compare(ReviewStatus o1, ReviewStatus o2) {
					return o1.getDisplayName().compareTo(o2.getDisplayName());
				}
			});
		}
    	reviewStatusOptionsMap.put("", "Please select");
    	reviewStatusOptionsMap.putAll(WebUtils.collectCustomOptions(reviewStatusList, "name", "code", "displayName", ":  "));
    	reviewStatusOptionsMap.putAll(WebUtils.collectOptions(reviewStatusList, "name", "displayName"));
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
    
}
