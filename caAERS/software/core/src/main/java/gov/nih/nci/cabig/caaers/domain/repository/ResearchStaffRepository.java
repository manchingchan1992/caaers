/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.repository;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.dao.OrganizationDao;
import gov.nih.nci.cabig.caaers.dao.ResearchStaffConverterDao;
import gov.nih.nci.cabig.caaers.dao.ResearchStaffDao;
import gov.nih.nci.cabig.caaers.dao.SiteResearchStaffDao;
import gov.nih.nci.cabig.caaers.dao.query.ResearchStaffQuery;
import gov.nih.nci.cabig.caaers.dao.query.SiteResearchStaffQuery;
import gov.nih.nci.cabig.caaers.domain.ConverterResearchStaff;
import gov.nih.nci.cabig.caaers.domain.LocalResearchStaff;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.RemoteOrganization;
import gov.nih.nci.cabig.caaers.domain.RemoteResearchStaff;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.domain.SiteResearchStaff;
import gov.nih.nci.cabig.caaers.event.EventFactory;
import gov.nih.nci.cabig.caaers.security.CaaersSecurityFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.mail.MailException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

 
/**
 * This is the repository class for managing the research staff domain object.
 *
 * @author Biju Joseph
 * @author Jared Flatow
 */
@Transactional(readOnly = true)
public class ResearchStaffRepository {

    /** The caaers security facade. */
    private CaaersSecurityFacade caaersSecurityFacade;
    
    /** The research staff dao. */
    private ResearchStaffDao researchStaffDao;
    
    /** The site research staff dao. */
    private SiteResearchStaffDao siteResearchStaffDao;
    
    /** The research staff converter dao. */
    private ResearchStaffConverterDao researchStaffConverterDao;
    
    /** The organization repository. */
    private OrganizationRepository organizationRepository;
    
    /** The organization dao. */
    private OrganizationDao organizationDao;
    
    /** The authentication mode. */
    private String authenticationMode;
    
    /** The Constant logger. */
    private static final Log logger = LogFactory.getLog(ResearchStaffRepository.class);
    
    /** The study repository. */
    private StudyRepository studyRepository;
    
    /** The coppa mode for auto completers. */
    private boolean coppaModeForAutoCompleters;
    
    /** The event factory. */
    private EventFactory eventFactory;
    
	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	public List<ResearchStaff> getAll() {
        ResearchStaffQuery researchStaffQuery = new ResearchStaffQuery();
        return getResearchStaff(researchStaffQuery);
    }
    
    /**
     * Saves or update the research staff.
     *
     * @param researchStaff the research staff
     * @param changeURL the change url
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, noRollbackFor = MailException.class)
    public void save(ResearchStaff researchStaff, String changeURL) {

    	try{
    		researchStaff = (ResearchStaff)researchStaffDao.merge(researchStaff);
    	}catch(Exception e){
    		logger.error("error while saving research staff", e);
			throw new CaaersSystemException("Failed to save researchstaff", e);
		}

		try{
			studyRepository.associateStudyPersonnel(researchStaff);
		}catch(Exception e){
			throw new CaaersSystemException("Failed to associte researchstaff to all studies", e);
		}
    	
    }
    
    
    /**
     * Evict.
     *
     * @param researchStaff the research staff
     */
    public void evict(ResearchStaff researchStaff){
    	researchStaffDao.evict(researchStaff);
    }
    
    /**
     * Convert to remote.
     *
     * @param localResearchStaff the local research staff
     * @param remoteResearchStaff the remote research staff
     */
    @Transactional(readOnly = false)
    public void convertToRemote(ResearchStaff localResearchStaff, ResearchStaff remoteResearchStaff){
    	ConverterResearchStaff conRStaff = researchStaffConverterDao.getById(localResearchStaff.getId());
    	conRStaff.setType("REMOTE");
    	conRStaff.setExternalId(remoteResearchStaff.getExternalId());
    	conRStaff.setFirstName(remoteResearchStaff.getFirstName());
    	conRStaff.setLastName(remoteResearchStaff.getLastName());
    	conRStaff.setMiddleName(remoteResearchStaff.getMiddleName());
    	conRStaff.setPhoneNumber(remoteResearchStaff.getPhoneNumber());
    	conRStaff.setFaxNumber(remoteResearchStaff.getFaxNumber());
    	//Commented below 2 lines for ResearchStaff changes. Organization info is captured in SiteResearchStaff 
    	//ConverterOrganization conOrg = organizationConverterDao.getById(localResearchStaff.getOrganization().getId());
    	//conRStaff.setConverterOrganization(conOrg);
    	researchStaffConverterDao.save(conRStaff);
    }

