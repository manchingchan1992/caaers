/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.study;

import gov.nih.nci.cabig.caaers.domain.LocalOrganization;
import gov.nih.nci.cabig.caaers.domain.LocalResearchStaff;
import gov.nih.nci.cabig.caaers.domain.SiteResearchStaff;
import gov.nih.nci.cabig.caaers.domain.StudyOrganization;
import gov.nih.nci.cabig.caaers.domain.StudyPersonnel;
import gov.nih.nci.cabig.caaers.domain.StudySite;
import gov.nih.nci.cabig.caaers.domain.workflow.WorkflowConfig;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.utils.Lov;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ion C. Olaru
 */

public class StudyPersonnelTabTestCase extends AbstractStudyWebTestCase {

 

    protected StudyTab createTab() {
        PersonnelTab tab = new PersonnelTab();
        tab.setConfigurationProperty(new ConfigProperty());
        Map<String, List<Lov>> map = new HashMap<String, List<Lov>>();
        map.put("studyPersonnelRoleRefData", new ArrayList<Lov>());
        map.put("studyPersonnelStatusRefData", new ArrayList<Lov>());
        tab.getConfigurationProperty().setMap(map);

        return tab;
    }

    public void testReferenceData() {
        replayMocks();
        Map<String, Object> output = tab.referenceData(request, command);
        verifyMocks();
        assertNotNull(output);
    }

    public void testValidation() {
        StudyOrganization so = new StudySite();
        so.setOrganization(new LocalOrganization());
        so.getOrganization().setName("A");

        StudyPersonnel sp;
        sp = new StudyPersonnel();
        SiteResearchStaff siteResearchStaff = new SiteResearchStaff();
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.getResearchStaff().setNciIdentifier("Id1");
        siteResearchStaff.getResearchStaff().setLoginId("Id1");
        sp.setSiteResearchStaff(siteResearchStaff);
        so.addStudyPersonnel(sp);

        sp = new StudyPersonnel();
        siteResearchStaff = new SiteResearchStaff();
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.getResearchStaff().setNciIdentifier("Id2");
        siteResearchStaff.getResearchStaff().setLoginId("Id2");
        sp.setSiteResearchStaff(siteResearchStaff);
        so.addStudyPersonnel(sp);

        sp = new StudyPersonnel();
        siteResearchStaff = new SiteResearchStaff();
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.getResearchStaff().setNciIdentifier("Id3");
        siteResearchStaff.getResearchStaff().setLoginId("Id3");
        sp.setSiteResearchStaff(siteResearchStaff);
        so.addStudyPersonnel(sp);

        study.addStudyOrganization(so);

        StudyOrganization _so = command.getStudy().getStudyOrganizations().get(0);
        assertEquals(3, _so.getStudyPersonnels().size());

        replayMocks();
        tab.validate(command, errors);
        verifyMocks();

        System.out.println(errors);
        assertEquals(0, errors.getErrorCount());
    }

    public void testPostProcess() {
        StudyOrganization so = new StudySite();
        so.setOrganization(new LocalOrganization());
        so.getOrganization().setName("A");

        StudyPersonnel sp;
        sp = new StudyPersonnel();
        SiteResearchStaff siteResearchStaff = new SiteResearchStaff();
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());        
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.getResearchStaff().setNciIdentifier("Id1");
        siteResearchStaff.getResearchStaff().setLoginId("Id1");
        sp.setSiteResearchStaff(siteResearchStaff);
        so.addStudyPersonnel(sp);

        sp = new StudyPersonnel();
        siteResearchStaff = new SiteResearchStaff();
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.getResearchStaff().setNciIdentifier("Id2");
        siteResearchStaff.getResearchStaff().setLoginId("Id2");
        sp.setSiteResearchStaff(siteResearchStaff);
        so.addStudyPersonnel(sp);

        sp = new StudyPersonnel();
        siteResearchStaff = new SiteResearchStaff();
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.getResearchStaff().setNciIdentifier("Id3");
        siteResearchStaff.getResearchStaff().setLoginId("Id3");
        sp.setSiteResearchStaff(siteResearchStaff);
        so.addStudyPersonnel(sp);
        
        study.addStudyOrganization(so);

        StudyOrganization _so = command.getStudy().getStudyOrganizations().get(0);
        assertEquals(3, _so.getStudyPersonnels().size());

        request.setParameter("_action", "removeStudyPersonnel");
        request.setParameter("_selectedPersonnel", "0");
        command.setStudySiteIndex(0);

        replayMocks();
        tab.postProcess(request, command, errors);
        verifyMocks();

        assertEquals(2, _so.getStudyPersonnels().size());
    }

    public void testCreateFieldGroups() {
        StudyOrganization so = new StudySite();
        so.setOrganization(new LocalOrganization());
        so.getOrganization().setName("A");

        StudyPersonnel sp;
        sp = new StudyPersonnel();
        SiteResearchStaff siteResearchStaff = new SiteResearchStaff();
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.getResearchStaff().setNciIdentifier("Id1");
        siteResearchStaff.getResearchStaff().setLoginId("Id1");
        sp.setSiteResearchStaff(siteResearchStaff);
        so.addStudyPersonnel(sp);

        sp = new StudyPersonnel();
        siteResearchStaff = new SiteResearchStaff();
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.getResearchStaff().setNciIdentifier("Id2");
        siteResearchStaff.getResearchStaff().setLoginId("Id2");
        sp.setSiteResearchStaff(siteResearchStaff);
        so.addStudyPersonnel(sp);

        sp = new StudyPersonnel();
        siteResearchStaff = new SiteResearchStaff();
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.setResearchStaff(new LocalResearchStaff());
        siteResearchStaff.getResearchStaff().setNciIdentifier("Id3");
        siteResearchStaff.getResearchStaff().setLoginId("Id3");
        sp.setSiteResearchStaff(siteResearchStaff);
        so.addStudyPersonnel(sp);

        study.addStudyOrganization(so);

        replayMocks();
        Map<String, InputFieldGroup> map = tab.createFieldGroups(command);
        verifyMocks();
        assertEquals(0, errors.getErrorCount());
        assertEquals(1, map.size());
    }

    
}
