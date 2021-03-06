/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao.meddra;

import gov.nih.nci.cabig.caaers.dao.CaaersDao;
import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the Data access related operations for the LowLevelTerm domain object.
 * 
 * @author Krikor Krumlian
 */
@Transactional(readOnly=true)
public class LowLevelTermDao extends CaaersDao<LowLevelTerm> {
    /**
     * Get the Class representation of the domain object that this DAO is representing.
     * 
     * @return Class representation of the domain object that this DAO is representing.
     */
	 @Transactional(readOnly = true, propagation= Propagation.NOT_SUPPORTED)
    public Class<LowLevelTerm> domainClass() {
        return LowLevelTerm.class;
    }

    private static final List<String> SUBSTRING_MATCH_PROPERTIES = Arrays.asList("meddraCode",
                    "meddraTerm");
    
    private static final List<String> EXACT_MATCH_PROPERTIES = Collections.emptyList();
    
    private static final List<Object> EXTRA_PARAMS = Collections.emptyList();

    /**
     * Get the list of all low level meddra terms.
     * 
     * @return return the list of low level meddra terms.
     */
    @SuppressWarnings("unchecked")
    public List<LowLevelTerm> getAll() {
        return getHibernateTemplate().find("from LowLevelTerm");
    }

    /**
     * Get low level meddra terms given the meddra code.
     * 
     * @param meddraCode
     *                The meddra code.
     * @return The low level meddra term.
     */
    @SuppressWarnings("unchecked")
    public List<LowLevelTerm> getByMeddraCode(String meddraCode) {
        return getHibernateTemplate().find("from LowLevelTerm llt where meddraCode=?",
                        new Object[] { meddraCode });
    }

    /**
     * Get  of low level meddra terms matching the term and code
     * 
     * @param meddraCode
     *                The meddra code.
     * @param meddraTerm
     * 					The meddra term
     * @return The low level meddra term.
     */
    public LowLevelTerm getByCodeAndTerm(String meddraCode, String meddraTerm) {
        List<LowLevelTerm> lowLevelterms = getHibernateTemplate().find("from LowLevelTerm llt where meddraCode=? and meddraTerm=? ",
                new Object[] { meddraCode , meddraTerm});
        return lowLevelterms.isEmpty() ? null : lowLevelterms.get(0);
    } 
    
    /**
     * Get low level meddra terms given the meddra code & version.
     * 
     * @param meddraCode
     *                The meddra code.
     * @return The low level meddra term.
     */
    @SuppressWarnings("unchecked")
    public List<LowLevelTerm> getByMeddraCodeandVersion(String meddraCode, int versionId) {
        return getHibernateTemplate().find("from LowLevelTerm llt where meddraCode=? and version_id=?",
                        new Object[] { meddraCode, versionId });
    }

    /**
     * Get the list of low level meddra terms matching the name fragments.
     * 
     * @param subnames
     *                the name fragments to search on.
     * @return List of matching low level meddra terms.
     */
    public List<LowLevelTerm> getBySubnames(String[] subnames) {
        return findBySubname(subnames, SUBSTRING_MATCH_PROPERTIES, EXACT_MATCH_PROPERTIES);
    }
    
   

    /**
     * Get the list of low level meddra terms of correct version matching the name fragments.
     * 
     * @params version_id
     * @params subnames
     * 				 the name fragments to search on.
     * @return List of matchin low level meddra terms.
     * @author Sameer Sawant
     */	
    public List<LowLevelTerm> getByVersionSubnames(int version_id, String[] subnames) {
    	return findBySubname(subnames, "o.meddraVersion.id = " + version_id, EXTRA_PARAMS,
    			SUBSTRING_MATCH_PROPERTIES, EXACT_MATCH_PROPERTIES);
    }
    
}
