package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;


/**
* This class represents the Reconciliation Report Adverse Event domain object.
*
* @author Ramakrishna
*
*/

@Entity
@Table(name = "reconciliation_reports")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "seq_reconciliation_reports_id")})
public class ReconciliationReport extends AbstractMutableDomainObject{

    /** Created date of the reconciliation report. */
    private Date createdDate = new Date();

    /** The date the reconciliation report is updated. */
    private Date updatedDate;

    /** The user who reviewed the reconciliation report. */
    private String reviewedBy;

	private AdverseEventReportingPeriod adverseEventReportingPeriod;
	
	private List<ReconciledAdverseEvent> reconciledAdverseEvents;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="reporting_period_id")
	public AdverseEventReportingPeriod getAdverseEventReportingPeriod() {
		return adverseEventReportingPeriod;
	}

	public void setAdverseEventReportingPeriod( AdverseEventReportingPeriod adverseEventReportingPeriod) {
		this.adverseEventReportingPeriod = adverseEventReportingPeriod;
	}

	@OneToMany(mappedBy = "reconciliationReportReportingPeriod", orphanRemoval = true)
    @Cascade(value = {CascadeType.ALL})
    @OrderBy
    @Fetch(value = org.hibernate.annotations.FetchMode.SUBSELECT)
	public List<ReconciledAdverseEvent> getReconciledAdverseEvents() {
		return reconciledAdverseEvents;
	}

	public void setReconciledAdverseEvents(List<ReconciledAdverseEvent> reconciledAdverseEvents) {
		this.reconciledAdverseEvents = reconciledAdverseEvents;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getReviewedBy() {
		return reviewedBy;
	}

	public void setReviewedBy(String reviewedBy) {
		this.reviewedBy = reviewedBy;
	}


}
