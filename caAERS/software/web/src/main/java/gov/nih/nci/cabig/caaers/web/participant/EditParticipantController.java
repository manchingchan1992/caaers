/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.participant;

import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.security.CaaersSecurityFacade;
import gov.nih.nci.cabig.caaers.security.SecurityUtils;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.web.study.StudyCommand;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;
import gov.nih.nci.cabig.ctms.web.chrome.Task;
import gov.nih.nci.cabig.ctms.web.tabs.Flow;
import gov.nih.nci.cabig.ctms.web.tabs.FlowFactory;
import gov.nih.nci.cabig.ctms.web.tabs.Tab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Ion C. Olaru
 */

public class EditParticipantController<T extends ParticipantInputCommand> extends ParticipantController<T> {

    private static final Log log = LogFactory.getLog(EditParticipantController.class);
    private Task task;
    private ConfigProperty configurationProperty;
    private CaaersSecurityFacade csf;

    public EditParticipantController() {
        setBindOnNewForm(true);
    }

    @Override
    protected Object formBackingObject(final HttpServletRequest request) throws ServletException {

        request.getSession().removeAttribute(CreateParticipantAjaxFacade.CREATE_PARTICIPANT_FORM_NAME);
        request.getSession().removeAttribute(getReplacedCommandSessionAttributeName(request));

        Participant participant = participantRepository.getParticipantById(Integer.parseInt(request.getParameter("participantId")));
        StudyParticipantAssignment assignment = null;
        Integer assignmentId = null;
        try {
            assignmentId = WebUtils.getIntParameter(request, "assignmentId");
        } catch (Exception e) {
        }

        if (assignmentId != null) {
            assignment = assignmentDao.getById(assignmentId);
        }

        EditParticipantCommand cmd = new EditParticipantCommand(participant);
        cmd.setUnidentifiedMode(getUnidentifiedMode());
        List<StudyParticipantAssignment> assignments = participant.getAssignments();

        cmd.setHasParUpdate(csf.checkAuthorization(SecurityUtils.getAuthentication(), "gov.nih.nci.cabig.caaers.domain.Participant:UPDATE"));

        // store StudySites from Participant object to Command object
        List<StudySite> studySites = new ArrayList<StudySite>();
        for (StudyParticipantAssignment studyParticipantAssignment : assignments) {
            studySites.add(studyParticipantAssignment.getStudySite());
        }
        cmd.setStudySites(studySites);

        if (participant.getAssignments().size() > 0)
            cmd.setOrganization(participant.getAssignments().get(0).getStudySite().getOrganization());

        populateCommandFromAssignment(assignment, cmd);
        return cmd;

    }

    @Override
    protected boolean suppressValidation(HttpServletRequest request, Object command, BindException errors) {
        int curPage = getCurrentPage(request);
        int targetPage = getTargetPage(request, curPage);
        EditParticipantCommand c = (EditParticipantCommand)command;

        // Do partial validation on Subject Details page, skipping Assignment selection if user just saves the page
        if (targetPage == 1 && curPage == 1) {
            c.getAdditionalParameters().put("DO_PARTIAL_VALIDATION", "");
        }
        return super.suppressValidation(request, command, errors);
    }

    private void populateCommandFromAssignment(StudyParticipantAssignment spa, EditParticipantCommand c) {
        if (spa != null) {
            StudySite site = getStudySiteDao().getById(spa.getStudySite().getId());
            c.setAssignment(spa);
            c.setStudy(site.getStudy());
            c.refreshStudyDiseases();
        }
    }

    @Override
    protected boolean isSummaryEnabled() {
        return true;
    }

    /**
     * Building Flow Pages.
     * */
    @Override
    public FlowFactory<T> getFlowFactory() {
        return new FlowFactory<T>() {
            public Flow<T> createFlow(T cmd) {
                Flow<T> flow = new Flow<T>("Edit Subject");
                flow.addTab(new EditParticipantReviewParticipantTab());
                flow.addTab(new EditParticipantTab());
                flow.addTab(new EditSubjectMedHistoryTab());
                return flow;
            }
        };
    }
    
