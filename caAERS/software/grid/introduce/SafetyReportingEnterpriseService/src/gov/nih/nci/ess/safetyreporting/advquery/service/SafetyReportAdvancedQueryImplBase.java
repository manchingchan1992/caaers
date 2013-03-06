/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.ess.safetyreporting.advquery.service;

import gov.nih.nci.ess.safetyreporting.advquery.service.globus.resource.SafetyReportAdvancedQueryResource;
import  gov.nih.nci.ess.safetyreporting.service.SafetyReportingEnterpriseServiceConfiguration;

import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceContextException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceHome;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.ResourcePropertySet;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * Provides some simple accessors for the Impl.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public abstract class SafetyReportAdvancedQueryImplBase {
	
	public SafetyReportAdvancedQueryImplBase() throws RemoteException {
	
	}
	
	public SafetyReportingEnterpriseServiceConfiguration getConfiguration() throws Exception {
		return SafetyReportingEnterpriseServiceConfiguration.getConfiguration();
	}
	
	
	public gov.nih.nci.ess.safetyreporting.advquery.service.globus.resource.SafetyReportAdvancedQueryResourceHome getResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("home");
		return (gov.nih.nci.ess.safetyreporting.advquery.service.globus.resource.SafetyReportAdvancedQueryResourceHome)resource;
	}

	
	
	
	public gov.nih.nci.ess.safetyreporting.service.globus.resource.SafetyReportingEnterpriseServiceResourceHome getSafetyReportingEnterpriseServiceResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("safetyReportingEnterpriseServiceHome");
		return (gov.nih.nci.ess.safetyreporting.service.globus.resource.SafetyReportingEnterpriseServiceResourceHome)resource;
	}
	
	public gov.nih.nci.ess.safetyreporting.management.service.globus.resource.SafetyReportManagementResourceHome getSafetyReportManagementResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("safetyReportManagementHome");
		return (gov.nih.nci.ess.safetyreporting.management.service.globus.resource.SafetyReportManagementResourceHome)resource;
	}
	
	public gov.nih.nci.ess.safetyreporting.qry.service.globus.resource.SafetyReportQueryResourceHome getSafetyReportQueryResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("safetyReportQueryHome");
		return (gov.nih.nci.ess.safetyreporting.qry.service.globus.resource.SafetyReportQueryResourceHome)resource;
	}
	
	public gov.nih.nci.ess.safetyreporting.tx.service.globus.resource.SafetyReportTransactionResourceHome getSafetyReportTransactionResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("safetyReportTransactionHome");
		return (gov.nih.nci.ess.safetyreporting.tx.service.globus.resource.SafetyReportTransactionResourceHome)resource;
	}
	
	public gov.nih.nci.ess.safetyreporting.rdm.service.globus.resource.SafetyReportDefinitionManagementResourceHome getSafetyReportDefinitionManagementResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("safetyReportDefinitionManagementHome");
		return (gov.nih.nci.ess.safetyreporting.rdm.service.globus.resource.SafetyReportDefinitionManagementResourceHome)resource;
	}
	
	public gov.nih.nci.ess.safetyreporting.rules.service.globus.resource.SafetyReportRulesManagementResourceHome getSafetyReportRulesManagementResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("safetyReportRulesManagementHome");
		return (gov.nih.nci.ess.safetyreporting.rules.service.globus.resource.SafetyReportRulesManagementResourceHome)resource;
	}
	
	public gov.nih.nci.ess.safetyreporting.notification.service.globus.resource.SafetyReportNotificationResourceHome getSafetyReportNotificationResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("safetyReportNotificationHome");
		return (gov.nih.nci.ess.safetyreporting.notification.service.globus.resource.SafetyReportNotificationResourceHome)resource;
	}
	
	public gov.nih.nci.ess.safetyreporting.review.service.globus.resource.SafetyReportReviewResourceHome getSafetyReportReviewResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("safetyReportReviewHome");
		return (gov.nih.nci.ess.safetyreporting.review.service.globus.resource.SafetyReportReviewResourceHome)resource;
	}
	
	public gov.nih.nci.ess.safetyreporting.ruleseval.service.globus.resource.SafetyReportRulesEvaluationResourceHome getSafetyReportRulesEvaluationResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("safetyReportRulesEvaluationHome");
		return (gov.nih.nci.ess.safetyreporting.ruleseval.service.globus.resource.SafetyReportRulesEvaluationResourceHome)resource;
	}
	
	public gov.nih.nci.ess.safetyreporting.rdquery.service.globus.resource.SafetyReportDefinitionQueryResourceHome getSafetyReportDefinitionQueryResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("safetyReportDefinitionQueryHome");
		return (gov.nih.nci.ess.safetyreporting.rdquery.service.globus.resource.SafetyReportDefinitionQueryResourceHome)resource;
	}
	
	
	protected ResourceHome getResourceHome(String resourceKey) throws Exception {
		MessageContext ctx = MessageContext.getCurrentContext();

		ResourceHome resourceHome = null;
		
		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/" + resourceKey;
		try {
			javax.naming.Context initialContext = new InitialContext();
			resourceHome = (ResourceHome) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate resource home. : " + resourceKey, e);
		}

		return resourceHome;
	}


}

