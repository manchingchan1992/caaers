package gov.nih.nci.cabig.caaers.web.fields;

import java.util.Map;

/**
 * Collects the basic information about a form field in a Spring MVC application.
 * The goal is for fields to be (mostly) rendered automatically and consistently,
 * including labels and the "required" indicator.
 * <p>
 * Also enables (but does not implement) some simple automatic validation
 * (via the required flag).
 *
 * @see TabWithFields
 * @see InputFieldGroup
 * @author Rhett Sutphin
 */
public interface InputField {
    String DETAILS = "details";
    String OPTIONS = "options";

    Category getCategory();

    /** @return the lowercased name of the value returned by {@link #getCategory} */
    String getCategoryName();

    String getDisplayName();

    boolean isRequired();

    String getPropertyName();

    Map<String, Object> getAttributes();

    enum Category {
        TEXT, TEXTAREA, DATE, SELECT, AUTOCOMPLETER
    }
}
