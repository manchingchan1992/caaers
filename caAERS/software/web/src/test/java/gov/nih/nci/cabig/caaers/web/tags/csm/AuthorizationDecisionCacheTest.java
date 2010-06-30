package gov.nih.nci.cabig.caaers.web.tags.csm;

import gov.nih.nci.cabig.caaers.AbstractTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * AuthorizationDecisionCache Tester.
 *
 * @author Biju Joseph
 * @since <pre>06/30/2010</pre>
 * 
 */
public class AuthorizationDecisionCacheTest extends AbstractTestCase {
    AuthorizationDecisionCache cache;
    public void setUp() throws Exception {
       super.setUp();
       cache = new AuthorizationDecisionCache();
    }

    public void testGetCacheKeyDiscriminator() throws Exception {
        assertEquals("0", cache.getCacheKeyDiscriminator());
    }


    public void testIsAuthorized(){
      assertNull( cache.isAuthorized("hi", "hello") );
    }

     public void testIsAuthorizedAfterAdd(){
      assertNull( cache.isAuthorized("hi", "hello") );
      cache.addDecision("hi", "hello", true);
      assertTrue(cache.isAuthorized("hi", "hello"));
    }

    public void testAddDecision(){
      
      cache.addDecision("hi", "hello", true);
      cache.addDecision("hi","man", false);
      
      assertTrue(cache.isAuthorized("hi", "hello"));
      assertFalse(cache.isAuthorized("hi", "man"));
      assertNull(cache.isAuthorized("hi","boy"));
    }
}
