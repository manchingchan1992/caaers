package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;

import java.util.Collections;
import java.util.Map;

/**
 * @author Rhett Sutphin
 */
public class EmptyAeTab extends AeTab {
    public EmptyAeTab(String longTitle, String shortTitle, String viewName) {
        super(longTitle, shortTitle, viewName);
    }

    @Override
    public Map<String, InputFieldGroup> createFieldGroups(AdverseEventInputCommand command) {
        return Collections.emptyMap();
    }
}
