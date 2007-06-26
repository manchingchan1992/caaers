package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.ctms.domain.DomainObject;
import gov.nih.nci.cabig.caaers.domain.report.ReportDefinition;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * @author Rhett Sutphin
 */
public class Fixtures {
    public static <T extends DomainObject> T setId(int id, T target) {
        target.setId(id);
        return target;
    }

    public static Participant createParticipant(String first, String last) {
        Participant p = new Participant();
        p.setFirstName(first);
        p.setLastName(last);
        return p;
    }

    public static Study createStudy(String shortTitle) {
        Study s = new Study();
        s.setShortTitle(shortTitle);
        return s;
    }

    public static Site createSite(String name) {
        Site site = new Site();
        site.setName(name);
        return site;
    }

    /** Creates an assignment and the associated Study, Participant, StudySite, and Site objs */
    public static StudyParticipantAssignment createAssignment() {
        return assignParticipant(
            createParticipant("D", "C"),
            createStudy("DC"),
            createSite("N/A")
        );
    }

    public static StudyParticipantAssignment assignParticipant(Participant p, Study study, Site site) {
        StudySite ss = new StudySite();
        ss.setSite(site);
        study.addStudySite(ss);
        site.addStudySite(ss);

        StudyParticipantAssignment assignment = new StudyParticipantAssignment();
        ss.addAssignment(assignment);
        p.addAssignment(assignment);

        return assignment;
    }

    public static StudyAgent createStudyAgent(String agentName) {
        StudyAgent agent = new StudyAgent();
        agent.setAgent(new Agent());
        agent.getAgent().setName(agentName);
        return agent;
    }

    public static ExpeditedAdverseEventReport createSaveableExpeditedReport() {
        ExpeditedAdverseEventReport report = new ExpeditedAdverseEventReport();
        report.setCreatedAt(new Timestamp(103));
        report.setReporter(new Reporter());
        report.getReporter().setFirstName("Frank");
        report.getReporter().setLastName("Just Frank");
        report.setPhysician(new Physician());
        report.getPhysician().setFirstName("Frank");
        report.getPhysician().setLastName("Just Frank");
        return report;
    }

    public static ReportDefinition createReportDefinition(String name) {
        ReportDefinition def = new ReportDefinition();
        def.setName(name);
        return def;
    }

    public static Ctc createCtcaeV3() {
        Ctc v3 = setId(3, new Ctc());
        // this is only partial, of course
        v3.setCategories(new ArrayList<CtcCategory>());
        v3.getCategories().add(createCtcCategory(v3, "ALLERGY/IMMUNOLOGY"));
        v3.getCategories().add(createCtcCategory(v3, "AUDITORY/EAR"));
        v3.getCategories().add(createCtcCategory(v3, "BLOOD/BONE MARROW"));
        return v3;
    }

    private static CtcCategory createCtcCategory(Ctc ctc, String name) {
        CtcCategory category = new CtcCategory();
        category.setName(name);
        category.setCtc(ctc);
        return category;
    }
}
