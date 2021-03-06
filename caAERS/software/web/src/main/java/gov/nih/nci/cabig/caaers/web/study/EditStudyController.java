/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.study;

import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.security.SecurityUtils;
import gov.nih.nci.cabig.caaers.utils.ExpectedAETermSorter;
import gov.nih.nci.cabig.ctms.web.chrome.Task;
import gov.nih.nci.cabig.ctms.web.tabs.Flow;
import gov.nih.nci.cabig.ctms.web.tabs.FlowFactory;
import gov.nih.nci.cabig.ctms.web.tabs.Tab;

import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Priyatam
 * @author <a href="mailto:biju.joseph@semanticbits.com">Biju Joseph</a>
 */
public class EditStudyController extends StudyController<StudyCommand> {

    private static final Log log = LogFactory.getLog(EditStudyController.class);
    private Task task;


	public EditStudyController() {
        setBindOnNewForm(true);
    }

    // /LOGIC

    @Override
    protected Object formBackingObject(final HttpServletRequest request) throws ServletException {
        request.getSession().removeAttribute(getReplacedCommandSessionAttributeName(request));
        request.getSession().removeAttribute(CreateStudyAjaxFacade.CREATE_STUDY_FORM_NAME);

        Study study = studyDao.getStudyDesignById(Integer.parseInt(request.getParameter("studyId")));
        if (log.isDebugEnabled()) {
            log.debug("Retrieved Study :" + String.valueOf(study));
        }

        study.initializeEpocsIfNecessary();

        StudyCommand command = new StudyCommand(studyDao, investigationalNewDrugDao);
        command.setMustFireEvent(false);

        command.setStudy(study);
        command.setAllPersonnelRoles(configPropertyRepository.getByType(ConfigPropertyType.RESEARCH_STAFF_ROLE_TYPE));
        command.setAllInvestigatorRoles(configPropertyRepository.getByType(ConfigPropertyType.INVESTIGATOR_ROLE_TYPE));
        command.populateRoleNamesMap();
        command.setWorkflowEnabled(getConfiguration().get(getConfiguration().ENABLE_WORKFLOW));
        command.setStudyRepository(this.getStudyRepository());

        command.setPrevFS(command.getStudy().getPrimaryFundingSponsor());
        command.setPrevCC(command.getStudy().getStudyCoordinatingCenter());
        Collections.sort(command.getStudy().getExpectedAEMeddraLowLevelTerms(), new ExpectedAETermSorter());
        Collections.sort(command.getStudy().getExpectedAECtcTerms(), new ExpectedAETermSorter());
        return command;
    }

    /**
     * Flow pages build
     * */
    @Override
    public FlowFactory<StudyCommand> getFlowFactory() {
        return new FlowFactory<StudyCommand>() {
            public Flow<StudyCommand> createFlow(StudyCommand cmd) {
                Flow<StudyCommand> flow = new Flow<StudyCommand>("Edit Study");
                
                flow.addTab(new EmptyStudyTab("Overview", "Overview", "study/study_reviewsummary"));

				if (cmd.getSupplementalInfoManager() || (cmd.isDataEntryComplete() && cmd.getStudyQAManager()) || (!cmd.isDataEntryComplete() && cmd.getStudyCreator())) {
					flow.addTab(new DetailsTab());
				}

                if(cmd.getSupplementalInfoManager()) {
                    flow.addTab(new AgentsTab());
                    flow.addTab(new TreatmentAssignmentTab());
                    flow.addTab(new DiseaseTab());
                    flow.addTab(new SolicitedAdverseEventTab());
                    flow.addTab(new ExpectedAEsTab());
                }
                
                if (SecurityUtils.checkAuthorization(UserGroupType.study_site_participation_administrator)){
                    flow.addTab(new SitesTab());
                }
                
                if (SecurityUtils.checkAuthorization(UserGroupType.study_team_administrator)){
                    flow.addTab(new InvestigatorsTab());
                    flow.addTab(new PersonnelTab());
                }
                
                if (cmd.getSupplementalInfoManager()){
                    flow.addTab(new IdentifiersTab());
                }
                
                return flow;
            }
        };

    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map referenceData(final HttpServletRequest request, final Object command, final Errors errors, final int page) throws Exception {
        Map<String, Object> refdata = super.referenceData(request, command, errors, page);

        refdata.put("currentTask", task);
        StudyCommand cmd = (StudyCommand) command;
        Study study = cmd.getStudy();
        
        refdata.put("editFlow", true);
        return refdata;
    }

    @Override
    protected ModelAndView processFinish(final HttpServletRequest request, final HttpServletResponse response, final Object command, final BindException errors) throws Exception {
        
        StudyCommand studyCommand = (StudyCommand) command;
        studyCommand.save();
        //only if we visited Personnel, Investigators page - mustFireEvent will be true,
        if(studyCommand.isMustFireEvent())  getEventFactory().publishEntityModifiedEvent(studyCommand.getStudy());

        
        return new ModelAndView(new RedirectView("search"));
    }

    @Override
    protected boolean shouldSave(final HttpServletRequest request, final StudyCommand command, final Tab<StudyCommand> tab) {
        return super.shouldSave(request, command, tab) && tab.getNumber() != 0; // dont study if it overview page
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

}
