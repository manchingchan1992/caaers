package gov.nih.nci.cabig.caaers.service.migrator;

import gov.nih.nci.cabig.caaers.AbstractTestCase;
import gov.nih.nci.cabig.caaers.domain.Fixtures;
import gov.nih.nci.cabig.caaers.domain.LocalStudy;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyTherapyType;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;

public class StudyTherapyMigratorTest extends AbstractTestCase {
	
	StudyTherapyMigrator migrator;
	Study xstreamStudy;
	DomainObjectImportOutcome<Study> outcome;
	Study dest;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println(getClass().getClassLoader());
		dest = new LocalStudy();
		xstreamStudy = Fixtures.createStudy("short title");
		outcome = new DomainObjectImportOutcome<Study>();
		migrator = new StudyTherapyMigrator();
	}
	
	
	public void testMigrate() {
		xstreamStudy.addStudyTherapy(StudyTherapyType.DRUG_ADMINISTRATION);
		xstreamStudy.addStudyTherapy(StudyTherapyType.RADIATION);
		
		migrator.migrate(xstreamStudy, dest, outcome);
		assertEquals(2, dest.getStudyTherapies().size());
		assertTrue("No errors should be there", outcome.getMessages().isEmpty());

	}
	
	
	public void testMigrateWithNoTherapy() {
		migrator.migrate(xstreamStudy, dest, outcome);
		assertEquals(0, dest.getStudyTherapies().size());
		assertTrue("No errors should be there", outcome.getMessages().isEmpty());

	}

}
