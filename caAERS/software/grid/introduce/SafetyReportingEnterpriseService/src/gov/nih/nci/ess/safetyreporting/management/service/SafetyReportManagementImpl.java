package gov.nih.nci.ess.safetyreporting.management.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class SafetyReportManagementImpl extends SafetyReportManagementImplBase {

	
	public SafetyReportManagementImpl() throws RemoteException {
		super();
	}
	
  public gov.nih.nci.ess.safetyreporting.types.SafetyReportVersion initiateSafetyReport(ess.caaers.nci.nih.gov.Id studyId,ess.caaers.nci.nih.gov.Id subjectId,ess.caaers.nci.nih.gov.Id patientId,_21090.org.iso.DSET_II adverseEventIds,_21090.org.iso.DSET_II problemIds,gov.nih.nci.ess.safetyreporting.types.AdverseEventReportingPeriod adverseEventReportingPeriod) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    //TODO: Implement this autogenerated method
    throw new RemoteException("Not yet implemented");
  }

}