    /**
     * Gets the by id.
     *
     * @param id the id
     * @return the by id
     */
    public ResearchStaff getById(final int id) {
        ResearchStaff researchStaff = researchStaffDao.getById(id);
        initialize(researchStaff);
        return researchStaff;
    }
    
    /**
     * Search research staff.
     *
     * @param query the query
     * @return the list
     */
    public List<ResearchStaff> searchResearchStaff(final ResearchStaffQuery query) {
        return researchStaffDao.searchResearchStaff(query);
    }

    /**
     * This method will populate the CSM roles associated to this research staff.
     *
     * @param researchStaff the research staff
     * @return the research staff
     */
    public ResearchStaff initialize(final ResearchStaff researchStaff) {
        //researchStaff.setUserGroupTypes(((CaaersSecurityFacadeImpl)caaersSecurityFacade).getCsmUserRepository().getUserGroups(researchStaff.getLoginId()));
        return researchStaff;
    }
    
    /**
     * Gets the site research staff by subnames.
     *
     * @param subnames the subnames
     * @param site the site
     * @return the site research staff by subnames
     */
    @Transactional(readOnly = false)
    public List<SiteResearchStaff> getSiteResearchStaffBySubnames(final String[] subnames, final int site) {
    	
    	List<ResearchStaff> remoteResearchStaffs = new ArrayList<ResearchStaff>();
    	if (coppaModeForAutoCompleters) {
	    	RemoteResearchStaff searchCriteria = new RemoteResearchStaff(); 
	    	Organization org = organizationDao.getById(site);
	    	SiteResearchStaff sr = new SiteResearchStaff();
	    	sr.setOrganization(org);
	    	searchCriteria.addSiteResearchStaff(sr);
	    	remoteResearchStaffs = researchStaffDao.getRemoteResearchStaff(searchCriteria);
    	} else {
    		return siteResearchStaffDao.getBySubnames(subnames, site);
    	}
        if(remoteResearchStaffs == null){
        	return siteResearchStaffDao.getBySubnames(subnames, site);
        }else{
        	saveRemoteResearchStaff(remoteResearchStaffs);
        }
    	return siteResearchStaffDao.getBySubnames(subnames, site);
    }
    
    /**
     * Gets the by subnames.
     *
     * @param subnames the subnames
     * @param site the site
     * @return the by subnames
     */
    @Transactional(readOnly = false)
    public List<ResearchStaff> getBySubnames(final String[] subnames, final int site) {
    	List<ResearchStaff> researchStaffs = researchStaffDao.getBySubnames(subnames, site);
    	ResearchStaff searchCriteria = new RemoteResearchStaff();
    	Organization org = organizationDao.getById(site);
    	//Commented below line for ResearchStaff changes. Organization is captured in SiteResearchStaff
    	//searchCriteria.setOrganization(org);
    	List<ResearchStaff> remoteResearchStaffs = getRemoteResearchStaff(searchCriteria);

    	return merge (researchStaffs,remoteResearchStaffs);
    }

    /**
     * Gets the research staff.
     *
     * @param query the query
     * @return the research staff
     */
    @Transactional(readOnly = false)
    public List<ResearchStaff> getResearchStaff(final ResearchStaffQuery query){
    	//Get all the RS from caAERS DB
        List<ResearchStaff> researchStaffs = researchStaffDao.getLocalResearchStaff(query);
        //Get all the RS from External System
        ResearchStaff searchCriteria = new RemoteResearchStaff();
        Map<String, Object> queryParameterMap = query.getParameterMap();
        for (String key : queryParameterMap.keySet()) {
            Object value = queryParameterMap.get(key);
            if (key.equals("firstName")) {
				searchCriteria.setFirstName(value.toString());
			}
            if (key.equals("lastName")) {
				searchCriteria.setLastName(value.toString());
			}
			if (key.equals("organizationNciInstituteCode")) {
				Organization organization = new RemoteOrganization();
				organization.setNciInstituteCode(value.toString());
				//Commented below line for ResearchStaff changes. Organization is captured in SiteResearchStaff
				//searchCriteria.setOrganization(organization);
			}	
        }
    	List<ResearchStaff> remoteResearchStaffs = researchStaffDao.getRemoteResearchStaff(searchCriteria);
    	//Merge and Return
    	return merge (researchStaffs,remoteResearchStaffs);
    }
    
