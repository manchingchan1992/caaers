package gov.nih.nci.cabig.caaers.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * This class represents the StudyParticipantMetastaticDiseaseSite domain object associated with the StudyParticipantAssignment
 *
 * @author Sameer Sawant
 */
@Entity
@Table(name = "spa_metastatic_disease_sites")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "seq_spa_meta_dis_sites_id") })
// TODO: this sort of class might be better as a component rather than another domain object
public class StudyParticipantMetastaticDiseaseSite extends AbstractIdentifiableDomainObject {
    private String otherSite;

    private AnatomicSite codedSite;

    public String getOtherSite() {
        return otherSite;
    }

    public void setOtherSite(String otherSite) {
        this.otherSite = otherSite;
    }

    @OneToOne
    @JoinColumn(name = "coded_site_id")
    public AnatomicSite getCodedSite() {
        return codedSite;
    }

    public void setCodedSite(AnatomicSite codedSite) {
        this.codedSite = codedSite;
    }

    ///OBJECT METHODS
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final StudyParticipantMetastaticDiseaseSite other = (StudyParticipantMetastaticDiseaseSite) obj;
		if (codedSite == null) {
			if (other.codedSite != null)
				return false;
		} else if (!codedSite.equals(other.codedSite))
			return false;
		if (otherSite == null) {
			if (other.otherSite != null)
				return false;
		} else if (!otherSite.equals(other.otherSite))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codedSite == null) ? 0 : codedSite.hashCode());
		result = prime * result
				+ ((otherSite == null) ? 0 : otherSite.hashCode());
		return result;
	}
}
