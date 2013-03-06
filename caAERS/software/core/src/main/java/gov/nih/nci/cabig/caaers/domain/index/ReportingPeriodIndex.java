/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.index;

import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "reportingperiod_index")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "seq_reportingperiod_index_id") })
public class ReportingPeriodIndex extends AbstractMutableDomainObject{
	
	private String loginId;
	private AdverseEventReportingPeriod reportingPeriod;

    private Integer roleCode;

    public Integer getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(Integer roleCode) {
        this.roleCode = roleCode;
    }
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	@ManyToOne
    @JoinColumn(name = "reportingperiod_id")
	public AdverseEventReportingPeriod getReportingPeriod() {
		return reportingPeriod;
	}
	public void setReportingPeriod(AdverseEventReportingPeriod reportingPeriod) {
		this.reportingPeriod = reportingPeriod;
	}

}
