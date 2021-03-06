/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

 
/**
 * The Class OtherIntervention.
 *
 * @author: Ion C. Olaru
 */
@Entity
@Table(name = "other_interventions")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "seq_other_interventions_id") })
public class OtherIntervention extends StudyIntervention {

    /** The name. */
    private String name;
    
    /** The description. */
    private String description;

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.StudyIntervention#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (this == o) return true;
        if(!(o instanceof OtherIntervention)) return false;

        OtherIntervention that = (OtherIntervention) o;
        if (!this.getStudyTherapyType().equals(((StudyIntervention) o).getStudyTherapyType())) return false;
        if (!StringUtils.equals(getName(), that.getName())) return false;
        if (!StringUtils.equals(getDescription(), that.getDescription())) return false;

        return super.equals(that);
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cabig.caaers.domain.StudyIntervention#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (getStudyTherapyType() != null ? getStudyTherapyType().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return super.hashCode();
    }
    
    @Transient
    @Override
    public  String getInterventionName() {
    	return getName();
    }
    
    @Override
    @Transient
    public String getHashKey() {
    	return super.getHashKey()+(StringUtils.isBlank(description)?"":description);
    }
}
