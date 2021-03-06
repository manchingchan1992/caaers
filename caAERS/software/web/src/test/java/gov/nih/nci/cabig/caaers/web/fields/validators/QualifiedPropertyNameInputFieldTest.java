/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.fields.validators;

import java.util.HashMap;
import java.util.Map;

import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.validation.fields.validators.FieldValidator;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.fields.QualifiedPropertyNameInputField;

import org.apache.commons.lang.StringUtils;

/**
 * @author Biju Joseph
 */

public class QualifiedPropertyNameInputFieldTest extends AbstractTestCase {
    private QualifiedPropertyNameInputField field;

    public void testGetValidatorClassNameIfOnlySingleValidatorIsUsed() throws Exception {
        InputField dateField = InputFieldFactory.createPastDateField("propertyName", "displayName", false);
        createField(dateField);
        assertNotNull(field.getValidators());
        assertTrue(field.getValidators().length > 0);
        assertEquals("commons-validations.js uses this class name for validation", "validate-DATE", field.getValidatorClassName());
    }


    public void testGetValidatorClassNameForRequiredTextField() throws Exception {
        field = new TestQualifiedPropertyNameInputField(InputFieldFactory.createTextField("propertyName", "displayName", true)) {
            @Override
            public Category getCategory() {
                return Category.TEXT;
            }
        };

        assertEquals("commons-validations.js uses this class name for validation", "validate-NOTEMPTY$$MAXLENGTH2000", field.getValidatorClassName());
    }

    
    public void testGetValidatorNameWithUpdatedSize() throws Exception {
        field = new TestQualifiedPropertyNameInputField(InputFieldFactory.createNumberField("propertyName", "displayName", true, FieldValidator.createTextSizeValidator(4))) {
            @Override
            public Category getCategory() {
                return Category.TEXT;
            }
        };
        assertEquals("commons-validations.js uses this class name for validation", "validate-NOTEMPTY$$NUMERIC$$TEXT$$MAXLENGTH4", field.getValidatorClassName());
    }

    public void testGetValidatorClassNameForRequiredTextAreaField() throws Exception {
        field = new TestQualifiedPropertyNameInputField(InputFieldFactory.createTextArea("propertyName", "displayName", true)) {
            @Override
            public Category getCategory() {
                return Category.TEXTAREA;
            }
        };

        assertEquals("commons-validations.js uses this class name for validation", "validate-NOTEMPTY$$MAXLENGTH2000", field.getValidatorClassName());
    }

    public void testGetValidatorClassNameForNonRequiredTextField() throws Exception {

        field = new TestQualifiedPropertyNameInputField(InputFieldFactory.createTextField("propertyName", "displayName", false)) {
            @Override
            public Category getCategory() {
                return Category.TEXT;
            }
        };

        assertEquals("commons-validations.js uses this class name for validation", "validate-MAXLENGTH2000", field.getValidatorClassName());
    }

    public void testGetValidatorClassNameForNonRequiredTextAreaField() throws Exception {
        field = new TestQualifiedPropertyNameInputField(InputFieldFactory.createTextArea("propertyName", "displayName", false)) {
            @Override
            public Category getCategory() {
                return Category.TEXTAREA;
            }
        };


        assertEquals("commons-validations.js uses this class name for validation", "validate-MAXLENGTH2000", field.getValidatorClassName());
    }

    public void testGetValidatorClassNameIfNoValidatorsAreApplied() throws Exception {
        createField(InputFieldFactory.createInputField(InputField.Category.IMAGE, "propertyName", "displayName"));

        assertTrue("class name muast be empty", StringUtils.isEmpty(field.getValidatorClassName()));
    }

    public void testGetValidatorClassNameIfMultipleValidatorIsUsed() throws Exception {
        createField(InputFieldFactory.createEmailField("propertyName", "displayName", true));

        assertEquals("commons-validations.js uses this class name for validation", "validate-NOTEMPTY$$EMAIL$$MAXLENGTH2000", field.getValidatorClassName());
    }

    public void testReadable(){
        InputField nameField = InputFieldFactory.createTextField("test", "test", false);
        nameField.setReadable(true);
        
        
        createField(nameField);
        assertTrue(field.isReadable());
        field.setReadable(false);
        assertFalse(field.isReadable());
        assertFalse(nameField.isReadable());
    }

    public void testValidatable(){
        InputField nameField = InputFieldFactory.createTextField("test", "test", false);
        nameField.setReadable(true);

        createField(nameField);
        assertTrue(field.isReadable());
        assertTrue(field.isValidateable());
        nameField.setModifiable(false);
        assertFalse(field.isValidateable());
    }


    public void testSetPrivilegeToModify(){
        InputField nameField = InputFieldFactory.createTextField("test", "test", false);
        nameField.setReadable(true);

        createField(nameField);

        assertNull(field.getPrivilegeToModify());
        nameField.setPrivilegeToModify("abcd:efg");
        
        assertEquals("abcd:efg", field.getPrivilegeToModify());

        field.setPrivilegeToModify("kk:kk");
        assertEquals("kk:kk", nameField.getPrivilegeToModify());

    }


    public void testSetPrivilegeToRead(){
         InputField nameField = InputFieldFactory.createTextField("test", "test", false);
        nameField.setReadable(true);

        createField(nameField);

        assertNull(field.getPrivilegeToRead());
        nameField.setPrivilegeToRead("abcd:efg");

        assertEquals("abcd:efg", field.getPrivilegeToRead());

        field.setPrivilegeToRead("kk:kk");
        assertEquals("kk:kk", nameField.getPrivilegeToRead());
    }


    private void createField(final InputField dateField) {
        field = new TestQualifiedPropertyNameInputField(dateField);

    }

    private static class TestQualifiedPropertyNameInputField extends QualifiedPropertyNameInputField {
        public TestQualifiedPropertyNameInputField(InputField field) {
            super(field);
        }

        public Category getCategory() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        protected String qualifyPropertyName(String propertyName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        protected InputField qualifySubfield(InputField subfield) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

    }
}
