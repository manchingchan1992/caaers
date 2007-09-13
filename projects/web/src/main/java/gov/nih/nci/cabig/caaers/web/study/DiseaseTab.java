package gov.nih.nci.cabig.caaers.web.study;

import java.util.Map;

import gov.nih.nci.cabig.caaers.dao.DiseaseTermDao;
import gov.nih.nci.cabig.caaers.dao.meddra.LowLevelTermDao;
import gov.nih.nci.cabig.caaers.domain.CtepStudyDisease;
import gov.nih.nci.cabig.caaers.domain.MeddraStudyDisease;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.web.ae.RoutineAdverseEventInputCommand;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;

/**
 * @author Rhett Sutphin
 * @author Krikor Krumlian
 */
public class DiseaseTab extends StudyTab {
    private static Log log = LogFactory.getLog(DiseaseTab.class);

    private DiseaseTermDao diseaseTermDao;
    private LowLevelTermDao lowLevelTermDao;
    private InputFieldGroup healthyVolunteerFieldGroup;
    
    private static final String HEALTHY_VOLUNTEER_FIELD_GROUP = "healthyVolunteer";
    

    public DiseaseTab() {
        super("Study Disease", "Disease", "study/study_diseases");
        
        healthyVolunteerFieldGroup = new DefaultInputFieldGroup(HEALTHY_VOLUNTEER_FIELD_GROUP);
        healthyVolunteerFieldGroup.getFields().add(InputFieldFactory.createBooleanSelectField("healthyVolunteer", "healthyVolunteer", true));
    }

    @Override
    public void postProcess(HttpServletRequest request, Study command, Errors errors) {
        handleStudyDiseaseAction(command, request.getParameter("_action"),
            request.getParameter("_selected"));
    }

    
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, InputFieldGroup> createFieldGroups(Study command) {
        InputFieldGroupMap map = new InputFieldGroupMap();
        map.addInputFieldGroup(healthyVolunteerFieldGroup);
        return map;
    }
    
    private void handleStudyDiseaseAction(Study study, String action, String selected) {
        if ("addMeddraStudyDisease".equals(action) && study.getDiseaseLlt().length() > 0 ) {
            String diseaseCode = study.getDiseaseLlt();
            MeddraStudyDisease meddraStudyDisease = new MeddraStudyDisease();
            meddraStudyDisease.setMeddraCode(diseaseCode);
            meddraStudyDisease.setTerm(lowLevelTermDao.getById(Integer.parseInt(diseaseCode)) == null ?
                lowLevelTermDao.getById(1) : lowLevelTermDao.getById(Integer.parseInt(diseaseCode)));
            study.addMeddraStudyDisease(meddraStudyDisease);
        }
        if ("removeMeddraStudyDisease".equals(action)) {
            study.getMeddraStudyDiseases().remove(Integer.parseInt(selected));
        }


        if ("addStudyDisease".equals(action)) {
            String[] diseases = study.getDiseaseTermIds();
            log.debug("Study Diseases Size : " + study.getCtepStudyDiseases().size());
            for (String diseaseId : diseases) {
                log.debug("Disease Id : " + diseaseId);
                //StudyDisease studyDisease = new StudyDisease();
                //studyDisease.setDiseaseTerm(diseaseTermDao.getById(Integer.parseInt(diseaseId)));
                //study.addStudyDisease(studyDisease);
                CtepStudyDisease ctepStudyDisease = new CtepStudyDisease();
                ctepStudyDisease.setTerm(diseaseTermDao.getById(Integer.parseInt(diseaseId)));
                study.addCtepStudyDisease(ctepStudyDisease);

            }
        } else if ("removeStudyDisease".equals(action)) {
            study.getCtepStudyDiseases().remove(Integer.parseInt(selected));
        }
    }

    public void setDiseaseTermDao(DiseaseTermDao diseaseTermDao) {
        this.diseaseTermDao = diseaseTermDao;
    }
    public void setLowLevelTermDao(LowLevelTermDao lowLevelTermDao) {
        this.lowLevelTermDao = lowLevelTermDao;
    }
}