    /**
     * Gets the site research staff.
     *
     * @param query the query
     * @return the site research staff
     */
    @Transactional(readOnly = false)
    public List<SiteResearchStaff> getSiteResearchStaff(final SiteResearchStaffQuery query){
    	//Get all the RS from caAERS DB
        List<SiteResearchStaff> siteResearchStaffs = researchStaffDao.getSiteResearchStaff(query);
        return siteResearchStaffs;
    }
    
    
    /**
     * Gets the site research staff.
     *
     * @param query the query
     * @param searchCriteriaMap the search criteria map
     * @return the site research staff
     */
    @SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
    public List<SiteResearchStaff> getSiteResearchStaff(SiteResearchStaffQuery query,HashMap searchCriteriaMap){
    	
        String firstName = (String)searchCriteriaMap.get("firstName");
        String lastName = (String)searchCriteriaMap.get("lastName");
        String organization = (String)searchCriteriaMap.get("organization");
        String nciIdentifier = (String)searchCriteriaMap.get("personIdentifier");
    	
        if(StringUtils.isEmpty(firstName) 
        		&& StringUtils.isEmpty(lastName) 
        		&& StringUtils.isEmpty(organization) 
        		&& StringUtils.isEmpty(nciIdentifier)){
        	
        	return researchStaffDao.getSiteResearchStaff(query);
        }
    	if(StringUtils.isNotEmpty(firstName) && firstName.indexOf("%") != -1){
    		return researchStaffDao.getSiteResearchStaff(query);
    	}

    	if(StringUtils.isNotEmpty(lastName) && lastName.indexOf("%") != -1){
    		return researchStaffDao.getSiteResearchStaff(query);
    	}
    	if(StringUtils.isNotEmpty(nciIdentifier) && nciIdentifier.indexOf("%") != -1){
    		return researchStaffDao.getSiteResearchStaff(query);
    	}
    	
        RemoteResearchStaff searchCriteria = new RemoteResearchStaff(); 
        searchCriteria.setFirstName(firstName);
        searchCriteria.setLastName(lastName);
        searchCriteria.setNciIdentifier(nciIdentifier);
        if(StringUtils.isNotEmpty(organization)){
        	Organization org = organizationDao.getById(Integer.parseInt(organization));
        	SiteResearchStaff sr = new SiteResearchStaff();
        	sr.setOrganization(org);
        	searchCriteria.addSiteResearchStaff(sr);
        }
        
        List<ResearchStaff> remoteResearchStaffs = null;
        try{
        	remoteResearchStaffs = researchStaffDao.getRemoteResearchStaff(searchCriteria);
        }catch(Exception e){
        	logger.warn("Error searching ResearchStaff from PO -- " + e.getMessage());
        }

        //save remote research staff and refresh index.
        if(CollectionUtils.isNotEmpty(remoteResearchStaffs)){
            saveRemoteResearchStaff(remoteResearchStaffs);
            logger.info(remoteResearchStaffs.size() + " :::: ResearchStaff fetched from PO");
        }
        List<SiteResearchStaff> sRsList = researchStaffDao.getSiteResearchStaff(query);
        if(sRsList != null){
        	logger.info(sRsList.size() +" :::: ResearchStaff is being displayed");
        }
        return sRsList;
    }
    
