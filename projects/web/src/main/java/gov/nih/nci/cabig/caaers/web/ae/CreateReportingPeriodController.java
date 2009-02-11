package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.dao.AdverseEventReportingPeriodDao;
import gov.nih.nci.cabig.caaers.dao.EpochDao;
import gov.nih.nci.cabig.caaers.dao.ParticipantDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.StudyParticipantAssignmentDao;
import gov.nih.nci.cabig.caaers.dao.TreatmentAssignmentDao;
import gov.nih.nci.cabig.caaers.dao.UserDao;
import gov.nih.nci.cabig.caaers.dao.workflow.WorkflowConfigDao;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.AdverseEventCtcTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventMeddraLowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.AdverseEventReportingPeriod;
import gov.nih.nci.cabig.caaers.domain.Epoch;
import gov.nih.nci.cabig.caaers.domain.ReviewStatus;
import gov.nih.nci.cabig.caaers.domain.SolicitedAdverseEvent;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyParticipantAssignment;
import gov.nih.nci.cabig.caaers.domain.Term;
import gov.nih.nci.cabig.caaers.domain.repository.AdverseEventRoutingAndReviewRepository;
import gov.nih.nci.cabig.caaers.service.workflow.WorkflowService;
import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.web.ControllerTools;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldAttributes;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * The create flow of Reporting Period is handled by this controller.
 *
 * @author Sameer Sawant
 * @author Biju Joseph
 */

public class CreateReportingPeriodController extends SimpleFormController {

    private static final String REPORTINGPERIOD_FIELD_GROUP = "ReportingPeriod";
    private InputFieldGroup reportingPeriodFieldGroup;

    private AdverseEventReportingPeriodDao adverseEventReportingPeriodDao;
    private StudyParticipantAssignmentDao assignmentDao;
    private ParticipantDao participantDao;
    private TreatmentAssignmentDao treatmentAssignmentDao;
    private StudyDao studyDao;
    private EpochDao epochDao;
    private AdverseEventRoutingAndReviewRepository adverseEventRoutingAndReviewRepository;
    private WorkflowConfigDao workflowConfigDao;
	private UserDao userDao;
	
	private Configuration configuration;

    public CreateReportingPeriodController() {
        setFormView("ae/createReportingPeriod");
    }

    /**
     * Will return the ReportingPeriodCommand, after properly initializing {@link StudyParticipantAssignment}, {@link Study}, {@link AdverseEventReportingPeriod}
     */
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {

        //obtain assignmentId and  id from request parameters
        int assignmentId = NumberUtils.toInt(request.getParameter("assignmentId"), 0);
        int reportingPeriodId = NumberUtils.toInt(request.getParameter("id"), 0);

        //load assignment
        StudyParticipantAssignment assignment = (assignmentId > 0) ? assignmentDao.getById(assignmentId) : null;
        AdverseEventReportingPeriod reportingPeriod = (reportingPeriodId > 0) ? adverseEventReportingPeriodDao.getById(reportingPeriodId) : null;

        ReportingPeriodCommand command = new ReportingPeriodCommand(assignment, reportingPeriod);
        command.setWorkflowEnabled(configuration.get(Configuration.ENABLE_WORKFLOW));
        return command;

    }

