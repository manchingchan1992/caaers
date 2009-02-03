package gov.nih.nci.cabig.caaers.domain.workflow;

import org.apache.commons.lang.StringUtils;

import junit.framework.TestCase;

public class ReviewCommentTest extends TestCase {
	ReviewComment comment;
	protected void setUp() throws Exception {
		super.setUp();
		comment = new ReportingPeriodReviewComment();
	}

	public void testGetFullComment() {
		assertTrue(StringUtils.isEmpty(comment.getFullComment()));
		comment.setAutoGeneratedText("Hello");
		assertEquals("Hello", comment.getFullComment());
		comment.setAutoGeneratedText(null);
		assertTrue(StringUtils.isEmpty(comment.getFullComment()));
		comment.setUserComment("World");
		System.out.println(comment.getFullComment());
		assertEquals(" : World", comment.getFullComment());
		
		comment.setAutoGeneratedText("Hello");
		assertEquals("Hello : World", comment.getFullComment());
	}

}