    /**
     * Save remote research staff.
     *
     * @param remoteList the remote list
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, noRollbackFor = MailException.class)
    private void saveRemoteResearchStaff(List<ResearchStaff> remoteList){
		for (ResearchStaff remoteResearchStaff:remoteList) {
			ResearchStaff rs = researchStaffDao.getByExternalId(remoteResearchStaff.getExternalId());
    		if (rs == null ) {
    			try {
    				List<SiteResearchStaff> srList = remoteResearchStaff.getSiteResearchStaffs();
    				List<SiteResearchStaff> srDBList = new ArrayList<SiteResearchStaff>();
    				for (SiteResearchStaff sr:srList) {
    					Organization remoteOrganization = sr.getOrganization();
    					Organization organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
    	    			if (organization == null) {
    	    				organizationRepository.create(remoteOrganization);
    	    				organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
    	    			} 
    	    			SiteResearchStaff dbSR = new SiteResearchStaff();
    	    			dbSR.setOrganization(organization);
    	    			dbSR.setResearchStaff(remoteResearchStaff);
    	    			dbSR.setEmailAddress(remoteResearchStaff.getEmailAddress());
    	    			dbSR.setPhoneNumber(remoteResearchStaff.getPhoneNumber());
    	    			dbSR.setFaxNumber(remoteResearchStaff.getFaxNumber());
    	    			srDBList.add(dbSR);
    				}
    				remoteResearchStaff.getSiteResearchStaffs().clear();
    				remoteResearchStaff.setSiteResearchStaffs(srDBList);
    				save(remoteResearchStaff,"URL");
    				remoteResearchStaff = researchStaffDao.getByExternalId(remoteResearchStaff.getExternalId());
    			} catch (MailException e) {
    				logger.error("Mail send exception --" + e.getMessage());
    			}
        	} else {
        		// if it exist in local list , remote interceptor would have loaded the rest of the details .but need to add orgs manually 
        		List<SiteResearchStaff> srList = remoteResearchStaff.getSiteResearchStaffs();
				try {
					for (SiteResearchStaff sr:srList) {
						Organization remoteOrganization = sr.getOrganization();
						Organization organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
		    			if (organization == null) {
		    				organizationRepository.create(remoteOrganization);
		    				organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
		    			} 
		    			SiteResearchStaff siteResearchStaff = new SiteResearchStaff();
		    			siteResearchStaff.setOrganization(organization);
		    			siteResearchStaff.setResearchStaff(remoteResearchStaff);
		    			siteResearchStaff.setEmailAddress(remoteResearchStaff.getEmailAddress());
		    			siteResearchStaff.setPhoneNumber(remoteResearchStaff.getPhoneNumber());
		    			siteResearchStaff.setFaxNumber(remoteResearchStaff.getFaxNumber());
		    			List<SiteResearchStaff> siDBList = rs.getSiteResearchStaffs();
		    			boolean exists = false;
		    			for (SiteResearchStaff srsd:siDBList){
		    				if (srsd.getOrganization().getNciInstituteCode().equals(organization.getNciInstituteCode())) {
		    					exists = true;
		    					break;
		    				}
		    			}
		    			if (!exists) {
		    				rs.addSiteResearchStaff(siteResearchStaff);
		    			}
					}
					save(rs,"URL");
	        	} catch (MailException e) {
	        		logger.error("Mail send exception --" + e.getMessage());
				}
        	}
    	}

        //publish event
        eventFactory.publishEntityModifiedEvent(new LocalResearchStaff(), false);
    }
    
    /**
     * Merge.
     *
     * @param localList the local list
     * @param remoteList the remote list
     * @return the list
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, noRollbackFor = MailException.class)
    private List<ResearchStaff> merge(List<ResearchStaff> localList , List<ResearchStaff> remoteList) {
		for (ResearchStaff remoteResearchStaff:remoteList) {
			ResearchStaff rs = researchStaffDao.getByEmailAddress(remoteResearchStaff.getEmailAddress());
    		if (rs == null ) {
    			try {
    				List<SiteResearchStaff> srList = remoteResearchStaff.getSiteResearchStaffs();
    				List<SiteResearchStaff> srDBList = new ArrayList<SiteResearchStaff>();
    				for (SiteResearchStaff sr:srList) {
    					Organization remoteOrganization = sr.getOrganization();
    					Organization organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
    	    			if (organization == null) {
    	    				organizationRepository.create(remoteOrganization);
    	    				organization = organizationDao.getByNCIcode(remoteOrganization.getNciInstituteCode());
    	    			} 
    	    			SiteResearchStaff dbSR = new SiteResearchStaff();
    	    			dbSR.setOrganization(organization);
    	    			dbSR.setResearchStaff(remoteResearchStaff);
    	    			srDBList.add(dbSR);
    				}
    				remoteResearchStaff.getSiteResearchStaffs().clear();
    				remoteResearchStaff.setSiteResearchStaffs(srDBList);
    				save(remoteResearchStaff,"URL");
    			} catch (MailException e) {
    				logger.error("Mail send exception --" + e.getMessage());
    			}

        		//rs = researchStaffDao.getByEmailAddress(remoteResearchStaff.getEmailAddress());
        		//rs.setFirstName(remoteResearchStaff.getFirstName());
        		//rs.setLastName(remoteResearchStaff.getLastName());
        		//rs.setMiddleName(remoteResearchStaff.getMiddleName());
        		localList.add(remoteResearchStaff);
        	} else {
        		// if it exist in local list , remote interceptor would have loaded the rest of the details .
        		if (!localList.contains(rs)) {
        			localList.add(rs);
        		}
        	}
    	}
		return localList;
	}
    
    /**
     * Gets the by nci identifier.
     *
     * @param subnames the subnames
     * @param site the site
     * @return the by nci identifier
     */
    @Transactional(readOnly = false)
    public List<ResearchStaff> getByNciIdentifier(final String[] subnames, final int site){
    	List<ResearchStaff> researchStaffs = researchStaffDao.getByNciIdentifier(subnames,site);
    	ResearchStaff searchCriteria = new RemoteResearchStaff();
    	List<ResearchStaff> remoteResearchStaffs = getRemoteResearchStaff(searchCriteria); 
    	
    	return merge (researchStaffs,remoteResearchStaffs);
    }

