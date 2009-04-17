package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.dao.query.InvestigatorQuery;
import gov.nih.nci.cabig.caaers.domain.Investigator;
import gov.nih.nci.cabig.caaers.domain.LocalInvestigator;
import gov.nih.nci.cabig.ctms.dao.MutableDomainObjectDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.semanticbits.coppa.infrastructure.RemoteSession;

/**
 * This class implements the Data access related operations for the Investigator domain object.
 * 
 * @author Kulasekaran
 * 
 */
@Transactional(readOnly = true)
public class InvestigatorDao extends GridIdentifiableDao<Investigator> implements
                MutableDomainObjectDao<Investigator> {
    private static final List<String> SUBSTRING_MATCH_PROPERTIES = Arrays.asList("firstName",
                    "lastName");

    private static final List<String> EXACT_MATCH_PROPERTIES = Collections.emptyList();
    
    private RemoteSession remoteSession;
    
    private OrganizationDao organizationDao;

    public void setOrganizationDao(OrganizationDao organizationDao) {
		this.organizationDao = organizationDao;
	}

	public void setRemoteSession(RemoteSession remoteSession) {
		this.remoteSession = remoteSession;
	}

	/**
     * Get the Class representation of the domain object that this DAO is representing.
     * 
     * @return Class representation of the domain object that this DAO is representing.
     */
    @Override
    public Class<Investigator> domainClass() {
        return Investigator.class;
    }

    /**
     * Save the investigator.
     * 
     * @param investigator
     *                The investigator to be saved.
     */
    @Transactional(readOnly = false)
    public void save(final Investigator investigator) {
    	if(investigator.getId() == null && investigator instanceof LocalInvestigator){
    		List<Investigator> remoteInvestigators = getRemoteInvestigators(investigator);
    		if(remoteInvestigators != null && remoteInvestigators.size() > 0){
    			logger.error("ResearchStaff exists in external system");
    			throw new RuntimeException("ResearchStaff exists in external system");
    		}
    	}
    	getHibernateTemplate().saveOrUpdate(investigator);
    }

    /**
     * Get the list of investigators matching the name fragments.
     * 
     * @param subnames
     *                the name fragments to search on.
     * @return List of matching investigators.
     */
    @Transactional(readOnly = false)
    public List<Investigator> getBySubnames(final String[] subnames) {
    	List<Investigator> investigators = findBySubname(subnames, SUBSTRING_MATCH_PROPERTIES, EXACT_MATCH_PROPERTIES);
        return investigators;
    	//Investigator searchCriteria = new RemoteInvestigator();
    	//List<Investigator> remoteInvestigators = (List)remoteSession.find(searchCriteria); 
    	//return merge(investigators,remoteInvestigators);
    }

    /**
     * Gets the Investigator by id. This initialize the Investigator and load all the objects.
     * 
     * @param id
     *                the id
     * 
     * @return the Investigator by id
     */
    public Investigator getInvestigatorById(final int id) {
        Investigator investigator = (Investigator) getHibernateTemplate().get(domainClass(), id);
        initialize(investigator);

        return investigator;
    }

    /**
     * TODO
     * 
     * @param investigator
     * @return
     */
    public Investigator initialize(final Investigator investigator) {
        HibernateTemplate ht = getHibernateTemplate();
        ht.initialize(investigator.getSiteInvestigatorsInternal());

        return investigator;
    }
    
    @SuppressWarnings("unchecked")
	public List<Investigator> getLocalInvestigator(final InvestigatorQuery query){
    	String queryString = query.getQueryString();
        log.debug("::: " + queryString.toString());
        List<Investigator> investigators = (List<Investigator>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(final Session session) throws HibernateException,
                            SQLException {
                org.hibernate.Query hibernateQuery = session.createQuery(query.getQueryString());
                Map<String, Object> queryParameterMap = query.getParameterMap();
                for (String key : queryParameterMap.keySet()) {
                    Object value = queryParameterMap.get(key);
                    hibernateQuery.setParameter(key, value);

                }
                return hibernateQuery.list();
            }

        });
        return investigators;
    }

    public List<Investigator> getRemoteInvestigators(final Investigator investigator) {
    	
    	//Commenting the stuff as SRINI is integrating with actual coppa service.
    	//No point in redoing this stuff now.
    	
//    	Investigator searchCriteria = new RemoteInvestigator();
    	List<Investigator> remoteInvestigators = new ArrayList<Investigator>(); 
//        List<Investigator> remoteInvestigators = (List)remoteSession.find(searchCriteria); 
//        for (Investigator remoteInvestigator:remoteInvestigators) {  
//        	Investigator investigator = getByEmailAddress(remoteInvestigator.getEmailAddress());
//    			if (investigator == null ) {  
//    				List<SiteInvestigator> siteInvestigators = remoteInvestigator.getSiteInvestigators();
//        			List<SiteInvestigator> newSiteInvestigators = new ArrayList<SiteInvestigator>();
//        			for (SiteInvestigator siteInvestigator:siteInvestigators) {
//        				//if associated organization is not there in our DB
//        				Organization remoteOrganization = siteInvestigator.getOrganization();
//        				Organization organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
//            			if (organization == null) {
//            				// TODO : need to get the remote organozation from coppa and save it ..
//            				organizationDao.save(remoteOrganization);
//            				organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
//            			} 
//
//            			SiteInvestigator newSi = new SiteInvestigator();
//            			newSi.setOrganization(organization);
//            			//newSi.setInvestigator(investigator);
//            			newSiteInvestigators.add(newSi);
//        			}
//        			remoteInvestigator.getSiteInvestigators().clear();
//        			save(remoteInvestigator);
//        			investigator = getByEmailAddress(remoteInvestigator.getEmailAddress());  
//        			remoteInvestigators1.add(investigator);
//    			} else {
//    				remoteInvestigators1.add(investigator);
//    			}
//        }
        return remoteInvestigators;
    }
    /**
     * Get the user who has specified email address.
     * 
     * @param loginId
     *                The loginId of the user.
     * @return The user.
     */
    @SuppressWarnings("unchecked")
	public Investigator getByLoginId(String loginId) {
        List<Investigator> results = getHibernateTemplate().find(
                        "from Investigator where loginId= ?", loginId);
        Investigator investigator =  results.size() > 0 ? results.get(0) : null;
        if(investigator != null) initialize(investigator);
        return investigator;
    }
    
    @SuppressWarnings("unchecked")
	public Investigator getByEmailAddress(String email) {
        List<Investigator> results = getHibernateTemplate().find(
                        "from Investigator where emailAddress= ?", email);
        Investigator investigator =  results.size() > 0 ? results.get(0) : null;
        if(investigator != null) initialize(investigator);
        return investigator;
    }
    

}