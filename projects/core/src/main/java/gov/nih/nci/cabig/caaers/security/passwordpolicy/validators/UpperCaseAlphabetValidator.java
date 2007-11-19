package gov.nih.nci.cabig.caaers.security.passwordpolicy.validators;

import gov.nih.nci.cabig.caaers.security.Credential;
import gov.nih.nci.cabig.caaers.security.passwordpolicy.PasswordPolicy;

public class UpperCaseAlphabetValidator implements PolicyValidator{
	
	

	public boolean validate(PasswordPolicy policy, Credential credential) throws ValidationException {
		String password = credential.getPassword();
		char[] chars = password.toCharArray();
		
		boolean contains = false;
		for(int i=0;i<chars.length;i++){
			if(Character.isUpperCase(chars[i])){
				contains = true;
				break;
			}
		}
		if(!contains){
			throw new ValidationException("The password should have atleast one upper case letter");
		}
		
		return true;
	}

}
