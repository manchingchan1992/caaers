package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.caaers.domain.attribution.ConcomitantMedicationAttribution;
import gov.nih.nci.cabig.caaers.domain.attribution.CourseAgentAttribution;
import gov.nih.nci.cabig.caaers.domain.attribution.OtherCauseAttribution;
import gov.nih.nci.cabig.caaers.domain.attribution.DiseaseAttribution;
import gov.nih.nci.cabig.caaers.domain.attribution.SurgeryAttribution;
import gov.nih.nci.cabig.caaers.domain.attribution.RadiationAttribution;
import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rhett Sutphin
 */
/* NOTES:
    - MedDRA code mentioned in use case not yet implemented
    */
@Entity
@GenericGenerator(name="id-generator", strategy = "native",
    parameters = {
        @Parameter(name="sequence", value="seq_adverse_events_id")
    }
)
public class AdverseEvent extends AbstractMutableDomainObject implements ExpeditedAdverseEventReportChild, RoutineAdverseEventReportChild {
    private CtcTerm ctcTerm;
    private String detailsForOther;
    private Grade grade;
    private Hospitalization hospitalization;
    private Boolean expected = false;
    private Attribution attributionSummary;
    private String comments;

    private ExpeditedAdverseEventReport report;
    private RoutineAdverseEventReport routineReport;
    private List<CourseAgentAttribution> courseAgentAttributions;
    private List<ConcomitantMedicationAttribution> concomitantMedicationAttributions;
    private List<OtherCauseAttribution> otherCauseAttributions;
    private List<DiseaseAttribution> diseaseAttributions;
    private List<SurgeryAttribution> surgeryAttributions;
    private List<RadiationAttribution> radiationAttributions;

    ////// BOUND PROPERTIES

    // This is annotated this way so that the IndexColumn in the parent
    // will work with the bidirectional mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable=false, updatable=false, nullable=true)
    public ExpeditedAdverseEventReport getReport() {
        return report;
    }

    public void setReport(ExpeditedAdverseEventReport report) {
        this.report = report;
    }
    
     //  This is annotated this way so that the IndexColumn in the parent
    // will work with the bidirectional mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable=false, updatable=false, nullable=true)
    public RoutineAdverseEventReport getRoutineReport() {
        return routineReport;
    }

    public void setRoutineReport(RoutineAdverseEventReport routineReport) {
        this.routineReport = routineReport;
    } 

    @OneToMany
    @JoinColumn(name="adverse_event_id", nullable=false)
    @IndexColumn(name="list_index")
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @Where(clause = "cause_type = 'CA'") // it is pretty lame that this is necessary
    public List<CourseAgentAttribution> getCourseAgentAttributions() {
        if (courseAgentAttributions == null) courseAgentAttributions = new ArrayList<CourseAgentAttribution>();
        return courseAgentAttributions;
    }

    public void setCourseAgentAttributions(List<CourseAgentAttribution> courseAgentAttributions) {
        this.courseAgentAttributions = courseAgentAttributions;
    }

    @OneToMany
    @JoinColumn(name="adverse_event_id", nullable=false)
    @IndexColumn(name="list_index")
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @Where(clause = "cause_type = 'CM'") // it is pretty lame that this is necessary
    public List<ConcomitantMedicationAttribution> getConcomitantMedicationAttributions() {
        if (concomitantMedicationAttributions == null) {
            concomitantMedicationAttributions = new ArrayList<ConcomitantMedicationAttribution>();
        }
        return concomitantMedicationAttributions;
    }

    public void setConcomitantMedicationAttributions(List<ConcomitantMedicationAttribution> concomitantMedicationAttributions) {
        this.concomitantMedicationAttributions = concomitantMedicationAttributions;
    }

    @OneToMany
    @JoinColumn(name="adverse_event_id", nullable=false)
    @IndexColumn(name="list_index")
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @Where(clause = "cause_type = 'OC'") // it is pretty lame that this is necessary
    public List<OtherCauseAttribution> getOtherCauseAttributions() {
        if (otherCauseAttributions == null) {
            otherCauseAttributions = new ArrayList<OtherCauseAttribution>();
        }
        return otherCauseAttributions;
    }

    public void setOtherCauseAttributions(List<OtherCauseAttribution> otherCauseAttributions) {
        this.otherCauseAttributions = otherCauseAttributions;
    }

    @OneToMany
    @JoinColumn(name="adverse_event_id", nullable=false)
    @IndexColumn(name="list_index")
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @Where(clause = "cause_type = 'DH'") // it is pretty lame that this is necessary
    public List<DiseaseAttribution> getDiseaseAttributions() {
        if (diseaseAttributions == null) {
            diseaseAttributions = new ArrayList<DiseaseAttribution>();
        }
        return diseaseAttributions;
    }

    public void setDiseaseAttributions(List<DiseaseAttribution> diseaseAttributions) {
        this.diseaseAttributions = diseaseAttributions;
    }

    @OneToMany
    @JoinColumn(name="adverse_event_id", nullable=false)
    @IndexColumn(name="list_index")
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @Where(clause = "cause_type = 'SI'") // it is pretty lame that this is necessary
    public List<SurgeryAttribution> getSurgeryAttributions() {
        if (surgeryAttributions == null) {
            surgeryAttributions = new ArrayList<SurgeryAttribution>();
        }
        return surgeryAttributions;
    }

    public void setSurgeryAttributions(List<SurgeryAttribution> surgeryAttributions) {
        this.surgeryAttributions = surgeryAttributions;
    }

    @OneToMany
    @JoinColumn(name="adverse_event_id", nullable=false)
    @IndexColumn(name="list_index")
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @Where(clause = "cause_type = 'RI'") // it is pretty lame that this is necessary
    public List<RadiationAttribution> getRadiationAttributions() {
        if (radiationAttributions == null) {
            radiationAttributions = new ArrayList<RadiationAttribution>();
        }
        return radiationAttributions;
    }

    public void setRadiationAttributions(List<RadiationAttribution> radiationAttributions) {
        this.radiationAttributions = radiationAttributions;
    }

    @ManyToOne
    public CtcTerm getCtcTerm() {
        return ctcTerm;
    }

    public void setCtcTerm(CtcTerm ctcTerm) {
        this.ctcTerm = ctcTerm;
    }

    public String getDetailsForOther() {
        return detailsForOther;
    }

    public void setDetailsForOther(String detailsForOther) {
        this.detailsForOther = detailsForOther;
    }

    @Type(type = "grade")
    @Column(name = "grade_code")
    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    @Type(type = "hospitalization")
    @Column(name = "hospitalization_code")
    public Hospitalization getHospitalization() {
        return hospitalization;
    }

    public void setHospitalization(Hospitalization hospitalization) {
        this.hospitalization = hospitalization;
    }

    public Boolean getExpected() {
        return expected;
    }

    public void setExpected(Boolean expected) {
        this.expected = expected;
    }

    @Type(type = "attribution")
    @Column(name = "attribution_summary_code")
    public Attribution getAttributionSummary() {
        return attributionSummary;
    }

    public void setAttributionSummary(Attribution attributionSummary) {
        this.attributionSummary = attributionSummary;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
