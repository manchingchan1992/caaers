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

 
/**
 * This class represents the StudyCoordinatingCenter domain object associated with the Adverse event
 * report.
 * 
 * @author Ram Chilukuri
 * 
 */
@Entity
@DiscriminatorValue(value = "SCC")
public class StudyCoordinatingCenter extends StudyOrganization {
    
    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.StudyOrganization#getRoleName()
     */
    @Override
    @Transient
    public String getRoleName() {
        return "CoordinatingCenter";
    }
}
