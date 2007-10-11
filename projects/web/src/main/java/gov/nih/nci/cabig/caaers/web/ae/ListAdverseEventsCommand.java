package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.dao.StudyParticipantAssignmentDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.service.EvaluationService;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.service.ReportSubmittability;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Rhett Sutphin
 */
public class ListAdverseEventsCommand {
    // TODO: these should be collected with other known ID types centrally
    // package-level for testing
    static final String PROTOCOL_AUTHORITY_IDENTIFIER_TYPE = "Protocol Authority Identifier";
    static final String MRN_IDENTIFIER_TYPE = "MRN";

    private StudyParticipantAssignment assignment;

    private Study study;
    private Participant participant;
    Map<Integer, Boolean> reportsSubmittable;

    // Alternate parameters
    private String mrn;
    private String nciIdentifier;

    private StudyParticipantAssignmentDao assignmentDao;
    private StudyDao studyDao;
    private ParticipantDao participantDao;
    private EvaluationService evaluationService;

    public ListAdverseEventsCommand(StudyParticipantAssignmentDao assignmentDao, StudyDao studyDao, ParticipantDao participantDao, EvaluationService evaluationService) {
        this.assignmentDao = assignmentDao;
        this.studyDao = studyDao;
        this.participantDao = participantDao;
        this.evaluationService = evaluationService;
        reportsSubmittable = new HashMap<Integer, Boolean>();
    }

    ////// LOGIC

    public StudyParticipantAssignment getAssignment() {
        if (assignment != null) {
            return assignment;
        } else if (getParticipant() != null && getStudy() != null) {
            assignment = assignmentDao.getAssignment(getParticipant(), getStudy());
            updateSubmittability();
            return assignment;
        } else {
            return null;
        }
    }

    private void updateSubmittability() {
        reportsSubmittable.clear();
        for (ExpeditedAdverseEventReport aeReport : assignment.getAeReports()) {
            for (Report report : aeReport.getReports()) {
                ReportSubmittability errorMessages = evaluationService.isSubmittable(report);
                reportsSubmittable.put(report.getId(), errorMessages.isSubmittable());
            }
        }
    }

    public Study getStudy() {
        if (study != null) {
            return study;
        } else if (assignment != null) {
            return assignment.getStudySite().getStudy();
        } else if (nciIdentifier != null) {
            study = studyDao.getByIdentifier(
                createIdentifierTemplate(null, nciIdentifier));
            return study;
        } else {
            return null;
        }
    }

    public Participant getParticipant() {
        if (participant != null) {
            return participant;
        } else if (assignment != null) {
            return assignment.getParticipant();
        } else if (mrn != null) {
            participant = participantDao.getByIdentifier(
                createIdentifierTemplate(MRN_IDENTIFIER_TYPE, mrn));
            return participant;
        } else {
            return null;
        }
    }

    private Identifier createIdentifierTemplate(String type, String value) {
        Identifier param = new Identifier();
        param.setType(type);
        param.setValue(value);
        return param;
    }

    public Map<Integer, Boolean> getReportsSubmittable() {
        return reportsSubmittable;
    }

    ////// BOUND PROPERTIES

    public void setAssignment(StudyParticipantAssignment assignment) {
        this.assignment = assignment;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public void setNciIdentifier(String nciIdentifier) {
        this.nciIdentifier = nciIdentifier;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }
}
