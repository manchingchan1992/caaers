package gov.nih.nci.cabig.caaers.domain.report;


import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
/**
 * ReportDefinition represents the predefined set of notifications <code>PlannedNotification</code> objects for an AdverseEventReport.
 * A ReportDefinition is applied or used by the Report to determine the notifications that are to be send out
 * on a particular instance of time.
 *
 * A ReportDefinition instance to be used, is picked-up by the Rules Engine Component, for a specific kind of report
 * based on the <code>name</code>.
 *
 * @author Biju Joseph
 *
 */
@Entity
@Table(name="REPORT_CALENDAR_TEMPLATES")
@GenericGenerator(name="id-generator", strategy = "native",
    parameters = {
    @Parameter(name="sequence", value="seq_report_calendar_templat_id")
        }
)
public class ReportDefinition extends AbstractMutableDomainObject implements Serializable{
    private String name;
    private String description;
    private int duration;
    private TimeScaleUnit timeScaleUnitType;
    private List<PlannedNotification> plannedNotifications;

    ////// LOGIC

    public Report createReport() {
        Report report = new Report();
        report.setReportDefinition(this);
        report.setStatus(ReportStatus.PENDING);
        return report;
    }

    public PlannedNotification fetchPlannedNotification(int indexOnScale) {
        if (plannedNotifications == null) return null;

        for (PlannedNotification pn : plannedNotifications) {
            if (pn.getIndexOnTimeScale() == indexOnScale) return pn;
        }

        return null;
    }

    public void addPlannedNotification(PlannedNotification pn) {
        if (pn == null) return;

        if (plannedNotifications == null) {
            plannedNotifications = new ArrayList<PlannedNotification>();
        }

        plannedNotifications.add(pn);
    }

    ////// BEAN PROPERTIES

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Type(type = "timeScaleUnit")
    @Column(name = "TIME_SCALE_UNIT_CODE")
    public TimeScaleUnit getTimeScaleUnitType() {
        return timeScaleUnitType;
    }
    public void setTimeScaleUnitType(TimeScaleUnit timeScaleUnitType) {
        this.timeScaleUnitType = timeScaleUnitType;
    }

    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name="rct_id", nullable=false)
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    public List<PlannedNotification> getPlannedNotifications() {
        return plannedNotifications;
    }
    public void setPlannedNotifications(List<PlannedNotification> eventList) {
        this.plannedNotifications = eventList;
    }

    ////// OBJECT METHODS

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        result = PRIME * result + ((timeScaleUnitType == null) ? 0 : timeScaleUnitType.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ReportDefinition other = (ReportDefinition) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;

        if (timeScaleUnitType == null) {
            if (other.timeScaleUnitType != null)
                return false;
        } else if (!timeScaleUnitType.equals(other.timeScaleUnitType))
            return false;
        return true;
    }

}
