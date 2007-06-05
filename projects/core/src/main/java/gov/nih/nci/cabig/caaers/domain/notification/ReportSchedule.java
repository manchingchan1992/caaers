package gov.nih.nci.cabig.caaers.domain.notification;

import gov.nih.nci.cabig.caaers.domain.AdverseEventReport;
import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A report sending schedule for an adverse event. 
 * The RuleExecutionService, evaluates pre-defined set of rules over the attributes of an AdverseEvent,
 * and creates a ReportSchedule.  
 * 
 * @author Biju Joseph
 *
 */
@Entity
@Table(name = "REPORT_SCHEDULES")
@GenericGenerator(name = "id-generator", strategy = "native",
    parameters = {
        @Parameter(name = "sequence", value = "seq_report_schedules_id")
    }
)
public class ReportSchedule extends AbstractMutableDomainObject implements Serializable{
	
	private String name;
	
	private AdverseEventReport aeReport;
	
	private ReportCalendarTemplate rcTemplate;
	
	/** The list of notificaiton that are to be scheduled */
	private List<ScheduledNotification> notifications;
	
	@Column(name="CREATED_ON")
	private Date createdOn;
	
	@Column(name="DUE_ON")
	private Date dueOn;
	
	@Column(name="SUBMITTED_ON")
	private Date submittedOn;
	
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rct_id")
	public ReportCalendarTemplate getReportCalendarTemplate(){
		return rcTemplate;
	}
	
	public void setReportCalendarTemplate(ReportCalendarTemplate rcTemplate){
		this.rcTemplate = rcTemplate;
	}
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="rpsh_id", nullable=false)
	@Cascade(value = { CascadeType.ALL })
	public List<ScheduledNotification> getScheduledNotifications() {
		return notifications;
	}
	public void setScheduledNotifications(List<ScheduledNotification> notifications) {
		this.notifications = notifications;
	}
	
	
	/**
	 * @return the aeReport
	 */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="report_id")
	public AdverseEventReport getAeReport() {
		return aeReport;
	}
	/**
	 * @param aeReport the aeReport to set
	 */
	public void setAeReport(AdverseEventReport aeReport) {
		this.aeReport = aeReport;
	}
	/**
	 * @return the dueOn
	 */
	public Date getDueOn() {
		return dueOn;
	}
	/**
	 * @param dueOn the dueOn to set
	 */
	public void setDueOn(Date dueOn) {
		this.dueOn = dueOn;
	}
	/**
	 * @return the submittedOn
	 */
	public Date getSubmittedOn() {
		return submittedOn;
	}
	/**
	 * @param submittedOn the submittedOn to set
	 */
	public void setSubmittedOn(Date submittedOn) {
		this.submittedOn = submittedOn;
	}
	public void addScheduledNotification(ScheduledNotification nf){
		if(notifications == null)
			notifications = new ArrayList<ScheduledNotification>();
		notifications.add(nf);
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("ReportSchedule [").append("id : ").append(getId())
    	.append(", name: ").append(name)
    	.append(", createdOn :").append(String.valueOf(createdOn))
    	.append(", submittedOn :").append(String.valueOf(submittedOn))
    	.append(", dueOn :").append(String.valueOf(dueOn));
    	sb.append("\r\n notifications :");
    	for(ScheduledNotification sn : notifications){
    		sb.append("\r\n").append(String.valueOf(sn));
    	}
    	sb.append("]");
    	return sb.toString();
    }
	
	
	
}

