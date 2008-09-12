package gov.nih.nci.cabig.caaers.web.ae;

import static gov.nih.nci.cabig.caaers.CaaersUseCase.CREATE_EXPEDITED_REPORT;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.same;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nih.nci.cabig.caaers.CaaersUseCases;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.ReportPerson;
import gov.nih.nci.cabig.caaers.domain.ReportStatus;
import gov.nih.nci.cabig.caaers.domain.report.Report;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;
import gov.nih.nci.cabig.caaers.domain.report.ReportVersion;
import gov.nih.nci.cabig.caaers.domain.report.TimeScaleUnit;
import gov.nih.nci.cabig.caaers.service.EvaluationService;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.utils.Lov;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;

/**
 * @author Rhett Sutphin
 */
@CaaersUseCases( { CREATE_EXPEDITED_REPORT })
public class ReporterTabTest extends AeTabTestCase {
    private EvaluationService evaluationService;
    private ConfigProperty configurationProperty;

    private AdverseEvent ae0;

    @Override
    protected void setUp() throws Exception {
        evaluationService = registerMockFor(EvaluationService.class);
        configurationProperty = registerMockFor(ConfigProperty.class);
        Map<String, List<Lov>> map = new HashMap<String, List<Lov>>();
        map.put("titleType", new ArrayList<Lov>());
        expect(configurationProperty.getMap()).andReturn(map).anyTimes();
     
        super.setUp();
        ae0 = command.getAeReport().getAdverseEvents().get(0);
        assertEquals("Test setup failure -- only expected 1 AE initially", 1, command.getAeReport()
                        .getAdverseEvents().size());
    }

    @Override
    protected AeTab createTab() {
        ReporterTab tab = new ReporterTab();
        tab.setEvaluationService(evaluationService);
        tab.setConfigurationProperty(configurationProperty);
        return tab;
    }

    @Override
    protected void fillInUsedProperties(ExpeditedAdverseEventInputCommand cmd) {
        cmd.getAeReport().getReporter().getContactMechanisms().put("phone", "foo");
        cmd.getAeReport().getReporter().getContactMechanisms().put("fax", "foo");
        cmd.getAeReport().getPhysician().getContactMechanisms().put("phone", "foo");
        cmd.getAeReport().getPhysician().getContactMechanisms().put("fax", "foo");
    }

    public void testGroupsIncludesReporter() throws Exception {
    	replayMocks();
        InputFieldGroup actual = getTab().createFieldGroups(command).get("reporter");
        assertNotNull("No reporter group", actual);
        assertEquals("Wrong group name", "reporter", actual.getName());
        assertEquals("Wrong display name", "Reporter details", actual.getDisplayName());
    }

    public void testGroupsIncludesPhysician() throws Exception {
    	replayMocks();
        InputFieldGroup actual = getTab().createFieldGroups(command).get("physician");
        assertNotNull("No physician group", actual);
        assertEquals("Wrong group name", "physician", actual.getName());
        assertEquals("Wrong display name", "Physician details", actual.getDisplayName());
    }

    public void testReporterFieldProperties() throws Exception {
    	replayMocks();
        assertFieldProperties("reporter", "aeReport.reporter.firstName",
                        "aeReport.reporter.middleName", "aeReport.reporter.lastName",
                        "aeReport.reporter.contactMechanisms[e-mail]",
                        "aeReport.reporter.contactMechanisms[phone]",
                        "aeReport.reporter.contactMechanisms[fax]");
    }

    public void testPhysicianFieldProperties() throws Exception {
    	replayMocks();
        assertFieldProperties("physician", "aeReport.physician.firstName",
                        "aeReport.physician.middleName", "aeReport.physician.lastName",
                        "aeReport.physician.contactMechanisms[e-mail]",
                        "aeReport.physician.contactMechanisms[phone]",
                        "aeReport.physician.contactMechanisms[fax]");
    }

    public void testReporterFirstNameRequired() throws Exception {
        command.getAeReport().getReporter().setFirstName(null);
        doValidate();
        assertFieldRequiredErrorRaised("aeReport.reporter.firstName", "First name");
    }

