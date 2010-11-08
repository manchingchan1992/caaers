
package gov.nih.nci.cabig.caaers.accesscontrol.query.impl;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.cabig.caaers.domain.UserGroupType;
import gov.nih.nci.cabig.caaers.domain.index.IndexEntry;

import com.semanticbits.security.contentfilter.IdFetcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Will find the investigators a logged-in person has access to.
 *
 * Assumptions:-
 * =============
 *
 *  The roles requiring investigator Indexing are :-
 * Site Scoped Roles:
 *      UserGroupType.person_and_organization_information_manager,
 *      UserGroupType.user_administrator,
 *      UserGroupType.study_team_administrator
 *
 * Study Scoped Roles:
        UserGroupType.data_reader,
        UserGroupType.data_analyst
 *
 * The OrganizationIndex is available at the time for the given login-Id
 *
 *
 *
 * @author Srini Akkala
 * @author Biju Joseph
 *
 */
public class CaaersInvestigatorIdFetcherImpl extends AbstractIdFetcher implements IdFetcher {
     protected final Log log = LogFactory.getLog(CaaersInvestigatorIdFetcherImpl.class);
    //the query
    private final String siteScopedSQL;
    private final String studyScopedSQL;


    public CaaersInvestigatorIdFetcherImpl(){


        //site scoped roles - can access all the researchstaff of their site.
       StringBuilder siteSQL = new StringBuilder("select distinct sr.investigator_id as id from site_investigators  sr ")
            .append("join organization_index oi on  oi.organization_id = sr.site_id ")
            .append("where oi.login_id = :LOGIN_ID  ")
            .append("and oi.role_code = :ROLE_CODE ");
       siteScopedSQL = siteSQL.toString();

        //study scoped roles - can access all research staff on their study and site
        StringBuilder studySQL = new StringBuilder("select distinct sr.investigator_id as id from site_investigators  sr ")
            .append("join study_organizations so on sr.site_id = so.site_id ")
            .append("join study_index si on so.study_id = si.study_id ")
            .append("join organization_index oi on  oi.organization_id = sr.site_id  ")
            .append("where si.login_id = :LOGIN_ID ")
            .append("and si.role_code = :ROLE_CODE ")
            .append("and si.role_code = oi.role_code ")
            .append("and si.login_id = oi.login_id ");
        studyScopedSQL = studySQL.toString();


    }

    
    /**
     * Will return the Site scoped HQL query
     *
     * @return
     */
    @Override
    public String getSiteScopedSQL() {
        return siteScopedSQL;
    }

    /**
     * Will return the Study scoped HQL query
     *
     * @return
     */
    @Override
    public String getStudyScopedSQL() {
        return studyScopedSQL;
    }

	    
}
