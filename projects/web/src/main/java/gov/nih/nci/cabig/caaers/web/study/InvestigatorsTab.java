package gov.nih.nci.cabig.caaers.web.study;

import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.fields.RepeatingFieldGroupFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.Errors;

/**
 * @author Rhett Sutphin
*/
class InvestigatorsTab extends StudyTab {
	private List<InputField> fields;

    public InvestigatorsTab() {
        super("Study Investigators", "Investigators", "study/study_investigators");
    }

    @Override
    public Map<String, Object> referenceData() {
        Map<String, Object> refdata = super.referenceData();
        addConfigMapToRefdata(refdata, "invRoleCodeRefData");
        addConfigMapToRefdata(refdata, "invStatusCodeRefData");
        return refdata;
    }

    @Override
	public void postProcess(HttpServletRequest request, Study command, Errors errors) {
		String action =request.getParameter("_action");
		String selectedInvestigator = request.getParameter("_selectedInvestigator");
		String prevSiteIndex = request.getParameter("_prevSite");
		Study study = command;
		int selectedIndex = study.getStudySiteIndex();
		if ("removeInv".equals(action) && selectedIndex >=0 ){
			study.getStudySites().get(selectedIndex).getStudyInvestigators()
				.remove(Integer.parseInt(selectedInvestigator));
		}else if ("changeSite".equals(action) && errors.hasErrors()){
			int siteIndex = Integer.parseInt(prevSiteIndex);
			study.setStudySiteIndex(siteIndex);
			if(siteIndex >= 0){
				study.getStudySites().get(siteIndex).getStudyInvestigators().get(0);
			}
		}
    }

	@Override
	public Map<String, InputFieldGroup> createFieldGroups(Study command) {
		InputFieldGroupMap map = new InputFieldGroupMap();
		InputFieldGroup siteFieldGroup = new DefaultInputFieldGroup("site");
		siteFieldGroup.getFields().add(InputFieldFactory.createSelectField("studySiteIndex", "Site", true,
				InputFieldFactory.collectOptions(collectStudyOrganizations(command), "code", "desc")));
		map.addInputFieldGroup(siteFieldGroup);

		if(fields == null){
			fields = new ArrayList<InputField>();
			InputField investigatorField = InputFieldFactory.createAutocompleterField("siteInvestigator", "Investigator", true);
			//sponsorField.getAttributes().put(InputField.DETAILS,"Enter a portion of the investigator name you are looking for");
			fields.add(investigatorField);
			fields.add(InputFieldFactory.createSelectField("roleCode", "Role", true,
					collectOptionsFromConfig("invRoleCodeRefData", "desc","desc")));
			fields.add(InputFieldFactory.createSelectField("statusCode", "Status", true,
					collectOptionsFromConfig("invStatusCodeRefData", "desc","desc")));
		}

		int ssIndex = command.getStudySiteIndex();
		if(ssIndex >= 0){
			RepeatingFieldGroupFactory rfgFactory = new RepeatingFieldGroupFactory("main", "studyOrganizations[" + ssIndex +"].studyInvestigators");
			for(InputField f : fields){
				rfgFactory.addField(f);
			}
			map.addRepeatingFieldGroupFactory(rfgFactory, command.getStudySites().get(ssIndex).getStudyInvestigators().size());
		}
		return map;
	}


}