    @Override
    protected ModelAndView processFinish(final HttpServletRequest request, final HttpServletResponse response, final Object command, final BindException errors) throws Exception {
        ParticipantInputCommand participantCommand = (ParticipantInputCommand) command;
        Participant participant = participantCommand.getParticipant();
        participantDao.merge(participant);

/*
        request.setAttribute("flashMessage", "Successfully updated the Participant");
        ModelAndView modelAndView = new ModelAndView("par/par_confirm");
*/

        response.sendRedirect("view?participantId=" + participant.getId() + "&type=edit");
        return null;
    }

    @Override
    protected boolean shouldSave(HttpServletRequest request, T command, Tab<T> tTab) {
        if (isAjaxRequest(request)) return false;
        return super.shouldSave(request, command, tTab);
    }

    protected Object currentFormObject(HttpServletRequest request, Object oCommand) throws Exception {
        try {
			ParticipantInputCommand cmd = (ParticipantInputCommand) oCommand;

			String p = request.getParameter("_asyncMethodName");
			if (p == null || !(p.equals("addOrganizationIdentifier") || p.equals("removeOrganizationIdentifier") || p.equals("addSystemIdentifier") || p.equals("removeSystemIdentifier"))) {
			    participantDao.reassociate(cmd.getParticipant());
			}
		}catch (HibernateOptimisticLockingFailureException  e) {
			log.warn("Optimistic locking error, while reassociating the report", e);
			request.setAttribute("OPTIMISTIC_LOCKING_ERROR", e);
		}

        return super.currentFormObject(request, oCommand);
    }

    protected T save(T command, Errors errors) {
        ParticipantInputCommand participantCommand = (ParticipantInputCommand) command;
        participantDao.merge(participantCommand.getParticipant());
        return command;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map referenceData(final HttpServletRequest request, final Object command, final Errors errors, final int page) throws Exception {
        ParticipantInputCommand c = (ParticipantInputCommand)command;
        Map<String, Object> refdata = super.referenceData(request, command, errors, page);
        refdata.put("currentTask", task);

        if (getFlow((T)command).getTabCount() == page + 1) {
            refdata.put("_finish", true);
        }
        return refdata;
    }

    @Override
    protected boolean suppressValidation(HttpServletRequest request, Object cmd) {
        ParticipantInputCommand command = (ParticipantInputCommand) cmd;

        // supress validation when target page is less than current page.
        int curPage = getCurrentPage(request);
        int targetPage = getTargetPage(request, curPage);

        if (targetPage < curPage || curPage == 0) return true;
        
        // supress for ajax and delete requests
        if (isAjaxRequest(request) && !StringUtils.equals("save", command.getTask())) return true;
        return super.suppressValidation(request, command);
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors, int page) throws Exception {
        super.onBindAndValidate(request, command, errors, page);
        ParticipantInputCommand cmd = (ParticipantInputCommand) command;

        // if the target tab is not the next to the current one
        if (getTargetPage(request, command, errors, page) - page > 1) {

            // if there is only 1 assignment for the participant, then we fetch it an populate the command accordingly.
            if (cmd.getParticipant().getAssignments().size() == 1) {
                populateCommandFromAssignment(cmd.getParticipant().getAssignments().get(0), (EditParticipantCommand)cmd);
            }

            // if the assignment object needed by SubjectMedHistoryTab is not in the command
            if (cmd.assignment == null || cmd.assignment.getId() == null) {
                Class c = getTab((T) command, getTargetPage(request, command, errors, page)).getClass();
                if (c == EditSubjectMedHistoryTab.class)
                    errors.reject("ERR_SELECT_STUDY_FROM_DETAILS", "Please select a study from the \"Details\" tab");
            }
        }

    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public ConfigProperty getConfigurationProperty() {
        return configurationProperty;
    }

    public void setConfigurationProperty(ConfigProperty configurationProperty) {
        this.configurationProperty = configurationProperty;
    }

    public CaaersSecurityFacade getCsf() {
        return csf;
    }

    public void setCsf(CaaersSecurityFacade csf) {
        this.csf = csf;
    }
}
