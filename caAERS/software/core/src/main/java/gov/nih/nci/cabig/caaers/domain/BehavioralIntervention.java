package gov.nih.nci.cabig.caaers.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

/**
 * @author Ion C. Olaru
 */
@Entity
@Table(name = "ae_behavioral_interventions")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "seq_ae_behavioral_interv_id")})
public class BehavioralIntervention extends AbstractAEIntervention {
}