    /**
     * Gets the remote research staff.
     *
     * @param researchStaff the research staff
     * @return the remote research staff
     */
    public List<ResearchStaff> getRemoteResearchStaff(final ResearchStaff researchStaff){
    	return researchStaffDao.getRemoteResearchStaff(researchStaff);
    }

    /**
     * Sets the research staff dao.
     *
     * @param researchStaffDao the new research staff dao
     */
    @Required
    public void setResearchStaffDao(final ResearchStaffDao researchStaffDao) {
        this.researchStaffDao = researchStaffDao;
    }

    /**
     * Gets the authentication mode.
     *
     * @return the authentication mode
     */
    @Required
    public String getAuthenticationMode() {
        return authenticationMode;
    }

    /**
     * Sets the authentication mode.
     *
     * @param authenticationMode the new authentication mode
     */
    public void setAuthenticationMode(String authenticationMode) {
        this.authenticationMode = authenticationMode;
    }

    /**
     * Sets the research staff converter dao.
     *
     * @param researchStaffConverterDao the new research staff converter dao
     */
    @Required
	public void setResearchStaffConverterDao(
			ResearchStaffConverterDao researchStaffConverterDao) {
		this.researchStaffConverterDao = researchStaffConverterDao;
	}

	
	/**
	 * Sets the organization repository.
	 *
	 * @param organizationRepository the new organization repository
	 */
	@Required
	public void setOrganizationRepository(
			OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}
	
	/**
	 * Sets the organization dao.
	 *
	 * @param organizationDao the new organization dao
	 */
	@Required
	public void setOrganizationDao(OrganizationDao organizationDao) {
		this.organizationDao = organizationDao;
	}

	/**
	 * Sets the study repository.
	 *
	 * @param studyRepository the new study repository
	 */
	@Required
	public void setStudyRepository(StudyRepository studyRepository) {
		this.studyRepository = studyRepository;
	}

    /**
     * Gets the site research staff dao.
     *
     * @return the site research staff dao
     */
    public SiteResearchStaffDao getSiteResearchStaffDao() {
        return siteResearchStaffDao;
    }

    /**
     * Sets the site research staff dao.
     *
     * @param siteResearchStaffDao the new site research staff dao
     */
    public void setSiteResearchStaffDao(SiteResearchStaffDao siteResearchStaffDao) {
        this.siteResearchStaffDao = siteResearchStaffDao;
    }
    
    /**
     * Sets the coppa mode for auto completers.
     *
     * @param coppaModeForAutoCompleters the new coppa mode for auto completers
     */
    public void setCoppaModeForAutoCompleters(boolean coppaModeForAutoCompleters) {
		this.coppaModeForAutoCompleters = coppaModeForAutoCompleters;
	}

	/**
	 * Sets the caaers security facade.
	 *
	 * @param caaersSecurityFacade the new caaers security facade
	 */
	public void setCaaersSecurityFacade(
			CaaersSecurityFacade caaersSecurityFacade) {
		this.caaersSecurityFacade = caaersSecurityFacade;
	}

    /**
     * Gets the event factory.
     *
     * @return the event factory
     */
    public EventFactory getEventFactory() {
        return eventFactory;
    }

    /**
     * Sets the event factory.
     *
     * @param eventFactory the new event factory
     */
    public void setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }
}
