/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.domain.LocalResearchStaff;
import gov.nih.nci.cabig.caaers.domain.RemoteResearchStaff;
import gov.nih.nci.cabig.caaers.domain.ResearchStaff;
import gov.nih.nci.cabig.caaers.domain.repository.ResearchStaffRepository;
import gov.nih.nci.cabig.caaers.web.WebTestCase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class EditResearchStaffControllerTest extends WebTestCase {
	
	private EditResearchStaffController controller;
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        controller = new EditResearchStaffController();
        controller.setResearchStaffRepository(new ResearchStaffRepository(){
        	public void save(final ResearchStaff researchStaff, String changeURL) {
        		
        	}
        	
        	public void convertToRemote(ResearchStaff localResearchStaff, ResearchStaff remoteResearchStaff){
        		
        	}
        	
        	public void evict(ResearchStaff researchStaff){
        		
        	}
        	
        	public ResearchStaff getById(final int id) {
                return new LocalResearchStaff();
            }
        	
        	public List<ResearchStaff> getRemoteResearchStaff(final ResearchStaff researchStaff){
        		List<ResearchStaff> rsList = new ArrayList<ResearchStaff>();
        		ResearchStaff r1 = new RemoteResearchStaff();
        		r1.setNciIdentifier("1");
        		ResearchStaff r2 = new RemoteResearchStaff();
        		r2.setNciIdentifier("2");
        		rsList.add(r1);
        		rsList.add(r2);
        		return rsList;
        	}
        	
        });
    }

	public void test(){
		//@TODO
		//Command object was changed from ResearchStaff to ResearchStaffCommand. This TestCase was not modified.
		//Have to revist the tests commented below.
	}
	
	/**
	 * 
	 * @throws Exception
	 */
//	public void testOnBindAndValidateWithResults() throws Exception {
//        request.setMethod("POST");
//        request.setParameter("_action", "syncResearchStaff");
//        ResearchStaff command = new LocalResearchStaff();
//        BindException errors = new BindException(command, "command");
//        controller.onBindAndValidate(request, command, errors, 1);
//        assertNotNull("List should not be Null", command.getExternalResearchStaff());
//        //TODO: Changed thse values to 0s from 2,1 .. need to check with monish - By Srini
//        assertEquals(0, command.getExternalResearchStaff().size());
//        assertEquals(0, errors.getErrorCount());
//	}
	

//	public void testOnBindAndValidateWithOutResults() throws Exception {
//		controller.setResearchStaffRepository(new ResearchStaffRepository(){
//        	public List<ResearchStaff> getRemoteResearchStaff(final ResearchStaff researchStaff){
//        		List<ResearchStaff> rsList = new ArrayList<ResearchStaff>();
//        		return rsList;
//        	}
//        	
//        });
//		
//		request.setMethod("POST");
//        request.setParameter("_action", "syncResearchStaff");
//        ResearchStaff command = new LocalResearchStaff();
//        BindException errors = new BindException(command, "command");
//        controller.onBindAndValidate(request, command, errors, 1);
//        assertEquals(0, command.getExternalResearchStaff().size());
//	}
}
