package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.caaers.domain.workflow.WorkflowConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;

/**
 * This class represents the StudySite domain object associated with the Adverse event report.
 * 
 * @author Krikor Krumlian
 * @author Kulasekaran
 * @author Biju Joseph
 */
@Entity
@DiscriminatorValue(value = "SST")
public class StudySite extends StudyOrganization {

    private String statusCode;

    private Date startDate;

    private Date endDate;

    private List<StudyParticipantAssignment> studyParticipantAssignments = new ArrayList<StudyParticipantAssignment>();

    // TODO : to be removed.
    private Date irbApprovalDate;

    private String roleCode;
    
    private Map<String, WorkflowConfig> workflowConfigs;
    
    // ////LOGIC

    public void addAssignment(StudyParticipantAssignment assignment) {
        getStudyParticipantAssignments().add(assignment);
        assignment.setStudySite(this);
    }

    /** Are there any assignments using this relationship? */
    @Transient
    public boolean isUsed() {
        return getStudyParticipantAssignments().size() > 0;
    }

    @Transient
    public String getSiteStudyNames() {
        return getStudy().getShortTitle() + " : " + getOrganization().getName();
    }

    // / BEAN PROPERTIES

    public void setStudyParticipantAssignments(
                    List<StudyParticipantAssignment> studyParticipantAssignments) {
        this.studyParticipantAssignments = studyParticipantAssignments;
    }

    @OneToMany(mappedBy = "studySite")
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    public List<StudyParticipantAssignment> getStudyParticipantAssignments() {
        return studyParticipantAssignments;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    @Transient
    public String getRoleName() {
        return "Site";
    }
    
    @CollectionOfElements
    @JoinTable(name = "site_workflow_configs", 
    		joinColumns = @JoinColumn(name = "site_id"), inverseJoinColumns = @JoinColumn(name="wf_config_id"))
    @MapKey(columns = @Column(name = "wf_entity"))
    @Column(name = "wf_config_id")
    public Map<String, WorkflowConfig> getWorkflowConfigs() {
		return workflowConfigs;
	}
    public void setWorkflowConfigs(Map<String, WorkflowConfig> workflowConfigs) {
		this.workflowConfigs = workflowConfigs;
	}
}
