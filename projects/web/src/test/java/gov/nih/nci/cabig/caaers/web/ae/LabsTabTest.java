package gov.nih.nci.cabig.caaers.web.ae;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class LabsTabTest extends AeTabTestCase<LabsTab> {
    @Override protected LabsTab createTab() { return new LabsTab(); }

    public void testGroupDisplayNames() throws Exception {
        assertDisplayNameForFieldGroup("Lab A", "lab0");
        assertDisplayNameForFieldGroup("Lab G", "lab6");
    }
    
    public void testFieldProperties() throws Exception {
        assertFieldProperties("lab3",
            "aeReport.labs[3].name",
            "aeReport.labs[3].units",
            "aeReport.labs[3].baseline.value",
            "aeReport.labs[3].baseline.date",
            "aeReport.labs[3].nadir.value",
            "aeReport.labs[3].nadir.date",
            "aeReport.labs[3].recovery.value",
            "aeReport.labs[3].recovery.date"
        );
    }
}
