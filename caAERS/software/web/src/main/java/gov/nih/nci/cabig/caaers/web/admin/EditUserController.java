package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.dao.query.OrganizationQuery;
import gov.nih.nci.cabig.caaers.dao.query.StudyQuery;
import gov.nih.nci.cabig.caaers.domain.Investigator;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.Person;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.domain.SiteInvestigator;
import gov.nih.nci.cabig.caaers.domain.SiteResearchStaff;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.UserGroupType;
import gov.nih.nci.cabig.caaers.domain._User;
import gov.nih.nci.cabig.ctms.suite.authorization.ProvisioningSession;
import gov.nih.nci.cabig.ctms.suite.authorization.SuiteRole;
import gov.nih.nci.cabig.ctms.suite.authorization.SuiteRoleMembership;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Monish
 *
 */
public class EditUserController extends UserController<UserCommand> {
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView processFinish(HttpServletRequest request,HttpServletResponse response, Object userCommand, BindException errors) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView("admin/user_confirmation");
		UserCommand command = (UserCommand)userCommand;
        if (!errors.hasErrors()) {
        	String statusMessage = "";
        	if(command.getCreateAsPerson() && command.getCreateAsUser()){
        		statusMessage = "Updated " +command.getPersonType()+ " with login capability"; 
        	}
        	if(command.getCreateAsPerson() && !command.getCreateAsUser()){
        		statusMessage = "Updated " +command.getPersonType()+ " without login capability";
        	}
        	if(!command.getCreateAsPerson() && command.getCreateAsUser()){
        		statusMessage = "Updated a User with login capability";
        	}
            modelAndView.getModel().put("flashMessage", statusMessage);
        }
        modelAndView.addAllObjects(errors.getModel());
		return modelAndView;
	}
	
	
	@Override
    protected Object formBackingObject(final HttpServletRequest request) throws ServletException {
		request.getSession().removeAttribute(getReplacedCommandSessionAttributeName(request));
		
		String recordType = request.getParameter("recordType");
		String userName = request.getParameter("userName");
		String id = request.getParameter("id");
		
		UserCommand command = new UserCommand();
		command.setCreateMode(Boolean.FALSE);
		command.setEditMode(Boolean.TRUE);
		
		if("CSM_RECORD".equals(recordType)){
			_User user = userRepository.getUserByLoginName(userName);
			if(user.getCsmUser() != null){
				command.setFirstName(user.getCsmUser().getFirstName());
				command.setLastName(user.getCsmUser().getLastName());
				command.setEmailAddress(user.getCsmUser().getEmailId());
				command.setUserName(user.getCsmUser().getLoginName());
	 			//Get all the suite role memberships for user
				populateRoleMemberships(user,command);
				populateSiteMap(command);
				populateStudyMap(command);
			}
			command.setPerson(null);
			command.setPersonType("Please Select");
			command.setCreateAsUser(Boolean.TRUE);
			command.setCreateAsPerson(Boolean.FALSE);
			command.setUser(user);
			command.buildRolesHelper();
		}else if("RESEARCHSTAFF_RECORD".equals(recordType) || "INVESTIGATOR_RECORD".equals(recordType)){
			Person p = personRepository.getById(Integer.parseInt(id));
			command.setFirstName(p.getFirstName());
			command.setMiddleName(p.getMiddleName());
			command.setLastName(p.getLastName());
			command.setEmailAddress(p.getEmailAddress());
			if(p instanceof ResearchStaff){
				command.setNciIdentifier(((ResearchStaff)p).getNciIdentifier());
				command.setPersonType("ResearchStaff");
				SitePerson sitePerson = null;
				for(SiteResearchStaff srs : ((ResearchStaff)p).getSiteResearchStaffs()){
					sitePerson = new SitePerson();
					sitePerson.setId(srs.getId());
					sitePerson.setOrganization(srs.getOrganization());
					sitePerson.setPerson(srs.getResearchStaff());
					sitePerson.setAddress(srs.getAddress());
					sitePerson.setPhoneNumber(srs.getPhoneNumber());
					sitePerson.setFaxNumber(srs.getFaxNumber());
					sitePerson.setEmailAddress(srs.getEmailAddress());
					command.addSitePersonnel(sitePerson);
				}
			}
			if(p instanceof Investigator){
				command.setNciIdentifier(((Investigator)p).getNciIdentifier());
				command.setPersonType("Investigator");
				SitePerson sitePerson = null;
				for(SiteInvestigator siteInv : ((Investigator)p).getSiteInvestigators()){
					sitePerson = new SitePerson();
					sitePerson.setId(siteInv.getId());
					sitePerson.setOrganization(siteInv.getOrganization());
					sitePerson.setPerson(siteInv.getInvestigator());
					sitePerson.setEmailAddress(siteInv.getEmailAddress());
					command.addSitePersonnel(sitePerson);
				}
			}
			command.setCreateAsPerson(Boolean.TRUE);
			command.setPerson(p);
			
			if(p.getCaaersUser() != null){
				_User user = userRepository.getUserByLoginName(p.getCaaersUser().getLoginName());
				p.setCaaersUser(user);
				command.setCreateAsUser(Boolean.TRUE);
				command.setUserName(user.getLoginName());
				command.setUser(user);
				populateRoleMemberships(p.getCaaersUser(),command);
				populateSiteMap(command);
				populateStudyMap(command);
			}else{
				command.setCreateAsUser(Boolean.FALSE);
				command.setUser(new _User());
			}
			command.buildRolesHelper();
		}
		return command;
	}
	
	/**
	 * Populates SuiteRoleMembeships for the user.
	 * @param csmUser
	 * @param command
	 */
	private void populateRoleMemberships(_User user,UserCommand command){
		ProvisioningSession session =  proSessionFactory.createSession(user.getCsmUser().getUserId());
		for(UserGroupType group : user.getUserGroupTypes()){
			command.addRoleMembership(session.getProvisionableRoleMembership(SuiteRole.getByCsmName(group.getCsmName())));
		}
	}
	
	/**
	 * Builds a Map containing <NCICode> <String to display>. This Map is used in the UI. 
	 * @param command
	 */
	private void populateSiteMap(UserCommand command){
		OrganizationQuery query = null;
		StringBuilder displayValue = null;
		for(SuiteRoleMembership srM : command.getRoleMemberships()){
			if(srM.getRole().isScoped()){
				if(!srM.isAllSites()){
					for(String nciCode : srM.getSiteIdentifiers()){
						if(!command.getSiteMap().containsKey(nciCode)){
							query = new OrganizationQuery();
							query.filterByNciCodeExactMatch(nciCode);
							List<Organization> orgs = organizationRepository.getLocalOrganizations(query);
							if(orgs.isEmpty()){
								command.getSiteMap().put(nciCode, nciCode);
							}else{
								displayValue = new StringBuilder();
								Organization org = orgs.get(0);
								displayValue.append("(").append(org.getNciInstituteCode()).append(") ");
								displayValue.append(org.getName());
								command.getSiteMap().put(nciCode, displayValue.toString());
							}							
						}
					}
				}
			}
		}
	}
	
	/**
	 * Builds a Map containing <Study Coordinating Center Identifier> <String to display>. This Map is used in the UI. 
	 * @param command
	 */
	private void populateStudyMap(UserCommand command){
		StudyQuery query = null;
		StringBuilder displayValue = null;
		for(SuiteRoleMembership srM : command.getRoleMemberships()){
			if(srM.getRole().isScoped()){
				if(srM.getRole().isSiteScoped() && srM.getRole().isStudyScoped()){
					if(!srM.isAllStudies()){
						for(String studyIdentifier : srM.getStudyIdentifiers()){
							if(!command.getStudyMap().containsKey(studyIdentifier)){
								query = new StudyQuery();
								query.filterByIdentifierValueExactMatch(studyIdentifier);
								List<Study> studies = studyRepository.find(query);
								if(studies.isEmpty()){
									command.getStudyMap().put(studyIdentifier, studyIdentifier);
								}else{
									displayValue = new StringBuilder();
									Study study = studies.get(0);
									displayValue.append("(").append(study.getCoordinatingCenterIdentifierValue()).append(") ");
							        String suffix = "";
							        String studyTitle = study.getShortTitle();
							        int end = studyTitle.length();
							        if(end > 30){
							        	end = 30;
							        	suffix = "...";
							        }
							        studyTitle = StringUtils.substring(studyTitle, 0, end);
							        studyTitle = studyTitle+suffix;
							        displayValue.append(studyTitle);
							        command.getStudyMap().put(studyIdentifier, displayValue.toString());
								}								
							}
						}						
					}
				}
			}
		}
	}
	
}
