package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.domain.attribution.AdverseEventAttribution;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.validation.annotation.UniqueObjectInCollection;
import gov.nih.nci.cabig.ctms.collections.LazyListHelper;
import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * This class represents the ExpeditedAdverseEventReport domain object.
 * @author Rhett Sutphin
 */
@Entity
@Table(name = "ae_reports")
@GenericGenerator(name="id-generator", strategy = "native",
    parameters = {
        @Parameter(name="sequence", value="seq_ae_reports_id")
    }
)
public class ExpeditedAdverseEventReport extends AbstractMutableDomainObject {
    private StudyParticipantAssignment assignment;
    private Timestamp createdAt;
    private LazyListHelper lazyListHelper;

    private AdverseEventResponseDescription responseDescription;
    private TreatmentInformation treatmentInformation;
    private AdditionalInformation additionalInformation;


    private Reporter reporter;
    private Physician physician;
    private ParticipantHistory participantHistory;
    private DiseaseHistory diseaseHistory;
    private AdverseEventReportingPeriod reportingPeriod;

    private List<Report> reports;
    private static final Log log = LogFactory.getLog(ExpeditedAdverseEventReport.class);

    // TODO
    // private List<MedicalDevice> medicalDevices;

    public ExpeditedAdverseEventReport() {
        lazyListHelper = new LazyListHelper();
        addReportChildLazyList(AdverseEvent.class);
        addReportChildLazyList(Lab.class);
        addReportChildLazyList(MedicalDevice.class);
        addReportChildLazyList(RadiationIntervention.class);
        addReportChildLazyList(SurgeryIntervention.class);
        addReportChildLazyList(ConcomitantMedication.class);
        addReportChildLazyList(OtherCause.class);
        addReportChildLazyList(SAEReportPriorTherapy.class);
        addReportChildLazyList(SAEReportPreExistingCondition.class);
        addReportChildLazyList(Outcome.class);
    }

    private <T extends ExpeditedAdverseEventReportChild> void addReportChildLazyList(Class<T> klass) {
        lazyListHelper.add(klass,
                new ExpeditedAdverseEventReportChildFactory<T>(klass, this));
    }

    ////// LOGIC

    @Transient
    public String getNotificationMessage() {
        if (isNotificationMessagePossible()) {
            String other = "";
            String fullName = "";
            AdverseEvent firstAe = getAdverseEventsInternal().get(0);
            if (firstAe.getAdverseEventCtcTerm().getCtcTerm() != null) {
                CtcTerm term = firstAe.getAdverseEventCtcTerm().getCtcTerm();
                fullName = term.getFullName();
                other = term.isOtherRequired()
                        ? String.format(" (%s)", firstAe.getDetailsForOther()) : "";
            } else {
                fullName = firstAe.getAdverseEventTerm().getUniversalTerm();
            }

            return String.format("Grade %d adverse event with term %s%s",
                    firstAe.getGrade().getCode(),
                    fullName, other
            );
        } else {
            throw new CaaersSystemException(
                    "Cannot create notification message until primary AE is filled in");
        }
    }

    @Transient
    public boolean isNotificationMessagePossible() {
        if (getAdverseEventsInternal().size() < 1) return false;
        AdverseEvent ae = getAdverseEventsInternal().get(0);
        return ae != null && ae.getGrade() != null && ae.getAdverseEventTerm().getTerm() != null;
    }

    @Transient
    public Participant getParticipant() {
        return getAssignment() == null ? null : getAssignment().getParticipant();
    }

    @Transient
    public Study getStudy() {
        StudySite ss = getAssignment() == null ? null : getAssignment().getStudySite();
        return ss == null ? null : ss.getStudy();
    }

    @Transient
    @Deprecated
    public boolean isExpeditedReportingRequired() {
        for (Report report : getReports()) {
            if (report.isRequired()) return true;
        }
        return false;
    }

    @Transient
    @Deprecated
    public int getRequiredReportCount() {
        int count = 0;
        for (Report report : getReports()) {
            if (report.isRequired()) count++;
        }
        return count;
    }

