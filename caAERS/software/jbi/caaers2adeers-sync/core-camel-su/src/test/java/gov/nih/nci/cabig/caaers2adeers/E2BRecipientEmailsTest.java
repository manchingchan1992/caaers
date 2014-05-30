/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers2adeers;

public class E2BRecipientEmailsTest extends SafetyReportE2BXslTest {

	public void testBaselinePerformanceTransformation() throws Exception{

		String result = null;
		result = transform("xslt/e2b/request/testXMLs/E2B_RTOG-1106.xml", "xslt/e2b/request/safetyreport_e2b_sync.xsl");
		assertNotNull(result);
		assertNotSame("", result);
		assertTrue(result.contains("<ae:ccEmails>vasudev.chatti@semanticbits.com,vinodh.rc@semanticbits.com, ramakrishna.gundala1@semanticbits.com1</ae:ccEmails>"));

	}
	
}
