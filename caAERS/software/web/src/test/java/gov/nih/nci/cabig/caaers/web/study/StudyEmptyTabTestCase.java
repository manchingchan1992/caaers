/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.study;

import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;

import java.util.Map;

/**
 * @author Ion C. Olaru
 */

public class StudyEmptyTabTestCase extends AbstractStudyWebTestCase {

   

    protected StudyTab createTab() {
        EmptyStudyTab tab = new EmptyStudyTab("", "", "");
        tab.setConfigurationProperty(new ConfigProperty());
        return tab;
    }

    public void testReferenceData() {
        replayMocks();
        Map<String, Object> output = tab.referenceData(request, command);
        verifyMocks();

        assertNotNull(output);
        assertNotNull(output.get("listOfSolicitedAERows"));
    }

    
}