    /*
    * Checks whether all reports in the SAE are complete or not
    *
    */
    @Transient
    public String getAreAllReportsSubmitted() {
        Boolean areAllReportsSubmitted = true;
        for (Report report : reports) {
            if (report.getLastVersion().getReportStatus() != ReportStatus.COMPLETED) {
                areAllReportsSubmitted = false;
                break;
            }
        }
        return areAllReportsSubmitted.toString();
    }

    @Transient
    public Map<String, String> getSummary() {
        Map<String, String> summary = new LinkedHashMap<String, String>();
        summary.put("Participant", summaryLine(getParticipant()));
        summary.put("Study", summaryLine(getStudy()));
        summary.put("Report created at", getCreatedAt() == null ? null : getCreatedAt().toString());
        String primaryAeLine = null;
        if (getAdverseEvents().size() > 0 &&
                getAdverseEvents().get(0).getAdverseEventTerm() != null &&
                getAdverseEvents().get(0).getAdverseEventTerm().getUniversalTerm() != null) {
            primaryAeLine = getAdverseEvents().get(0).getAdverseEventTerm().getUniversalTerm();
        }
        summary.put("Primary AE", primaryAeLine);
        summary.put("AE count", Integer.toString(getAdverseEvents().size()));
        summary.put("Public identifier", getPublicIdentifier());
        // TODO: placeholders
        summary.put("Ticket number", null);
        summary.put("Next report due", null);

        return summary;
    }

    private String summaryLine(Participant participant) {
        if (participant == null) return null;
        StringBuilder sb = new StringBuilder(participant.getFullName());
        appendPrimaryIdentifier(participant, sb);
        return sb.toString();
    }

    private String summaryLine(Study study) {
        if (study == null) return null;
        StringBuilder sb = new StringBuilder(study.getLongTitle());
        appendPrimaryIdentifier(study, sb);
        return sb.toString();
    }

    private void appendPrimaryIdentifier(IdentifiableByAssignedIdentifers ided, StringBuilder sb) {
        if (ided.getPrimaryIdentifier() != null) {
            sb.append(" (").append(ided.getPrimaryIdentifier().getValue()).append(')');
        }
    }

    public void addAdverseEvent(AdverseEvent adverseEvent) {
        getAdverseEventsInternal().add(adverseEvent);
        if (adverseEvent != null) adverseEvent.setReport(this);
    }

    /**
     * @return a wrapped list which will never throw an {@link IndexOutOfBoundsException}
     */
    @Transient
    public List<AdverseEvent> getAdverseEvents() {
        return lazyListHelper.getLazyList(AdverseEvent.class);
    }

    public void addLab(Lab lab) {
        getLabsInternal().add(lab);
        if (lab != null) lab.setReport(this);
    }

    /**
     * @return a wrapped list which will never throw an {@link IndexOutOfBoundsException}
     */
    @Transient
    public List<Lab> getLabs() {
        return lazyListHelper.getLazyList(Lab.class);
    }

    public void addMedicalDevice(MedicalDevice medicalDevice) {
        getMedicalDevicesInternal().add(medicalDevice);
        if (medicalDevice != null) medicalDevice.setReport(this);
    }

    /**
     * @return a wrapped list which will never throw an {@link IndexOutOfBoundsException}
     */
    @Transient
    public List<MedicalDevice> getMedicalDevices() {
        return lazyListHelper.getLazyList(MedicalDevice.class);
    }


    public void addRadiationIntervention(RadiationIntervention radiationIntervention) {
        getRadiationInterventionsInternal().add(radiationIntervention);
        if (radiationIntervention != null) radiationIntervention.setReport(this);
    }

    /**
     * @return a wrapped list which will never throw an {@link IndexOutOfBoundsException}
     */
    @Transient
    public List<RadiationIntervention> getRadiationInterventions() {
        return lazyListHelper.getLazyList(RadiationIntervention.class);
    }

    public void addSurgeryIntervention(SurgeryIntervention surgeryIntervention) {
        getSurgeryInterventionsInternal().add(surgeryIntervention);
        if (surgeryIntervention != null) surgeryIntervention.setReport(this);
    }

    /**
     * @return a wrapped list which will never throw an {@link IndexOutOfBoundsException}
     */
    @Transient
    public List<SurgeryIntervention> getSurgeryInterventions() {
        return lazyListHelper.getLazyList(SurgeryIntervention.class);
    }


