/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.BeanUtils;

/**
 * This class represents the SurgeryIntervention domain object associated with the Adverse event
 * report.
 *
 * @author Krikor Krumlian
 */
@Entity
@Table(name = "ae_surgery_interventions")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "seq_ae_surgery_intervention_id")})
public class SurgeryIntervention extends AbstractExpeditedReportCollectionElementChild {

    private String treatmentArm;
    private String description;
    private InterventionSite interventionSite;
    private Date interventionDate;

    private OtherIntervention studySurgery;

    // //// LOGIC

    // //// BEAN PROPERTIES

    @Transient
    public String getTreatmentArm() {
        return treatmentArm;
    }

    public void setTreatmentArm(String treatmentArm) {
        this.treatmentArm = treatmentArm;
    }

    @Transient
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getInterventionDate() {
        return interventionDate;
    }

    public void setInterventionDate(Date interventionDate) {
        this.interventionDate = interventionDate;
    }

    @ManyToOne
    public InterventionSite getInterventionSite() {
        return interventionSite;
    }

    public void setInterventionSite(InterventionSite interventionSite) {
        this.interventionSite = interventionSite;
    }

    @ManyToOne
    @JoinColumn(name = "study_intervention_id")
    public OtherIntervention getStudySurgery() {
        return studySurgery;
    }

    public void setStudySurgery(OtherIntervention studySurgery) {
        this.studySurgery = studySurgery;
    }

}
