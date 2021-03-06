/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.security.passwordpolicy.validators;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.validation.ValidationErrors;

public class PasswordCreationPolicyException extends CaaersSystemException {
	private static final long serialVersionUID = 6787210172663632951L;
	
	private ValidationErrors errors;
	public PasswordCreationPolicyException(String message, ValidationErrors errors) {
		super(message);
		this.errors = errors;
	}
	
	public ValidationErrors getErrors() {
		return errors;
	}
	public void setErrors(ValidationErrors errors) {
		this.errors = errors;
	}
}
