/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(value="LOCAL")
public class LocalStudy extends Study{

	private static final long serialVersionUID = 1L;

    public String getShortTitle() {
        return shortTitle;
    }
	
    public String getLongTitle() {
        return longTitle;
    }
	
    public String getPhaseCode() {
        return phaseCode;
    }
	
    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }
	
	@Transient
    public String getExternalId() {
		return externalId;
	}
}
