package gov.nih.nci.cabig.caaers.service;

import gov.nih.nci.cabig.caaers.dao.report.ReportDefinitionDao;
import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.ExpeditedAdverseEventReport;
import gov.nih.nci.cabig.caaers.domain.Grade;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * A mock implementation of {@link gov.nih.nci.cabig.caaers.service.EvaluationService}, suitable
 * for local testing.
 *
 * @author Rhett Sutphin
 */
@Transactional(readOnly=true)
public class MockEvaluationService implements EvaluationService {
    private static final Log log = LogFactory.getLog(MockEvaluationService.class);

    private ReportDefinitionDao reportDefinitionDao;
    private ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao;
    private ReportService reportService;

    /**
     * Mock implementation returns true for all grade 5 AEs.
     */
    public boolean isSevere(StudyParticipantAssignment assignment, AdverseEvent adverseEvent) {
        return adverseEvent.getGrade() == Grade.DEATH;
    }

    /**
     * Mock implementation adds a report for the first report def returned by reportDefDao#getAll.
     */
    @Transactional(readOnly=false)
    public void addRequiredReports(ExpeditedAdverseEventReport expeditedData) {
        List<ReportDefinition> allDefs = reportDefinitionDao.getAll();
        if (allDefs.size() == 0) {
            log.warn("Mock evaluation service needs at least one report definition");
            return;
        }

        ReportDefinition def = allDefs.get(0);
        Report report = existingReportWithDef(expeditedData, def);
        if (report == null) {
            report = reportService.createReport(def, expeditedData);
        }
        report.setRequired(true);

        expeditedAdverseEventReportDao.save(expeditedData);
    }

    private Report existingReportWithDef(ExpeditedAdverseEventReport expeditedData, ReportDefinition def) {
        for (Report report : expeditedData.getReports()) {
            log.debug("Examining Report with def "+ report.getReportDefinition().getName()
                + " (id: " + report.getReportDefinition().getId() + "; hash: "
                + Integer.toHexString(report.getReportDefinition().hashCode()) + ')');
            if (report.getReportDefinition().getId().equals(def.getId())) {
                log.debug("Matched");
                return report;
            }
        }
        log.debug("No Report with def matching " + def.getName()
            + " (id: " + def.getId() + "; hash: "
            + Integer.toHexString(def.hashCode()) + ") found in EAER " + expeditedData.getId());
        return null;
    }

    /**
     * Mock implementation returns all the report defs in the system
     */
    public List<ReportDefinition> applicableReportDefinitions(StudyParticipantAssignment assignment) {
        return reportDefinitionDao.getAll();
    }

    ////// CONFIGURATION

    public void setReportDefinitionDao(ReportDefinitionDao reportDefinitionDao) {
        this.reportDefinitionDao = reportDefinitionDao;
    }

    public void setExpeditedAdverseEventReportDao(ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao) {
        this.expeditedAdverseEventReportDao = expeditedAdverseEventReportDao;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