    @Override
    protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, ControllerTools.getDateEditor(false));
        ControllerTools.registerDomainObjectEditor(binder, epochDao);
        ControllerTools.registerDomainObjectEditor(binder, treatmentAssignmentDao);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map referenceData(final HttpServletRequest request, final Object command, final Errors errors) throws Exception {
        Map<Object, Object> refDataMap = new LinkedHashMap<Object, Object>();
        ReportingPeriodCommand rpCommand = (ReportingPeriodCommand) command;
        refDataMap.put("fieldGroups", createFieldGroups(command));
        refDataMap.put("treatmentAssignments", fetchTreatmentAssignmentOptions(command));
        return refDataMap;
    }

    /**
     * Creates the fields that are displayed.
     *
     * @param command
     */
    protected Map<String, InputFieldGroup> createFieldGroups(Object command) {
        InputFieldGroupMap fieldMap = new InputFieldGroupMap();
        reportingPeriodFieldGroup = new DefaultInputFieldGroup(REPORTINGPERIOD_FIELD_GROUP);

        reportingPeriodFieldGroup.getFields().add(InputFieldFactory.createDateField("assignment.startDateOfFirstCourse", "Start date of first course", true));
        reportingPeriodFieldGroup.getFields().add(InputFieldFactory.createDateField("reportingPeriod.startDate", "Start date of course", true));
        reportingPeriodFieldGroup.getFields().add(InputFieldFactory.createDateField("reportingPeriod.endDate", "End date of course", true));
        reportingPeriodFieldGroup.getFields().add(InputFieldFactory.createSelectField("reportingPeriod.epoch", "Treatment type", true, createEpochOptions(command)));
        InputField cycleNumberField = InputFieldFactory.createNumberField("reportingPeriod.cycleNumber", "Course/cycle number", true);
        InputFieldAttributes.setSize(cycleNumberField, 2);
        reportingPeriodFieldGroup.getFields().add(cycleNumberField);
        
        fieldMap.addInputFieldGroup(reportingPeriodFieldGroup);
        return fieldMap;
    }


    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
        super.onBindAndValidate(request, command, errors);
        BeanWrapper commandBean = new BeanWrapperImpl(command);
        Map<String, InputFieldGroup> fieldGroups = createFieldGroups(command);
        for (InputFieldGroup fieldGroup : fieldGroups.values()) {
            for (InputField field : fieldGroup.getFields()) {
                field.validate(commandBean, errors);
            }
        }
        validateRepPeriodDates((ReportingPeriodCommand) command, fieldGroups, errors);
    }

    /**
     * Validate the form,if no errors found, save the Reporting period object. Then return to
     * the success view.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object cmd, BindException errors) throws Exception {
        ReportingPeriodCommand command = (ReportingPeriodCommand) cmd;

        AdverseEventReportingPeriod reportingPeriod = command.getReportingPeriod();
        reportingPeriod.setAssignment(command.getAssignment());

        //initialize the solicited AEs
        if (reportingPeriod.getAdverseEvents().isEmpty()) {
            for (SolicitedAdverseEvent sae : reportingPeriod.getEpoch().getArms().get(0).getSolicitedAdverseEvents()) {
                AdverseEvent adverseEvent = new AdverseEvent();
                adverseEvent.setSolicited(true);
                adverseEvent.setRequiresReporting(false);

                if (command.getStudy().getAeTerminology().getTerm() == Term.MEDDRA) {
                    AdverseEventMeddraLowLevelTerm aellt = new AdverseEventMeddraLowLevelTerm();
            		aellt.setLowLevelTerm(sae.getLowLevelTerm());
            		adverseEvent.setAdverseEventMeddraLowLevelTerm(aellt);
            		aellt.setAdverseEvent(adverseEvent);
                } else {
                    AdverseEventCtcTerm aeCtcTerm = new AdverseEventCtcTerm();
                    aeCtcTerm.setCtcTerm(sae.getCtcterm());
                    adverseEvent.setAdverseEventTerm(aeCtcTerm);
                    if(sae.getOtherTerm() != null)
                    	adverseEvent.setLowLevelTerm(sae.getOtherTerm());
                    aeCtcTerm.setAdverseEvent(adverseEvent);
                }
                reportingPeriod.addAdverseEvent(adverseEvent);
            }
        }
        
        adverseEventReportingPeriodDao.save(reportingPeriod);
        
        //call workflow, to enact
        if(command.isWorkflowEnabled())  adverseEventRoutingAndReviewRepository.enactReportingPeriodWorkflow(reportingPeriod);
        
        Map map = new LinkedHashMap();
        map.putAll(createFieldGroups(command));

        map.putAll(errors.getModel());
        ModelAndView modelAndView = new ModelAndView("ae/confirmReportingPeriod", map);

        return modelAndView;
    }

    /**
     * This method validates the dates of the reporting period created/edited.
     *
     * @param cmd
     * @return
     */
    protected void validateRepPeriodDates(ReportingPeriodCommand command, Map<String, InputFieldGroup> groups, BindException errors) {

        AdverseEventReportingPeriod rPeriod = command.getReportingPeriod();
        List<AdverseEventReportingPeriod> rPeriodList = command.getAssignment().getReportingPeriods();
        Date startDate = rPeriod.getStartDate();
        Date endDate = rPeriod.getEndDate();
        InputField endDateField = groups.get(REPORTINGPERIOD_FIELD_GROUP).getFields().get(2);

        // Check for duplicate baseline Reporting Periods.
        if (rPeriod.getEpoch() == null) {
            return;
        }

        if (rPeriod.getTreatmentAssignment() == null || rPeriod.getTreatmentAssignment().getId() == null) {
            errors.reject("", "Select the Treatment Assignment.");
            return;
        }

        if (rPeriod.getEpoch().getName().equals("Baseline")) {
            for (AdverseEventReportingPeriod aerp : rPeriodList) {
                if (!aerp.getId().equals(rPeriod.getId()) && aerp.getEpoch().getName().equals("Baseline")) {
                    InputField epochField = groups.get(REPORTINGPERIOD_FIELD_GROUP).getFields().get(3);
                    errors.rejectValue(epochField.getPropertyName(), "REQUIRED", "A Baseline treatment type already exists");
                    return;
                }
            }
        }

        // Check if the start date is equal to or before the end date.
        if (startDate != null && endDate != null && (endDate.getTime() - startDate.getTime() < 0)) {
            errors.rejectValue(endDateField.getPropertyName(), "REQUIRED", "End date cannot be earlier than Start date");
        }

        // Check if the start date is equal to end date.
        // This is allowed only for Baseline reportingPeriods and not for other reporting periods.
        if (!rPeriod.getEpoch().getName().equals("Baseline")) {
            if (startDate.equals(endDate)) {
                errors.rejectValue(endDateField.getPropertyName(), "REQUIRED", "For Non-Baseline treatment type Start date cannot be equal to End date");
            }

        }

        // Check if the start date - end date for the reporting Period overlaps with the
        // date range of an existing Reporting Period.
        for (AdverseEventReportingPeriod aerp : rPeriodList) {
            if (!aerp.getId().equals(rPeriod.getId())) {
                Date sDate = aerp.getStartDate();
                Date eDate = aerp.getEndDate();
                if (((sDate.getTime() - startDate.getTime() < 0) && startDate.getTime() - eDate.getTime() < 0) ||
                        ((sDate.getTime() - endDate.getTime() < 0) && (endDate.getTime() - eDate.getTime() < 0)) ||
                        ((startDate.getTime() - sDate.getTime() < 0) && (eDate.getTime() - endDate.getTime() < 0)) ||
                        (sDate.compareTo(startDate) == 0 && eDate.compareTo(endDate) == 0)) {
                    errors.rejectValue(endDateField.getPropertyName(), "REQUIRED", "Course cannot overlap with an existing course.");
                    break;
                }
            }
        }

        // If the epoch of reportingPeriod is not - Baseline , then it cannot be earlier than a Baseline
        // reportingPeriod of
        for (AdverseEventReportingPeriod aerp : rPeriodList) {
            Date sDate = aerp.getStartDate();
            Date eDate = aerp.getEndDate();
            if (rPeriod.getEpoch().getName().equals("Baseline")) {
                if (!aerp.getEpoch().getName().equals("Baseline")) {
                    if (sDate.getTime() - startDate.getTime() < 0) {
                        errors.rejectValue(endDateField.getPropertyName(), "REQUIRED", "Baseline treatment type cannot start after an existing Non-Baseline treatment type.");
                        return;
                    }
                }
            } else {
                if (aerp.getEpoch().getName().equals("Baseline")) {
                    if (startDate.getTime() - sDate.getTime() < 0) {
                        errors.rejectValue(endDateField.getPropertyName(), "REQUIRED", "Non-Baseline treatment type cannot start before an existing Baseline treatment type.");
                        return;
                    }
                }
            }
        }

    }


    public Map<Object, Object> fetchTreatmentAssignmentOptions(final Object cmd) {
        ReportingPeriodCommand rpCommand = (ReportingPeriodCommand) cmd;
        return WebUtils.collectOptions(rpCommand.getStudy().getTreatmentAssignments(), "id", "code", "Please select");
    }

    protected Map<Object, Object> createEpochOptions(final Object command) {
        Map<Object, Object> epochMap = new LinkedHashMap<Object, Object>();
        epochMap.put("", "Please select");
        ReportingPeriodCommand rpCommand = (ReportingPeriodCommand) command;
        Study study = rpCommand.getAssignment().getStudySite().getStudy();
        List<Epoch> epochList = study.getEpochs();
        for (Epoch epoch : epochList) {
            epochMap.put(epoch.getId(), epoch.getName());
        }
        return epochMap;
    }


    ///OBJECT PROPERTIES

    public AdverseEventReportingPeriodDao getAdverseEventReportingPeriodDao() {
        return adverseEventReportingPeriodDao;
    }

    @Required
    public void setAdverseEventReportingPeriodDao(AdverseEventReportingPeriodDao adverseEventReportingPeriodDao) {
        this.adverseEventReportingPeriodDao = adverseEventReportingPeriodDao;
    }

    public StudyParticipantAssignmentDao getAssignmentDao() {
        return assignmentDao;
    }

    @Required
    public void setAssignmentDao(StudyParticipantAssignmentDao assignmentDao) {
        this.assignmentDao = assignmentDao;
    }

    public StudyDao getStudyDao() {
        return studyDao;
    }

    @Required
    public void setStudyDao(StudyDao studyDao) {
        this.studyDao = studyDao;
    }

    @Required
    public void setEpochDao(EpochDao epochDao) {
        this.epochDao = epochDao;
    }

    public ParticipantDao getParticipantDao() {
        return participantDao;
    }

    @Required
    public void setParticipantDao(ParticipantDao participantDao) {
        this.participantDao = participantDao;
    }

    public TreatmentAssignmentDao getTreatmentAssignmentDao() {
        return treatmentAssignmentDao;
    }

    @Required
    public void setTreatmentAssignmentDao(TreatmentAssignmentDao treatmentAssignmentDao) {
        this.treatmentAssignmentDao = treatmentAssignmentDao;
    }
    
    public AdverseEventRoutingAndReviewRepository getAdverseEventRoutingAndReviewRepository() {
    	return adverseEventRoutingAndReviewRepository;
    }
    public void setAdverseEventRoutingAndReviewRepository(
		AdverseEventRoutingAndReviewRepository adverseEventRoutingAndReviewRepository) {
    	this.adverseEventRoutingAndReviewRepository = adverseEventRoutingAndReviewRepository;
    }
    
    public void setWorkflowConfigDao (WorkflowConfigDao workflowConfigDao){
    	this.workflowConfigDao = workflowConfigDao;
    }
    
    public WorkflowConfigDao getWorkflowConfigDao(){
    	return workflowConfigDao;
    }

    public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
    
	@Required
	public Configuration getConfiguration() {
		return configuration;
	}
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
