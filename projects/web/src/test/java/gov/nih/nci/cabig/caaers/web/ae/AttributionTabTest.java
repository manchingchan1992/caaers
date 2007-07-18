package gov.nih.nci.cabig.caaers.web.ae;

import static gov.nih.nci.cabig.caaers.CaaersUseCase.*;
import gov.nih.nci.cabig.caaers.CaaersUseCases;
import gov.nih.nci.cabig.caaers.domain.StudyAgent;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.ConcomitantMedication;
import gov.nih.nci.cabig.caaers.domain.Agent;
import gov.nih.nci.cabig.caaers.domain.TreatmentInformation;
import gov.nih.nci.cabig.caaers.domain.CourseAgent;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.OtherCause;
import gov.nih.nci.cabig.caaers.domain.DiseaseHistory;
import gov.nih.nci.cabig.caaers.domain.CtepStudyDisease;
import gov.nih.nci.cabig.caaers.domain.DiseaseTerm;
import static gov.nih.nci.cabig.caaers.domain.Fixtures.*;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;

import java.util.Map;
import java.util.List;
import java.math.BigDecimal;

/**
 * @author Rhett Sutphin
 */
@CaaersUseCases({ CREATE_EXPEDITED_REPORT })
public class AttributionTabTest extends AeTabTestCase {
    @Override
    protected AttributionTab createTab() {
        return new AttributionTab();
    }

    public void testCourseAgentFieldsIncluded() throws Exception {
        ensureCourseAgentCount(2);
        ensureAeCount(4);

        CourseAgent ca0 = command.getAeReport().getTreatmentInformation().getCourseAgents().get(0);
        ca0.setStudyAgent(createStudyAgent("Witch niece"));
        ca0.getDose().setAmount(new BigDecimal(56));
        ca0.getDose().setUnits("ug");
        ca0.getDose().setRoute("IV");

        Map<String, InputFieldGroup> map = getTab().createFieldGroups(command);
        assertEquals("Wrong number of study agent groups", 2, map.size());
        InputFieldGroup actualGroup0 = map.get("courseAgent0");
        assertNotNull(actualGroup0);
        assertEquals("Witch niece (56ug IV)", actualGroup0.getDisplayName());
        assertEquals(4, actualGroup0.getFields().size());
        assertEquals("attributionMap[courseAgent][0][0]", actualGroup0.getFields().get(0).getPropertyName());
        assertEquals("attributionMap[courseAgent][1][0]", actualGroup0.getFields().get(1).getPropertyName());
        assertEquals("attributionMap[courseAgent][2][0]", actualGroup0.getFields().get(2).getPropertyName());
        assertEquals("attributionMap[courseAgent][3][0]", actualGroup0.getFields().get(3).getPropertyName());

        InputFieldGroup actualGroup1 = map.get("courseAgent1");
        assertNotNull(actualGroup1);
        assertEquals(4, actualGroup0.getFields().size());
        assertEquals("attributionMap[courseAgent][0][1]", actualGroup1.getFields().get(0).getPropertyName());
        assertEquals("attributionMap[courseAgent][1][1]", actualGroup1.getFields().get(1).getPropertyName());
        assertEquals("attributionMap[courseAgent][2][1]", actualGroup1.getFields().get(2).getPropertyName());
        assertEquals("attributionMap[courseAgent][3][1]", actualGroup1.getFields().get(3).getPropertyName());
    }

