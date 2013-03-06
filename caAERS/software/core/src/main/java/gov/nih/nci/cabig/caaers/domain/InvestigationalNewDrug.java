/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents the InvestigationalNewDrug domain object associated with the Adverse event
 * report.
 * 
 * @author
 * 
 */
@Entity
@Table(name = "investigational_new_drugs")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "seq_investigational_new_dru_id") })
public class InvestigationalNewDrug extends AbstractMutableDomainObject {

    static private Log log = LogFactory.getLog(InvestigationalNewDrug.class);

	public static int CTEP_IND = -111;
	public static int DCP_IND = -222;
    public static final String STRING_CTEP_IND = "CTEP IND";
    public static final String STRING_DCP_IND = "DCP IND";

    private Integer indNumber;
    private INDHolder iNDHolder;

    private List<StudyAgentINDAssociation> studyAgentINDAssociations;

    private transient String holderName;

    public Integer getIndNumber() {
        return indNumber;
    }

    public void setIndNumber(Integer number) {
        indNumber = number;
    }

    @OneToOne(mappedBy = "investigationalNewDrug", fetch = FetchType.EAGER)
    @Cascade( { CascadeType.ALL })
    public INDHolder getINDHolder() {
        return iNDHolder;
    }

    public void setINDHolder(INDHolder holder) {
        iNDHolder = holder;
        holderName = holder.getName();
    }

    @OneToMany(mappedBy = "investigationalNewDrug")
    @Cascade( { CascadeType.DELETE, CascadeType.DELETE_ORPHAN })
    public List<StudyAgentINDAssociation> getStudyAgentINDAssociations() {
        return studyAgentINDAssociations;
    }

    public void setStudyAgentINDAssociations(List<StudyAgentINDAssociation> studyAgentINDAssociations) {
        this.studyAgentINDAssociations = studyAgentINDAssociations;
    }

    @Transient
    public String getHolderName() {
        if (holderName != null) return holderName;
        return (iNDHolder != null) ? iNDHolder.getName() : "";
    }

    @Transient
    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    @Transient
    public String getStrINDNo() {
    	if(indNumber == null) return "";
    	if(indNumber == CTEP_IND) return STRING_CTEP_IND;
    	if(indNumber == DCP_IND) return STRING_DCP_IND;
    	
        return String.valueOf(indNumber);
    }

    public void setStrINDNo(String strINDNo) {
        if (strINDNo.equals(STRING_CTEP_IND)) indNumber = CTEP_IND;
        else if (strINDNo.equals(STRING_DCP_IND)) indNumber = DCP_IND;
        else {
            int n = 0;
            try {
                n = Integer.parseInt(strINDNo);
            } catch (NumberFormatException e) {
                log.error("Unrecognized String came as an Agent #: " + strINDNo);
            }
            indNumber = new Integer(n);
        };
    }
    
    @Transient
    public String getNumberAndHolderName(){
    	return getStrINDNo() + " : " + getHolderName();
    }

    @Override
    public String toString() {
        return "InvestigationalNewDrug[" + indNumber + " : " + getHolderName() + "]";
    }
}
