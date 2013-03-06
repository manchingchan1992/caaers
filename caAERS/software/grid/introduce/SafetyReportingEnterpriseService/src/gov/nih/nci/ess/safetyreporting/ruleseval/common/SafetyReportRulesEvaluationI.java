/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.ess.safetyreporting.ruleseval.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public interface SafetyReportRulesEvaluationI {

  public gov.nih.nci.ess.safetyreporting.types.DSET_ReportDefinition evaluateAgainstSafetyReportingRules(ess.caaers.nci.nih.gov.Id adverseEventId,ess.caaers.nci.nih.gov.Id problemId,ess.caaers.nci.nih.gov.Id studyId,ess.caaers.nci.nih.gov.Id organizationId) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException ;

  public org.oasis.wsrf.lifetime.DestroyResponse destroy(org.oasis.wsrf.lifetime.Destroy params) throws RemoteException ;

  public org.oasis.wsrf.lifetime.SetTerminationTimeResponse setTerminationTime(org.oasis.wsrf.lifetime.SetTerminationTime params) throws RemoteException ;

}

