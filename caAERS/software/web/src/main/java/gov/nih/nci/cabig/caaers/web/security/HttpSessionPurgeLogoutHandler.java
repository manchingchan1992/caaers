package gov.nih.nci.cabig.caaers.web.security;

import gov.nih.nci.cabig.caaers.web.tags.csm.AuthorizationDecisionCache;
import org.acegisecurity.Authentication;
import org.acegisecurity.ui.logout.LogoutHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Map;

/**
 *
 * This class is added to address the following:-
 *  <li>Tomcat is not properly invalidating the HttpSession.</li>
 *  <li>Log out and re-login to the application shows the incorrect tabs due to incorrect Authentication object in session</li>
 * @author Biju Joseph
 *
 */
public class HttpSessionPurgeLogoutHandler implements LogoutHandler, ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(HttpSessionPurgeLogoutHandler.class);
    private ApplicationContext applicationContext;

    /**
     * This method will obtain the session and will explicitly set all the attributes to NULL and then remove it from session.
     *
     * @param request
     * @param response
     * @param authentication
     */
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try{

            HttpSession session = request.getSession(false);
            
		    if (session != null) {

                String sessionId = session.getId();
                Map map = applicationContext.getBeansOfType(AuthorizationDecisionCache.class,true,false);
                if(map != null){
                    for(Object cacheBean : map.values()){
                        ((AuthorizationDecisionCache)cacheBean).clear(sessionId);
                    }
                }
			    session.invalidate();
		    }


            for(Cookie c : request.getCookies()){
                c.setMaxAge(0);
                response.addCookie(c);
            }

        }catch(Exception e){
            //ignore
           if(logger.isDebugEnabled()) logger.debug("Ignore this error",e);
        }

    }


    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