    public void testReporterLastNameRequired() throws Exception {
        command.getAeReport().getReporter().setLastName(null);
        doValidate();
        assertFieldRequiredErrorRaised("aeReport.reporter.lastName", "Last name");
    }

    public void testReporterEmailAddressRequired() throws Exception {
        command.getAeReport().getReporter().getContactMechanisms().remove(ReportPerson.EMAIL);
        doValidate();
        assertFieldRequiredErrorRaised("aeReport.reporter.contactMechanisms[e-mail]",
                        "E-mail address");
    }

    public void testPhysicianFirstNameRequired() throws Exception {
        command.getAeReport().getPhysician().setFirstName(null);
        doValidate();
        assertFieldRequiredErrorRaised("aeReport.physician.firstName", "First name");
    }

    public void testPhysicianLastNameRequired() throws Exception {
        command.getAeReport().getPhysician().setLastName(null);
        doValidate();
        assertFieldRequiredErrorRaised("aeReport.physician.lastName", "Last name");
    }

    public void testPhysicianEmailAddressRequired() throws Exception {
        command.getAeReport().getPhysician().getContactMechanisms().remove(ReportPerson.EMAIL);
        doValidate();
        assertFieldRequiredErrorRaised("aeReport.physician.contactMechanisms[e-mail]",
                        "E-mail address");
    }

    public void testOnDisplayEvaluates() throws Exception {
        expect(evaluationService.isSevere(same(assignment), same(ae0))).andReturn(true);
        replayMocks();

        getTab().onDisplay(request, command);
        verifyMocks();

        assertEquals(Boolean.TRUE, request.getAttribute("oneOrMoreSevere"));
    }

    public void testOnDisplayWhenNotSevere() throws Exception {
        expect(evaluationService.isSevere(same(assignment), same(ae0))).andReturn(false);
        replayMocks();

        getTab().onDisplay(request, command);
        verifyMocks();

        assertEquals(Boolean.FALSE, request.getAttribute("oneOrMoreSevere"));
    }

    private AdverseEvent addAEToCommand() {
        AdverseEvent ae = new AdverseEvent();
        command.getAeReport().addAdverseEvent(ae);
        return ae;
    }

    public void testOnDisplayWithMultipleAEs() throws Exception {
        AdverseEvent ae1 = addAEToCommand();
        AdverseEvent ae2 = addAEToCommand();

        expect(evaluationService.isSevere(same(assignment), same(ae0))).andReturn(false);
        expect(evaluationService.isSevere(same(assignment), same(ae1))).andReturn(false);
        expect(evaluationService.isSevere(same(assignment), same(ae2))).andReturn(true);
        replayMocks();

        getTab().onDisplay(request, command);
        verifyMocks();

        assertEquals(Boolean.TRUE, request.getAttribute("oneOrMoreSevere"));
    }
    
    public void testInitilizeExistingReportMap() throws Exception{
    	addReportsToAeReport();
    	command.getAeReport().getReports().get(1).getLastVersion().setReportStatus(ReportStatus.COMPLETED);
    	command.getAeReport().getReports().get(3).getLastVersion().setReportStatus(ReportStatus.COMPLETED);
    	
    	Map<ReportDefinition, ReportStatus> map = ((ReporterTab)getTab()).initilizeExistingReportMap(command);
    	assertEquals(4, map.size());
    }
   
    public void addReportsToAeReport(){
    	for(int i = 0; i < 4; i++){
    		Report report = new Report();
    		report.setId(i);
    		ReportDefinition reportDefinition = new ReportDefinition();
    		reportDefinition.setAmendable(false);
    		reportDefinition.setExpedited(false);
    		reportDefinition.setName("repDefn " + i);
    		report.setReportDefinition(reportDefinition);
    		ReportVersion reportVersion= new ReportVersion();
    		reportVersion.setReportStatus(ReportStatus.PENDING);
    		report.addReportVersion(reportVersion);
    		command.getAeReport().addReport(report);
    	}
    }
}
