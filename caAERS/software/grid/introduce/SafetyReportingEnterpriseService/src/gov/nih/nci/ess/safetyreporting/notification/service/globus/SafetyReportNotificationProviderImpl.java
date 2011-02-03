package gov.nih.nci.ess.safetyreporting.notification.service.globus;

import gov.nih.nci.ess.safetyreporting.notification.service.SafetyReportNotificationImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the SafetyReportingEnterpriseServiceImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class SafetyReportNotificationProviderImpl{
	
	SafetyReportNotificationImpl impl;
	
	public SafetyReportNotificationProviderImpl() throws RemoteException {
		impl = new SafetyReportNotificationImpl();
	}
	

    public gov.nih.nci.ess.safetyreporting.notification.stubs.CreateSafetyReportDefinitionNotificationResponse createSafetyReportDefinitionNotification(gov.nih.nci.ess.safetyreporting.notification.stubs.CreateSafetyReportDefinitionNotificationRequest params) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    gov.nih.nci.ess.safetyreporting.notification.stubs.CreateSafetyReportDefinitionNotificationResponse boxedResult = new gov.nih.nci.ess.safetyreporting.notification.stubs.CreateSafetyReportDefinitionNotificationResponse();
    boxedResult.setSafetyReportDefinitionNotification(impl.createSafetyReportDefinitionNotification(params.getSafetyReportDefinitionNotification().getSafetyReportDefinitionNotification(),params.getReportDefinitionId().getId()));
    return boxedResult;
  }

    public gov.nih.nci.ess.safetyreporting.notification.stubs.UpdateSafetyReportDefinitionNotificationResponse updateSafetyReportDefinitionNotification(gov.nih.nci.ess.safetyreporting.notification.stubs.UpdateSafetyReportDefinitionNotificationRequest params) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    gov.nih.nci.ess.safetyreporting.notification.stubs.UpdateSafetyReportDefinitionNotificationResponse boxedResult = new gov.nih.nci.ess.safetyreporting.notification.stubs.UpdateSafetyReportDefinitionNotificationResponse();
    boxedResult.setSafetyReportDefinitionNotification(impl.updateSafetyReportDefinitionNotification(params.getSafetyReportDefinitionNotification().getSafetyReportDefinitionNotification()));
    return boxedResult;
  }

    public gov.nih.nci.ess.safetyreporting.notification.stubs.DeactivateSafetyReportDefinitionNotificationResponse deactivateSafetyReportDefinitionNotification(gov.nih.nci.ess.safetyreporting.notification.stubs.DeactivateSafetyReportDefinitionNotificationRequest params) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    gov.nih.nci.ess.safetyreporting.notification.stubs.DeactivateSafetyReportDefinitionNotificationResponse boxedResult = new gov.nih.nci.ess.safetyreporting.notification.stubs.DeactivateSafetyReportDefinitionNotificationResponse();
    boxedResult.setSafetyReportDefinitionNotification(impl.deactivateSafetyReportDefinitionNotification(params.getNotificationId().getId(),params.getReasonForDeactivation().getST()));
    return boxedResult;
  }

}