    public void addConcomitantMedication(ConcomitantMedication concomitantMedication) {
        getConcomitantMedicationsInternal().add(concomitantMedication);
        if (concomitantMedication != null) concomitantMedication.setReport(this);
    }

    /**
     * @return a wrapped list which will never throw an {@link IndexOutOfBoundsException}
     */
    @Transient
    public List<ConcomitantMedication> getConcomitantMedications() {
        return lazyListHelper.getLazyList(ConcomitantMedication.class);
    }

    public void addSaeReportPreExistingCondition(SAEReportPreExistingCondition sAEReportPreExistingCondition) {
        getSaeReportPreExistingConditionsInternal().add(sAEReportPreExistingCondition);
        if (sAEReportPreExistingCondition != null) sAEReportPreExistingCondition.setReport(this);
    }

    /** @return a wrapped list which will never throw an {@link IndexOutOfBoundsException} */
    @Transient
    @UniqueObjectInCollection(message="Duplicate pre existing condition")
    public List<SAEReportPreExistingCondition> getSaeReportPreExistingConditions() {
        return lazyListHelper.getLazyList(SAEReportPreExistingCondition.class);
    }

    public void addSaeReportPriorTherapies(SAEReportPriorTherapy saeReportPriorTherapy) {
        getSaeReportPriorTherapiesInternal().add(saeReportPriorTherapy);
        if (saeReportPriorTherapy != null) saeReportPriorTherapy.setReport(this);
    }

    /** @return a wrapped list which will never throw an {@link IndexOutOfBoundsException} */
    @Transient
    @UniqueObjectInCollection(message="Duplicate prior therapy")
    public List<SAEReportPriorTherapy> getSaeReportPriorTherapies() {
        return lazyListHelper.getLazyList(SAEReportPriorTherapy.class);
    }
    
    public void addOutcomes(Outcome outcome) {
        getOutcomesInternal().add(outcome);
        if (outcome != null) outcome.setReport(this);
    }

    /** @return a wrapped list which will never throw an {@link IndexOutOfBoundsException} */
    @Transient
    public List<Outcome> getOutcomes() {
        return lazyListHelper.getLazyList(Outcome.class);
    }

    public void addOtherCause(OtherCause otherCause) {
        getOtherCausesInternal().add(otherCause);
        if (otherCause != null) otherCause.setReport(this);
    }

    /** @return a wrapped list which will never throw an {@link IndexOutOfBoundsException} */
    @Transient
    public List<OtherCause> getOtherCauses() {
        return lazyListHelper.getLazyList(OtherCause.class);
    }
    

    ////// BEAN PROPERTIES



    @ManyToOne(fetch = FetchType.LAZY)
    public StudyParticipantAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(StudyParticipantAssignment assignment) {
        this.assignment = assignment;
    }

    // This is annotated this way so that the IndexColumn will work with
    // the bidirectional mapping.  See section 2.4.6.2.3 of the hibernate annotations docs.
    @OneToMany
    @JoinColumn(name = "report_id", nullable = true)
    @IndexColumn(name = "list_index", nullable = false)
    @Cascade(value = {
            // Manually-managing PERSIST cascades due to cascade ordering issue
            CascadeType.DELETE, CascadeType.EVICT, CascadeType.LOCK, CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DELETE_ORPHAN})
    protected List<AdverseEvent> getAdverseEventsInternal() {
        return lazyListHelper.getInternalList(AdverseEvent.class);
    }

    @SuppressWarnings("unchecked")
    protected void setAdverseEventsInternal(List<AdverseEvent> adverseEvents) {
        lazyListHelper.setInternalList(AdverseEvent.class, adverseEvents);
    }

    // This is annotated this way so that the IndexColumn will work with
    // the bidirectional mapping.  See section 2.4.6.2.3 of the hibernate annotations docs.
    @OneToMany
    @JoinColumn(name = "report_id", nullable = false)
    @IndexColumn(name = "list_index")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    protected List<Lab> getLabsInternal() {
        return lazyListHelper.getInternalList(Lab.class);
    }

