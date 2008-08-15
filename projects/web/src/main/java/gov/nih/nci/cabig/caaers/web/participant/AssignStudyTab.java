package gov.nih.nci.cabig.caaers.web.participant;

import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.repository.StudyRepository;
import gov.nih.nci.cabig.caaers.web.fields.TabWithFields;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.ctms.web.tabs.Tab;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssignStudyTab extends TabWithFields<AssignParticipantStudyCommand> {
    private static final Log log = LogFactory.getLog(AssignStudyTab.class);

    private StudyRepository studyRepository;

    public AssignStudyTab() {
        super("Search for Studies", "Search Study", "par/reg_protocol_search");
    }

    @Override
    public Map<String, Object> referenceData(AssignParticipantStudyCommand command) {
        Map<String, Object> refdata = super.referenceData(command);
        refdata.put("assignType", "study");
        return refdata;
    }

    @Override
    public void onDisplay(HttpServletRequest request, AssignParticipantStudyCommand command) {
        super.onDisplay(request, command);

        String searchType = command.getStudyType();
        String searchText = command.getStudyText();
        log.debug("Search text : " + searchText + "Type : " + searchType);
        List<Study> studies = null;
        if (StringUtils.isEmpty(searchText)) return; // no search string.
        Study exampleStudy = new Study();
        if (StringUtils.equals("st", searchType)) {
            exampleStudy.setShortTitle(searchText);
        } else if (StringUtils.equals("lt", searchType)) {
            exampleStudy.setLongTitle(searchText);
        } else if (StringUtils.equals("idtf", searchType)) {
            Identifier identifier = new Identifier();
            identifier.setValue(searchText);
            exampleStudy.addIdentifier(identifier);
        }
        try {
            studies = studyRepository.search(exampleStudy);
        } catch (Exception ex) {
            log.error("Error in search", ex);
        }
        command.setStudies(studies);
        command.setStudyText("");

    }

/*
    @Override
    public void validate(AssignParticipantStudyCommand command, Errors errors) {
        super.validate(command, errors);
        if (command.getStudyId() == null) errors.rejectValue("studyId", "REQUIRED",
                        "Study not selected");
        if (command.getStudySiteId() == null) errors.rejectValue("studySiteId", "REQUIRED",
                        "Study Site not selected");
    }
*/

    @Override
    public void postProcess(HttpServletRequest request, AssignParticipantStudyCommand command,
                    Errors errors) {
        super.postProcess(request, command, errors);
        // identify the study site and keep it in the command.
        ArrayList<StudySite> studySites = new ArrayList<StudySite>();
        for (Study study : command.getStudies()) {
            if (study.getId().equals(command.getStudyId())) {
                for (StudySite studySite : study.getStudySites()) {
                    if (studySite.getId().equals(command.getStudySiteId())) {
                        studySites.add(studySite);
                        command.setStudySites(studySites);
                        break;
                    }
                }

                break;
            }
        }
    }

    public void setStudyRepository(final StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    public Map<String, InputFieldGroup> createFieldGroups(AssignParticipantStudyCommand command) {
        InputFieldGroupMap map = new InputFieldGroupMap();
        return map;
    }
}