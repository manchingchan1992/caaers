package gov.nih.nci.cabig.caaers.api.impl;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.api.ResearchStaffMigratorService;
import gov.nih.nci.cabig.caaers.dao.ResearchStaffDao;
import gov.nih.nci.cabig.caaers.dao.query.ResearchStaffQuery;
import gov.nih.nci.cabig.caaers.domain.Address;
import gov.nih.nci.cabig.caaers.domain.LocalResearchStaff;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.domain.SiteResearchStaff;
import gov.nih.nci.cabig.caaers.domain.SiteResearchStaffRole;
import gov.nih.nci.cabig.caaers.domain.UserGroupType;
import gov.nih.nci.cabig.caaers.domain.repository.ResearchStaffRepository;
import gov.nih.nci.cabig.caaers.integration.schema.common.CaaersServiceResponse;
import gov.nih.nci.cabig.caaers.integration.schema.common.ServiceResponse;
import gov.nih.nci.cabig.caaers.integration.schema.common.Status;
import gov.nih.nci.cabig.caaers.integration.schema.common.WsError;
import gov.nih.nci.cabig.caaers.integration.schema.researchstaff.ResearchStaffType;
import gov.nih.nci.cabig.caaers.integration.schema.researchstaff.SiteResearchStaffRoleType;
import gov.nih.nci.cabig.caaers.integration.schema.researchstaff.SiteResearchStaffType;
import gov.nih.nci.cabig.caaers.integration.schema.researchstaff.Staff;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome.Severity;
import gov.nih.nci.cabig.caaers.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@WebService(endpointInterface="gov.nih.nci.cabig.caaers.api.ResearchStaffMigratorService", serviceName="ResearchStaffMigratorService")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class DefaultResearchStaffMigratorService extends DefaultMigratorService implements
		ResearchStaffMigratorService,ApplicationContextAware {
	
	private static final Log logger = LogFactory.getLog(DefaultResearchStaffMigratorService.class);
	private ResearchStaffDao researchStaffDao;
	private ApplicationContext applicationContext;
	protected ResearchStaffRepository researchStaffRepository;
	
	
	/**
     * Fetches the research staff from the DB
     * 
     * @param nciCode
     * @return
     */
    ResearchStaff fetchResearchStaff(String loginId) {//String nciIdentifier) {
    	ResearchStaffQuery rsQuery = new ResearchStaffQuery();
        if (StringUtils.isNotEmpty(loginId)) {
        	rsQuery.filterByExactLoginId(loginId);
        	//rsQuery.filterByEmailAddress(email);
        }
        List<ResearchStaff> rsList = researchStaffRepository.searchResearchStaff(rsQuery);
        
        if (rsList == null || rsList.isEmpty()) {
            return null;
        }
        return rsList.get(0);
    }
    
    
    public DomainObjectImportOutcome<ResearchStaff> processResearchStaff(ResearchStaffType researchStaffType){
    	
    	DomainObjectImportOutcome<ResearchStaff> researchStaffImportOutcome = new DomainObjectImportOutcome<ResearchStaff>();
    	ResearchStaff xmlResearchStaff = null;
    	ResearchStaff dbResearchStaff = null;
		try {
			xmlResearchStaff = buildResearchStaff(researchStaffType);
			String email = researchStaffType.getEmailAddress();
            String loginId = researchStaffType.getLoginId();
            if (StringUtils.isEmpty(loginId)) {
          	  loginId = email;
            }
            dbResearchStaff = fetchResearchStaff(loginId);
			if(dbResearchStaff == null){
    			validateResearchStaff(xmlResearchStaff,researchStaffImportOutcome,null,false);
    			researchStaffImportOutcome.setImportedDomainObject(xmlResearchStaff);
			}else{
				validateResearchStaff(xmlResearchStaff,researchStaffImportOutcome,null,true);
				syncResearchStaff(xmlResearchStaff,dbResearchStaff);
    			researchStaffImportOutcome.setImportedDomainObject(dbResearchStaff);
			}
		} catch (CaaersSystemException e) {
			xmlResearchStaff = new LocalResearchStaff();
			xmlResearchStaff.setNciIdentifier(researchStaffType.getNciIdentifier());
			xmlResearchStaff.setFirstName(researchStaffType.getFirstName());
			xmlResearchStaff.setLastName(researchStaffType.getLastName());
        	researchStaffImportOutcome = new DomainObjectImportOutcome<ResearchStaff>();
        	researchStaffImportOutcome.setImportedDomainObject(xmlResearchStaff);
        	researchStaffImportOutcome.addErrorMessage(e.getMessage(), Severity.ERROR);
		}
    	return researchStaffImportOutcome;
    }
    
	private List<Organization> checkAuthorizedOrganizations (ResearchStaffType researchStaffType) {
		List<SiteResearchStaffType> siteResearchStaffTypeList = researchStaffType.getSiteResearchStaffs().getSiteResearchStaff();
		String nciIntituteCode = "";
		List<Organization> orgs = new ArrayList<Organization>();
		for(SiteResearchStaffType siteResearchStaffType : siteResearchStaffTypeList){
			nciIntituteCode = siteResearchStaffType.getOrganizationRef().getNciInstituteCode();
			orgs.addAll(getAuthorizedOrganizationsByNameOrNciId(null, nciIntituteCode));
		}
		return orgs;
	}
	
    public CaaersServiceResponse saveResearchStaff(Staff staff) {
    	List<ResearchStaffType> researchStaffList = staff.getResearchStaff();
    	ResearchStaff xmlResearchStaff = null;
    	ResearchStaff dbResearchStaff = null;
    	
    	List<WsError> wsErrors = new ArrayList<WsError>();
    	CaaersServiceResponse caaersServiceResponse = new CaaersServiceResponse();
    	ServiceResponse serviceResponse = new ServiceResponse();
    	serviceResponse.setStatus(Status.FAILED_TO_PROCESS);
    	
    	DomainObjectImportOutcome<ResearchStaff> researchStaffImportOutcome = new DomainObjectImportOutcome<ResearchStaff>();
    	
    	for (ResearchStaffType researchStaffType:researchStaffList) {
    		if (checkAuthorizedOrganizations(researchStaffType).size() == 0){
    			WsError err = new WsError();
    			err.setErrorDesc("Failed to process ResearchStaff ::: nciIdentifier : "+researchStaffType.getNciIdentifier() + " ::: firstName : "+researchStaffType.getFirstName()+ " ::: lastName : "+researchStaffType.getLastName()) ;
    			err.setException("User not authorized on this Organization : " + researchStaffType.getNciIdentifier());
    			wsErrors.add(err);
    		}
    		
    		try {
    			xmlResearchStaff = buildResearchStaff(researchStaffType);
    			String email = researchStaffType.getEmailAddress();
                String loginId = researchStaffType.getLoginId();
                if (StringUtils.isEmpty(loginId)) {
              	  loginId = email;
                }
                dbResearchStaff = fetchResearchStaff(loginId);
    			if(dbResearchStaff == null){
    				validateResearchStaff(xmlResearchStaff,researchStaffImportOutcome,wsErrors,false);
    				if(wsErrors.size() == 0){
    					saveResearchStaff(xmlResearchStaff);
    				}
    			}else{
    				validateResearchStaff(xmlResearchStaff,researchStaffImportOutcome,wsErrors,true);
    				if(wsErrors.size() == 0){
        				syncResearchStaff(xmlResearchStaff,dbResearchStaff);
        				saveResearchStaff(dbResearchStaff);
    				}
    			}
    		} catch (CaaersSystemException e) {
    			xmlResearchStaff = new LocalResearchStaff();
    			xmlResearchStaff.setNciIdentifier(researchStaffType.getNciIdentifier());
    			xmlResearchStaff.setFirstName(researchStaffType.getFirstName());
    			xmlResearchStaff.setLastName(researchStaffType.getLastName());
            	researchStaffImportOutcome.setImportedDomainObject(xmlResearchStaff);
            	researchStaffImportOutcome.addErrorMessage(e.getMessage(), Severity.ERROR);
 
    			WsError err = new WsError();
    			err.setErrorDesc("Failed to process ResearchStaff ::: nciIdentifier : "+researchStaffType.getNciIdentifier() + " ::: firstName : "+researchStaffType.getFirstName()+ " ::: lastName : "+researchStaffType.getLastName()) ;
    			err.setException(e.getMessage());
    			wsErrors.add(err);           	
    		}
    	}
    	serviceResponse.setWsError(wsErrors);
    	if (wsErrors.size() == 0) {
    		serviceResponse.setStatus(Status.PROCESSED);
    	}
    	caaersServiceResponse.setServiceResponse(serviceResponse);
    	return caaersServiceResponse;
    }
    
    public ResearchStaff buildResearchStaff(ResearchStaffType researchStaffDto) throws CaaersSystemException {
    	  try {
              logger.info("Begining of ResearchStaffMigrator : buildResearchStaff");
               
              String nciIdentifier = researchStaffDto.getNciIdentifier();
              String email = researchStaffDto.getEmailAddress();
              String loginId = researchStaffDto.getLoginId();
              if (StringUtils.isEmpty(loginId)) {
            	  loginId = email;
              }
              
              	ResearchStaff researchStaff = new LocalResearchStaff();
              	researchStaff.setNciIdentifier(nciIdentifier); 
              	if (StringUtils.isEmpty(loginId)) {
              		researchStaff.setLoginId(researchStaffDto.getEmailAddress());
              	} else {
              		researchStaff.setLoginId(loginId);
              	} 
                researchStaff.setFirstName(researchStaffDto.getFirstName());
                researchStaff.setLastName(researchStaffDto.getLastName());
                researchStaff.setMiddleName(researchStaffDto.getMiddleName());              
                researchStaff.setFaxNumber(researchStaffDto.getFaxNumber());
                researchStaff.setPhoneNumber(researchStaffDto.getPhoneNumber());
                researchStaff.setEmailAddress(researchStaffDto.getEmailAddress());
                Address researchStaffAddress = new Address();
                researchStaffAddress.setStreet(researchStaffDto.getStreet());
                researchStaffAddress.setCity(researchStaffDto.getCity());
                researchStaffAddress.setState(researchStaffDto.getState());
                if(researchStaffDto.getZip() != null & !StringUtils.isEmpty(researchStaffDto.getZip())){
              	 researchStaffAddress.setZip(Integer.parseInt(researchStaffDto.getZip()));
                }
                researchStaff.setAddress(researchStaffAddress);
                
                List<SiteResearchStaffType> siteRsTypeList= researchStaffDto.getSiteResearchStaffs().getSiteResearchStaff();
                Address siteResearchStaffAddress = null;
                SiteResearchStaffRole siteResearchStaffRole = null;
                List<SiteResearchStaffRoleType> srsRoleTypes = null;
                for (SiteResearchStaffType siteResearchStaffType : siteRsTypeList) {
	              	SiteResearchStaff siteResearchStaff = new SiteResearchStaff();
	              	siteResearchStaff.setAssociateAllStudies(siteResearchStaffType.isAssociateAllStudies());
	              	siteResearchStaff.setEmailAddress(siteResearchStaffType.getEmailAddress());
	              	siteResearchStaff.setPhoneNumber(siteResearchStaffType.getPhoneNumber());
	              	siteResearchStaff.setFaxNumber(siteResearchStaffType.getFaxNumber());
	              	siteResearchStaffAddress = new Address();
	              	siteResearchStaffAddress.setStreet(siteResearchStaffType.getStreet());
	              	siteResearchStaffAddress.setCity(siteResearchStaffType.getCity());
	              	siteResearchStaffAddress.setState(siteResearchStaffType.getState());
	              	if(siteResearchStaffType.getZip() != null & !StringUtils.isEmpty(siteResearchStaffType.getZip())){
	              		siteResearchStaffAddress.setZip(Integer.parseInt(siteResearchStaffType.getZip()));
	              	}
	              	siteResearchStaff.setAddress(siteResearchStaffAddress);
	                	
	              	Organization org = fetchOrganization(siteResearchStaffType.getOrganizationRef().getNciInstituteCode());
	                	
	              	srsRoleTypes = 	siteResearchStaffType.getSiteResearchStaffRoles().getSiteResearchStaffRole();
	              	for(SiteResearchStaffRoleType srsRoleType : srsRoleTypes){
	              		siteResearchStaffRole = new SiteResearchStaffRole();
	              		siteResearchStaffRole.setRoleCode(srsRoleType.getRole().value());
	              		siteResearchStaffRole.setStartDate(srsRoleType.getStartDate().toGregorianCalendar().getTime());
	              		if(srsRoleType.getEndDate() != null){
	              			siteResearchStaffRole.setEndDate(srsRoleType.getEndDate().toGregorianCalendar().getTime());
	              		}
	              		siteResearchStaffRole.setSiteResearchStaff(siteResearchStaff);
	              		siteResearchStaff.addSiteResearchStaffRole(siteResearchStaffRole);
	              	}
	                	siteResearchStaff.setOrganization(org);
	                	siteResearchStaff.setResearchStaff(researchStaff);
	                	researchStaff.addSiteResearchStaff(siteResearchStaff);
                }              	
              
              researchStaff.getUserGroupTypes().clear();
              for (String roleCode : researchStaff.getAllRoles()) {
            	  researchStaff.addUserGroupType(UserGroupType.valueOf(roleCode));
              }
              
              return researchStaff;

          } catch (Exception e) {
              logger.error("Error while creating research staff", e);
              throw new CaaersSystemException(e.getMessage(), e);
          }	  	
    	
    }
	public void saveResearchStaff(ResearchStaff researchStaff) throws CaaersSystemException {

        try {
            logger.info("Begining of ResearchStaffMigrator : saveResearchStaff");             
            
            //save 
            researchStaffRepository.save(researchStaff,"URL");
            logger.info("Created the research staff :" + researchStaff.getId());
            logger.info("End of ResearchStaffMigrator : saveResearchStaff");

        } catch (Exception e) {
            logger.error("Error while creating research staff", e);
            throw new CaaersSystemException("Unable to create research staff : "+ e.getMessage(), e);
        }	
        
	}
	
	/**
	 * 
	 * @param xmlResearchStaff
	 * @param dbResearchStaff
	 */
	private void syncResearchStaff(ResearchStaff xmlResearchStaff, ResearchStaff dbResearchStaff){
		
		//do the basic property sync
		dbResearchStaff.setEmailAddress(xmlResearchStaff.getEmailAddress());
		dbResearchStaff.setPhoneNumber(xmlResearchStaff.getPhoneNumber());
		dbResearchStaff.setFaxNumber(xmlResearchStaff.getFaxNumber());
		dbResearchStaff.getAddress().setStreet(xmlResearchStaff.getAddress().getStreet());
		dbResearchStaff.getAddress().setCity(xmlResearchStaff.getAddress().getCity());
		dbResearchStaff.getAddress().setState(xmlResearchStaff.getAddress().getState());
		dbResearchStaff.getAddress().setZip(xmlResearchStaff.getAddress().getZip());
		
		//do the site research staff sync
		if(CollectionUtils.isEmpty(xmlResearchStaff.getSiteResearchStaffs())) return;  //nothing provided in xml input
		List<SiteResearchStaff> existingSiteResearchStaffs = new ArrayList<SiteResearchStaff>();
		List<SiteResearchStaff> newSiteResearchStaffs = new ArrayList<SiteResearchStaff>();
		for(SiteResearchStaff xmlSiteResearchStaff : xmlResearchStaff.getSiteResearchStaffs()){
			SiteResearchStaff existing = dbResearchStaff.findSiteResearchStaff(xmlSiteResearchStaff);
			if(existing != null){
				//sync the roles
				List<SiteResearchStaffRole> existingRoles = new ArrayList<SiteResearchStaffRole>();
				List<SiteResearchStaffRole> newRoles = new ArrayList<SiteResearchStaffRole>();
				if(CollectionUtils.isNotEmpty(xmlSiteResearchStaff.getSiteResearchStaffRoles())){
					for(SiteResearchStaffRole xmlRole : xmlSiteResearchStaff.getSiteResearchStaffRoles()){
						SiteResearchStaffRole existingRole = existing.findSiteResearchStaffRole(xmlRole);
						if(existingRole != null){
							existingRole.setStartDate(xmlRole.getStartDate());
							existingRole.setEndDate(xmlRole.getEndDate());
							existingRoles.add(existingRole);
						}else{
							xmlRole.setSiteResearchStaff(existing);
							newRoles.add(xmlRole);
						}
					}
					
					//add new roles
					existing.getSiteResearchStaffRoles().addAll(newRoles);
					existingSiteResearchStaffs.add(existing);
				}
				
			}else {
				newSiteResearchStaffs.add(xmlSiteResearchStaff);
			}
			
		}
		
		//add the items in new
		for(SiteResearchStaff sRs : newSiteResearchStaffs){
			dbResearchStaff.addSiteResearchStaff(sRs);
		}
	}
	
	
	private void validateResearchStaff(ResearchStaff researchStaff ,DomainObjectImportOutcome<ResearchStaff> researchStaffImportOutcome, List<WsError> wsErrors,boolean isExisting) throws CaaersSystemException{
		Date now = new Date();
		WsError err = null;
        List<SiteResearchStaff> siteResearchStaffs = researchStaff.getSiteResearchStaffs();
        for(SiteResearchStaff siteResearchStaff : siteResearchStaffs){
        	for(SiteResearchStaffRole siteResearchStaffRole : siteResearchStaff.getSiteResearchStaffRoles()){
                
        		if(siteResearchStaff.getId() == null & !isExisting){
                	//startdate cannot be less than today's date
                    if(siteResearchStaffRole.getStartDate() != null){
                    	if(DateUtils.compareDate(siteResearchStaffRole.getStartDate(),now) < 0){
                    		researchStaffImportOutcome.addErrorMessage("Start date cannot be before today's date for role " +siteResearchStaffRole.getRoleCode()+ " at " +siteResearchStaff.getOrganization().getNciInstituteCode(), Severity.ERROR);
                    		if(wsErrors != null){
                    			err = new WsError();
                    			err.setErrorDesc("Start date cannot be before today's date for role " +siteResearchStaffRole.getRoleCode()+ " at " +siteResearchStaff.getOrganization().getNciInstituteCode());
                    			wsErrors.add(err);
                    		}
                    	}
                    }
                }
                
                if(siteResearchStaffRole.getEndDate() != null){
                	if(DateUtils.compareDate(siteResearchStaffRole.getEndDate(),now) < 0){
                		researchStaffImportOutcome.addErrorMessage("End date cannot be before today's date, for role " +siteResearchStaffRole.getRoleCode()+ " at " +siteResearchStaff.getOrganization().getNciInstituteCode(), Severity.ERROR);
                		if(wsErrors != null){
                			err = new WsError();
                			err.setErrorDesc("End date cannot be before today's date, for role " +siteResearchStaffRole.getRoleCode()+ " at " +siteResearchStaff.getOrganization().getNciInstituteCode());
                			wsErrors.add(err);
                		}
                    }
                }
                if(siteResearchStaffRole.getStartDate() != null && siteResearchStaffRole.getEndDate() != null){
                	if(DateUtils.compareDate(siteResearchStaffRole.getEndDate(), siteResearchStaffRole.getStartDate()) < 0){
                		researchStaffImportOutcome.addErrorMessage("End date cannot be before Start date, for role " +siteResearchStaffRole.getRoleCode()+ " at " +siteResearchStaff.getOrganization().getNciInstituteCode(), Severity.ERROR);
                		if(wsErrors != null){
                			err = new WsError();
                			err.setErrorDesc("End date cannot be before Start date, for role " +siteResearchStaffRole.getRoleCode()+ " at " +siteResearchStaff.getOrganization().getNciInstituteCode());
                			wsErrors.add(err);
                		}
                	}
                }
        	}
        }
	}
	
	
	//CONFIGURATION

    @Required
    public void setResearchStaffDao(ResearchStaffDao researchStaffDao) {
		this.researchStaffDao = researchStaffDao;
	}

    @Required
	public ResearchStaffDao getResearchStaffDao() {
		return researchStaffDao;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
		
	}
	public void setResearchStaffRepository(
			ResearchStaffRepository researchStaffRepository) {
		this.researchStaffRepository = researchStaffRepository;
	}
}