    protected void setLabsInternal(List<Lab> labsInternal) {
        lazyListHelper.setInternalList(Lab.class, labsInternal);
    }

    // This is annotated this way so that the IndexColumn will work with
    // the bidirectional mapping.  See section 2.4.6.2.3 of the hibernate annotations docs.
    @OneToMany
    @JoinColumn(name = "report_id", nullable = false)
    @IndexColumn(name = "list_index")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    protected List<MedicalDevice> getMedicalDevicesInternal() {
        return lazyListHelper.getInternalList(MedicalDevice.class);
    }

    protected void setMedicalDevicesInternal(List<MedicalDevice> medicalDevicesInternal) {
        lazyListHelper.setInternalList(MedicalDevice.class, medicalDevicesInternal);
    }

    // This is annotated this way so that the IndexColumn will work with
    // the bidirectional mapping.  See section 2.4.6.2.3 of the hibernate annotations docs.
    @OneToMany
    @JoinColumn(name = "report_id", nullable = false)
    @IndexColumn(name = "list_index")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    protected List<RadiationIntervention> getRadiationInterventionsInternal() {
        return lazyListHelper.getInternalList(RadiationIntervention.class);
    }

    protected void setRadiationInterventionsInternal(List<RadiationIntervention> radiationInterventionsInternal) {
        lazyListHelper.setInternalList(RadiationIntervention.class, radiationInterventionsInternal);
    }

    //  This is annotated this way so that the IndexColumn will work with
    // the bidirectional mapping.  See section 2.4.6.2.3 of the hibernate annotations docs.
    @OneToMany
    @JoinColumn(name = "report_id", nullable = false)
    @IndexColumn(name = "list_index")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    protected List<SurgeryIntervention> getSurgeryInterventionsInternal() {
        return lazyListHelper.getInternalList(SurgeryIntervention.class);
    }

    protected void setSurgeryInterventionsInternal(List<SurgeryIntervention> surgeryInterventionsInternal) {
        lazyListHelper.setInternalList(SurgeryIntervention.class, surgeryInterventionsInternal);
    }

    // This is annotated this way so that the IndexColumn will work with
    // the bidirectional mapping.  See section 2.4.6.2.3 of the hibernate annotations docs.
    @OneToMany
    @JoinColumn(name = "report_id", nullable = false)
    @IndexColumn(name = "list_index")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    protected List<ConcomitantMedication> getConcomitantMedicationsInternal() {
        return lazyListHelper.getInternalList(ConcomitantMedication.class);
    }

    protected void setConcomitantMedicationsInternal(List<ConcomitantMedication> concomitantMedicationsInternal) {
        lazyListHelper.setInternalList(ConcomitantMedication.class, concomitantMedicationsInternal);
    }

    // This is annotated this way so that the IndexColumn will work with
    // the bidirectional mapping.  See section 2.4.6.2.3 of the hibernate annotations docs.
    @OneToMany
    @JoinColumn(name = "report_id", nullable = false)
    @IndexColumn(name = "list_index")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    protected List<SAEReportPreExistingCondition> getSaeReportPreExistingConditionsInternal() {
        return lazyListHelper.getInternalList(SAEReportPreExistingCondition.class);
    }

    protected void setSaeReportPreExistingConditionsInternal(List<SAEReportPreExistingCondition> saeReportPreExistingConditionInternal) {
        lazyListHelper.setInternalList(SAEReportPreExistingCondition.class, saeReportPreExistingConditionInternal);
    }

    // This is annotated this way so that the IndexColumn will work with
    // the bidirectional mapping.  See section 2.4.6.2.3 of the hibernate annotations docs.
    @OneToMany
    @JoinColumn(name = "report_id", nullable = false)
    @IndexColumn(name = "list_index")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    protected List<OtherCause> getOtherCausesInternal() {
        return lazyListHelper.getInternalList(OtherCause.class);
    }

    protected void setOtherCausesInternal(List<OtherCause> otherCausesInternal) {
        lazyListHelper.setInternalList(OtherCause.class, otherCausesInternal);
    }

