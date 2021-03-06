/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.fields;

import gov.nih.nci.cabig.caaers.validation.fields.validators.FieldValidator;

import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.validation.Errors;

/**
 * Collects the basic information about a form field in a Spring MVC application. The goal is for
 * fields to be (mostly) rendered automatically and consistently, including labels and the
 * "required" indicator.
 * <p/>
 * Also enables (but does not implement) some simple automatic validation (via the required flag).
 *
 * @author Rhett Sutphin
 * @author Biju Joseph
 * 
 * @see TabWithFields
 * @see InputFieldGroup
 */
public interface InputField {
    String DETAILS = "details";

    String OPTIONS = "options";

    String SUBFIELDS = "subfields";

    String HELP = "help"; // refers to the help text key in messages.properties

    String SIZE = "size"; // size of the field (applied only for Text fields/AutoCompleters)
    String MAX_LENGTH = "maxlength";

    // TODO: I don't think we need this; clear should probably be available for every autocompleter
    // - RMS20070725
    String ENABLE_CLEAR = "enableClear"; // enables the clear button (only for AutoCompleters)

    String COLS = "cols";

    String ROWS = "rows";

    String ENABLE_DELETE = "enableDelete"; // will put delete symbol near to the field(in
    // renderRow.tag).

    String DEFAULT_VALUE = "defaultValue";

    //Below fields are used for SPLIT_DATE kind of fields.
    String MONTH_REQUIRED = "mmRequired";
    String DAY_REQUIRED = "ddRequired";
    String YEAR_REQUIRED = "yyRequired";
    
    String MONTH_MANDATORY = "mmMandatory";
    String DAY_MANDATORY = "ddMandatory";
    String YEAR_MANDATORY = "yyMandatory";


    String EXTRA_VALUE_PARAMS = "extraParams";
    
    String LABEL_PROPERTY = "labelProperty";

    /** The field type */
    Category getCategory();

    /**
     * @return the lowercased name of the value returned by {@link #getCategory}
     */
    String getCategoryName();

    String getDisplayName();

    /**
     * Will be true if the field is "Required" 
     * @return
     */
    boolean isRequired();

    /**
     * Will be true, if the property represented by this field can be modified.
     * @return
     */
    boolean isModifiable();
    void setModifiable(boolean modifiable);

    /**
     * Will be true if the property represented by this field can be read
     * @return
     */
    boolean isReadable();
    void setReadable(boolean readable);
    
    /**
     * Will represent the property that this field should represent when the field is readOnly. 
     * @return
     */
    String getDisplayTextProperty();


    /**
     * Will return true, if the property represented by this field can be validated. 
     * @return
     */
    boolean isValidateable();

    /**
     * The security privilege needed to Modify this field
     * @return
     */
    String getPrivilegeToModify();
    void setPrivilegeToModify(String privilege);

    /**
     * The privilege required to read this field. 
     * @return
     */
    String getPrivilegeToRead();
    void setPrivilegeToRead(String privilege);

    void validate(BeanWrapper commandBean, Errors errors);

    /**
     * Returns an array of validators configured for this field.
     *
     * @return {@link FieldValidator}
     */
    FieldValidator[] getValidators();

    String getValidatorClassName();

    String getPropertyName();

    Map<String, Object> getAttributes();

    FieldValidator  getValidatorOfType(Class<? extends FieldValidator> klass);

    enum Category {
        TEXT, TEXTAREA, DATE, SPLIT_DATE, SELECT, AUTOCOMPLETER, COMPOSITE, CHECKBOX, LONGSELECT, INPLACE_TEXT, LABEL, RADIO, IMAGE , HIDDEN
    }
}
