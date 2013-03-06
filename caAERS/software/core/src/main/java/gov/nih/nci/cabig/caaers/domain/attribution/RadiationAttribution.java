/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.attribution;

import gov.nih.nci.cabig.caaers.domain.RadiationIntervention;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Rhett Sutphin
 */
@Entity
@DiscriminatorValue("RI")
public class RadiationAttribution extends AdverseEventAttribution<RadiationIntervention> {
    @ManyToOne
    @JoinColumn(name = "cause_id")
    @Override
    public RadiationIntervention getCause() {
        return super.getCause();
    }

    @Override
    public RadiationAttribution copy() {
        return super.copy();
    }
}