    //  This is annotated this way so that the IndexColumn will work with
    // the bidirectional mapping.  See section 2.4.6.2.3 of the hibernate annotations docs.
    @OneToMany
    @JoinColumn(name = "report_id", nullable = false)
    @IndexColumn(name = "list_index")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public List<SAEReportPriorTherapy> getSaeReportPriorTherapiesInternal() {
        return lazyListHelper.getInternalList(SAEReportPriorTherapy.class);
    }

    public void setSaeReportPriorTherapiesInternal(
            List<SAEReportPriorTherapy> saeReportPriorTherapiesInternal) {
        lazyListHelper.setInternalList(SAEReportPriorTherapy.class, saeReportPriorTherapiesInternal);
    }

    //  This is annotated this way so that the IndexColumn will work with
    // the bidirectional mapping.  See section 2.4.6.2.3 of the hibernate annotations docs.
    @OneToMany
    @JoinColumn(name = "report_id", nullable = false)
    @IndexColumn(name = "list_index")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public List<Outcome> getOutcomesInternal() {
        return lazyListHelper.getInternalList(Outcome.class);
    }

    public void setOutcomesInternal(
            List<Outcome> outcomesInternal) {
        lazyListHelper.setInternalList(Outcome.class, outcomesInternal);
    }


    @OneToOne(fetch = FetchType.LAZY, mappedBy = "report")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public TreatmentInformation getTreatmentInformation() {
        if (treatmentInformation == null) setTreatmentInformation(new TreatmentInformation());
        return treatmentInformation;
    }

    public void setTreatmentInformation(TreatmentInformation treatmentInformation) {
        this.treatmentInformation = treatmentInformation;
        if (treatmentInformation != null) treatmentInformation.setReport(this);
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "report")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public AdditionalInformation getAdditionalInformation() {
        if (additionalInformation == null) setAdditionalInformation(new AdditionalInformation());
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
        if (additionalInformation != null) additionalInformation.setReport(this);
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "report")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public AdverseEventResponseDescription getResponseDescription() {
        if (responseDescription == null) setResponseDescription(new AdverseEventResponseDescription());
        return responseDescription;
    }

    public void setResponseDescription(AdverseEventResponseDescription responseDescription) {
        this.responseDescription = responseDescription;
        if (responseDescription != null) responseDescription.setReport(this);
    }

    // non-total cascade allows us to skip saving if the reporter hasn't been filled in yet
    @OneToOne(mappedBy = "expeditedReport")
    @Cascade(value = { CascadeType.DELETE, CascadeType.EVICT, CascadeType.LOCK, CascadeType.REMOVE})
    public Reporter getReporter() {
        if (reporter == null) setReporter(new Reporter());
        return reporter;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
        if (reporter != null) reporter.setExpeditedReport(this);
    }

    // non-total cascade allows us to skip saving if the physician hasn't been filled in yet
    @OneToOne(mappedBy = "expeditedReport")
    @Cascade(value = { CascadeType.DELETE, CascadeType.EVICT, CascadeType.LOCK, CascadeType.REMOVE })
    public Physician getPhysician() {
        if (physician == null) setPhysician(new Physician());
        return physician;
    }

    public void setPhysician(Physician physician) {
        this.physician = physician;
        if (physician != null) physician.setExpeditedReport(this);
    }

    @OneToOne(mappedBy = "report")
    @Cascade(value = {CascadeType.ALL})
    public DiseaseHistory getDiseaseHistory() {
        if (diseaseHistory == null) setDiseaseHistory(new DiseaseHistory());
        return diseaseHistory;
    }

    public void setDiseaseHistory(DiseaseHistory diseaseHistory) {
        this.diseaseHistory = diseaseHistory;
        if (diseaseHistory != null) diseaseHistory.setReport(this);
    }

    @OneToOne(mappedBy = "report")
    @Cascade(value = {CascadeType.ALL})
    public ParticipantHistory getParticipantHistory() {
        if (participantHistory == null) setParticipantHistory(new ParticipantHistory());
        return participantHistory;
    }

