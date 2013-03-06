/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;

import javax.persistence.*;

/**
 * @author Ion C. Olaru
 * 
 */
@Entity
@DiscriminatorValue("meddra")
public class AgentSpecificMeddraLowLevelTerm extends AgentSpecificTerm<LowLevelTerm> {

    @Transient
    public String getFullName() {
    	if(getTerm() == null) return "";
    	return getTerm().getFullName();
    }

    @ManyToOne
    @JoinColumn(name = "term_id")
    @Override
    public LowLevelTerm getTerm() {
        return super.getTerm();
    }

    @Transient
    public void setLowLevelTerm(LowLevelTerm lowlevelTerm) {
        super.setTerm(lowlevelTerm);
    }

    @Override
    public AgentSpecificMeddraLowLevelTerm copy() {
        return (AgentSpecificMeddraLowLevelTerm) super.copy();
    }

    @Override
    @Transient
    public boolean isMedDRA() {
    	return true;
    }

    @Override
    @Transient
    public boolean isOtherRequired() {
        return false;
    }

}