    public void testConMedFieldsIncluded() throws Exception {
        ensureConMedCount(3);
        ensureAeCount(3);

        Map<String, InputFieldGroup> map = getTab().createFieldGroups(command);
        assertEquals("Wrong number of con med groups", 3, map.size());

        InputFieldGroup actualGroup0 = map.get("conMed0");
        assertNotNull(actualGroup0);
        assertEquals(3, actualGroup0.getFields().size());
        assertEquals("attributionMap[conMed][0][0]", actualGroup0.getFields().get(0).getPropertyName());
        assertEquals("attributionMap[conMed][1][0]", actualGroup0.getFields().get(1).getPropertyName());
        assertEquals("attributionMap[conMed][2][0]", actualGroup0.getFields().get(2).getPropertyName());

        InputFieldGroup actualGroup1 = map.get("conMed1");
        assertNotNull(actualGroup1);
        assertEquals(3, actualGroup1.getFields().size());
        assertEquals("attributionMap[conMed][0][1]", actualGroup1.getFields().get(0).getPropertyName());
        assertEquals("attributionMap[conMed][1][1]", actualGroup1.getFields().get(1).getPropertyName());
        assertEquals("attributionMap[conMed][2][1]", actualGroup1.getFields().get(2).getPropertyName());

        InputFieldGroup actualGroup2 = map.get("conMed2");
        assertNotNull(actualGroup2);
        assertEquals(3, actualGroup2.getFields().size());
        assertEquals("attributionMap[conMed][0][2]", actualGroup2.getFields().get(0).getPropertyName());
        assertEquals("attributionMap[conMed][1][2]", actualGroup2.getFields().get(1).getPropertyName());
        assertEquals("attributionMap[conMed][2][2]", actualGroup2.getFields().get(2).getPropertyName());
    }

    public void testOtherCauseFieldsIncluded() throws Exception {
        ensureOtherCauseCount(2);
        ensureAeCount(1);

        Map<String, InputFieldGroup> map = getTab().createFieldGroups(command);
        assertEquals("Wrong number of other cause groups", 2, map.size());

        InputFieldGroup actualGroup0 = map.get("other0");
        assertNotNull(actualGroup0);
        assertEquals(1, actualGroup0.getFields().size());
        assertEquals("attributionMap[other][0][0]", actualGroup0.getFields().get(0).getPropertyName());
        InputFieldGroup actualGroup1 = map.get("other1");
        assertNotNull(actualGroup1);
        assertEquals(1, actualGroup1.getFields().size());
        assertEquals("attributionMap[other][0][1]", actualGroup1.getFields().get(0).getPropertyName());
    }

    public void testDiseaseFieldsIncluded() throws Exception {
        DiseaseHistory dh = new DiseaseHistory();
        dh.setCtepStudyDisease(new CtepStudyDisease());
        dh.getCtepStudyDisease().setTerm(new DiseaseTerm());
        dh.getCtepStudyDisease().getTerm().setTerm("Something");
        command.getAeReport().setDiseaseHistory(dh);
        ensureAeCount(2);

        Map<String, InputFieldGroup> map = getTab().createFieldGroups(command);
        assertEquals("Wrong number of disease groups", 1, map.size());
        assertTrue("Map doesn't contain expected repeating group key. Actual keys: " +  map.keySet(),
            map.containsKey("disease0"));

        InputFieldGroup actualGroup0 = map.get("disease0");
        assertNotNull(actualGroup0);
        assertEquals(2, actualGroup0.getFields().size());
        assertEquals("attributionMap[disease][0][0]", actualGroup0.getFields().get(0).getPropertyName());
        assertEquals("attributionMap[disease][1][0]", actualGroup0.getFields().get(1).getPropertyName());
    }

    public void testSurgeryFieldsIncluded() throws Exception {
        command.getAeReport().getSurgeryIntervention().setDescription("Something");
        ensureAeCount(2);

        Map<String, InputFieldGroup> map = getTab().createFieldGroups(command);
        assertEquals("Wrong number of surgery groups", 1, map.size());

        InputFieldGroup actualGroup0 = map.get("surgery0");
        assertNotNull(actualGroup0);
        assertEquals(2, actualGroup0.getFields().size());
        assertEquals("attributionMap[surgery][0][0]", actualGroup0.getFields().get(0).getPropertyName());
        assertEquals("attributionMap[surgery][1][0]", actualGroup0.getFields().get(1).getPropertyName());
    }