    public void setParticipantHistory(ParticipantHistory participantHistory) {
        this.participantHistory = participantHistory;
        if (participantHistory != null) participantHistory.setReport(this);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "aeReport")
    @OrderBy("dueOn")
    @Cascade(value = { CascadeType.DELETE, CascadeType.EVICT, 
    				   CascadeType.LOCK, CascadeType.REMOVE }) // Manually manage update-style reassociates and saves
    public List<Report> getReports() {
        if (reports == null) reports = new ArrayList<Report>();
        return reports;
    }

    /**
     * This method returns all the reports that are not in {@link ReportStatus}.WITHDRAWN.
     *
     * @return
     */
    @Transient
    public List<Report> getNonWithdrawnReports() {
        List<Report> reports = getReports();
        if (reports.isEmpty()) return reports;
        List<Report> submitableReports = new ArrayList<Report>();
        for (Report report : reports) {
            if (!report.getStatus().equals(ReportStatus.WITHDRAWN)) submitableReports.add(report);
        }
        return submitableReports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public void addReport(Report report) {
        getReports().add(report);
        report.setAeReport(this);
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @OneToOne
    @JoinColumn(name = "reporting_period_id")
    @Cascade(value = { CascadeType.LOCK })
    public AdverseEventReportingPeriod getReportingPeriod() {
		return reportingPeriod;
	}
    
    public void setReportingPeriod(AdverseEventReportingPeriod reportingPeriod) {
		this.reportingPeriod = reportingPeriod;
	}


    @Deprecated
    @Transient
    public ReportStatus getStatus() {
        //TODO: to be removed after compile/runtime
        //dependency is resolved, by respective developer

        //assert false : "Update your code";
        return ReportStatus.PENDING;
    }

    @Deprecated
    public void setStatus(ReportStatus status) {
        //TODO: to be removed after compile/runtime
        //dependency is resolved, by respective developer

        //assert false : "Update your code";
    }

    @Transient
    public String getPublicIdentifier() {
        String id = getAssignment().getStudySite().getOrganization().getNciInstituteCode() + "-" + getAssignment().getStudySite().getOrganization().getNciInstituteCode();
        id = (id.indexOf("null") > -1) ? "None" : id;
        return id;
    }

    @Transient
    public void setPublicIdentifier(String strId) {
    }

    @Transient
    public List<String> findEmailAddress(String role) {
        List<String> addresses = new ArrayList<String>();
        String address = null;
        final Study study = getStudy();

        try {
            if (StringUtils.equals("REP", role)) {//Reporter
                address = getReporter().getContactMechanisms().get(PersonContact.EMAIL);
                if (StringUtils.isNotEmpty(address)) addresses.add(address);
            } else if (StringUtils.equals(role, "SUB")) {//Submitter
                address = getReports().get(0).getLastVersion().getSubmitter().getContactMechanisms().get(PersonContact.EMAIL);
                if (StringUtils.isNotEmpty(address)) addresses.add(address);
            } else if (StringUtils.equals(role, "SPI")) {//Site Principal Investigator
                addresses.addAll(study.findEmailsOfSiteInvestigators("Site Principal Investigator"));
            } else if (StringUtils.equals(role, "SI")) {//Site Investigator
                addresses.addAll(study.findEmailsOfSiteInvestigators("Site Investigator"));
            } else if (StringUtils.equals(role, "PI")) {//Principal Investigator
                addresses.addAll(study.findEmailsOfSiteInvestigators("Principal Investigator"));
            } else if (StringUtils.equals(role, "PC")) {//Participant Coordinator
                addresses.addAll(study.findEmailsOfResearchStaff("Participant Coordinator"));
            } else if (StringUtils.equals(role, "SC")) {//Study Coordinator
                addresses.addAll(study.findEmailsOfResearchStaff("Study Coordinator"));
            } else if (StringUtils.equals(role, "AEC")) {//Adverse Event Coordinator
                addresses.addAll(study.findEmailsOfResearchStaff("Adverse Event Coordinator"));
            }
        } catch (Exception e) {
            log.warn("Unable to fetch the email address of Role :" + role + ", mechanismType : EMAIL", e);
        }
        return addresses;
    }

    @Transient
    List<String> findPhoneNumbers(String role) {
        assert false : "Not implemented";
        return null;
    }

    @Transient
    List<String> findFaxNumbers(String role) {
        assert false : "Not implemented";
        return null;
    }
}
