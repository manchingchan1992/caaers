package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldAttributes;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.fields.TabWithFields;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.validation.Errors;

/**
 * @author Saurabh Agrawal
 */
public class ResearchStaffTab extends TabWithFields<ResearchStaff> {

	protected static final Log log = LogFactory.getLog(ResearchStaffTab.class);

	private static final String RESEARCH_STAFF_FIELD_GROUP = "researchStaff";

	public ResearchStaffTab() {
		super("Research Staff Details", "Research Staff Details", "admin/research_staff_details");
		setAutoPopulateHelpKey(true);
	}

	@Override
	public Map<String, InputFieldGroup> createFieldGroups(final ResearchStaff command) {
		InputFieldGroup researchStaffFieldGroup;

		researchStaffFieldGroup = new DefaultInputFieldGroup(RESEARCH_STAFF_FIELD_GROUP);

		InputField firstNameField = InputFieldFactory.createTextField("firstName", "First name", true);
		InputFieldAttributes.setSize(firstNameField, 30);
		researchStaffFieldGroup.getFields().add(firstNameField);

		InputField middleNameField = InputFieldFactory.createTextField("middleName", "Middle name", false);
		InputFieldAttributes.setSize(middleNameField, 30);
		researchStaffFieldGroup.getFields().add(middleNameField);

		InputField lastNameField = InputFieldFactory.createTextField("lastName", "Last name", true);
		InputFieldAttributes.setSize(lastNameField, 30);
		researchStaffFieldGroup.getFields().add(lastNameField);

		InputField emailAddressField = InputFieldFactory.createTextField("emailAddress", "Email address", true);
		InputFieldAttributes.setSize(emailAddressField, 30);
		researchStaffFieldGroup.getFields().add(emailAddressField);

		InputField phoneNumberField = InputFieldFactory.createTextField("phoneNumber", "Phone", true);
		InputFieldAttributes.setSize(phoneNumberField, 30);
		researchStaffFieldGroup.getFields().add(phoneNumberField);

		InputField faxNumberField = InputFieldFactory.createTextField("faxNumber", "Fax", false);
		InputFieldAttributes.setSize(faxNumberField, 30);
		researchStaffFieldGroup.getFields().add(faxNumberField);

		Map<Object, Object> options = new LinkedHashMap<Object, Object>();
		options.put("", "Please select");
		options.putAll(InputFieldFactory.collectOptions(organizationDao.getAll(), "id", "name"));

		researchStaffFieldGroup.getFields().add(
				InputFieldFactory.createSelectField("organization", "Site", true, options));

		InputFieldGroupMap map = new InputFieldGroupMap();
		map.addInputFieldGroup(researchStaffFieldGroup);

		return map;
	}

	@Override
	protected void validate(final ResearchStaff command, final BeanWrapper commandBean,
			final Map<String, InputFieldGroup> fieldGroups, final Errors errors) {
		super.validate(command, commandBean, fieldGroups, errors);
	}

	private OrganizationDao organizationDao;

	public void setOrganizationDao(final OrganizationDao organizationDao) {
		this.organizationDao = organizationDao;
	}
}