    public void testRadiationFieldsIncluded() throws Exception {
        command.getAeReport().getRadiationIntervention().setDescription("Something");
        ensureAeCount(2);

        Map<String, InputFieldGroup> map = getTab().createFieldGroups(command);
        assertEquals("Wrong number of radiation groups", 1, map.size());

        InputFieldGroup actualGroup0 = map.get("radiation0");
        assertNotNull(actualGroup0);
        assertEquals(2, actualGroup0.getFields().size());
        assertEquals("attributionMap[radiation][0][0]", actualGroup0.getFields().get(0).getPropertyName());
        assertEquals("attributionMap[radiation][1][0]", actualGroup0.getFields().get(1).getPropertyName());
    }

    public void testNoDiseaseFieldsWhenDiseaseBlank() throws Exception {
        ensureAeCount(2);
        Map<String, InputFieldGroup> map = getTab().createFieldGroups(command);
        assertEquals("Wrong number of disease groups", 0, map.size());
    }

    public void testNoSurgeryFieldsWhenSurgeryBlank() throws Exception {
        ensureAeCount(2);
        Map<String, InputFieldGroup> map = getTab().createFieldGroups(command);
        assertEquals("Wrong number of surgery groups", 0, map.size());
    }

    public void testNoRadiationFieldsWhenRadiationBlank() throws Exception {
        ensureAeCount(2);
        Map<String, InputFieldGroup> map = getTab().createFieldGroups(command);
        assertEquals("Wrong number of radiation groups", 0, map.size());
    }

    public void testCreateStudyAgentBlock() throws Exception {
        ensureCourseAgentCount(1);
        ensureAeCount(2);

        AttributionTab.AttributionBlock block = getBlocks().get(1);
        assertEquals(1, block.getRows().size());
        assertEquals("Course", block.getDisplayName());
    }

    public void testCreateStudyAgentsBlock() throws Exception {
        ensureCourseAgentCount(2);
        ensureAeCount(2);

        AttributionTab.AttributionBlock block = getBlocks().get(1);
        assertEquals(2, block.getRows().size());
        assertEquals("Course", block.getDisplayName());
    }

    public void testCreateConMedBlock() throws Exception {
        ensureConMedCount(1);
        ensureAeCount(2);

        AttributionTab.AttributionBlock block = getBlocks().get(4);
        assertEquals(1, block.getRows().size());
        assertEquals("Concomitant medication", block.getDisplayName());
    }

    public void testCreateConMedsBlock() throws Exception {
        ensureConMedCount(2);
        ensureAeCount(2);

        AttributionTab.AttributionBlock block = getBlocks().get(4);
        assertEquals(2, block.getRows().size());
        assertEquals("Concomitant medications", block.getDisplayName());
    }

    @SuppressWarnings({ "unchecked" })
    private List<AttributionTab.AttributionBlock> getBlocks() {
        return (List<AttributionTab.AttributionBlock>) getTab()
            .referenceData(command).get("blocks");
    }

    private void ensureAeCount(int count) {
        while (command.getAeReport().getAdverseEvents().size() < count) {
            command.getAeReport().addAdverseEvent(new AdverseEvent());
        }
    }

    private void ensureCourseAgentCount(int count) {
        TreatmentInformation info = command.getAeReport().getTreatmentInformation();
        while (info.getCourseAgents().size() < count) {
            CourseAgent ca = new CourseAgent();
            info.addCourseAgent(ca);
        }
    }

    private void ensureConMedCount(int count) {
        while (command.getAeReport().getConcomitantMedications().size() < count) {
            command.getAeReport().addConcomitantMedication(new ConcomitantMedication());
        }
    }

    private void ensureOtherCauseCount(int count) {
        while (command.getAeReport().getOtherCauses().size() < count) {
            command.getAeReport().addOtherCause(new OtherCause());
        }
    }
}
