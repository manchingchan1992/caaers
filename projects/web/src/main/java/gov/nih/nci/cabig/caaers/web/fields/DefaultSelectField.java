package gov.nih.nci.cabig.caaers.web.fields;

import java.util.Map;

/**
 * @author Rhett Sutphin
 */
public class DefaultSelectField extends AbstractInputField {
    public DefaultSelectField() { }

    public DefaultSelectField(
        String propertyName, String displayName, boolean required, Map<Object, Object> options
    ) {
        super(propertyName, displayName, required);
        setOptions(options);
    }

    @Override
    public Category getCategory() {
        return Category.SELECT;
    }

    public Map<Object, Object> getOptions() {
        return (Map<Object, Object>) getAttributes().get(OPTIONS);
    }

    public void setOptions(Map<Object, Object> options) {
        getAttributes().put(OPTIONS, options);
    }
}
