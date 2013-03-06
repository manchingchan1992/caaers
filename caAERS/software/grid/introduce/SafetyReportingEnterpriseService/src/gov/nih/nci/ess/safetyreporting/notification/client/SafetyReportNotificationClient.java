/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.ess.safetyreporting.notification.client;

import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

import org.oasis.wsrf.properties.GetResourcePropertyResponse;

import org.globus.gsi.GlobusCredential;

import gov.nih.nci.ess.safetyreporting.notification.stubs.SafetyReportNotificationPortType;
import gov.nih.nci.ess.safetyreporting.notification.stubs.service.SafetyReportNotificationServiceAddressingLocator;
import gov.nih.nci.ess.safetyreporting.notification.common.SafetyReportNotificationI;
import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;

/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.3
 */
public class SafetyReportNotificationClient extends SafetyReportNotificationClientBase implements SafetyReportNotificationI {	

	public SafetyReportNotificationClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public SafetyReportNotificationClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public SafetyReportNotificationClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public SafetyReportNotificationClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(SafetyReportNotificationClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  SafetyReportNotificationClient client = new SafetyReportNotificationClient(args[1]);
			  // place client calls here if you want to use this main as a
			  // test....
			} else {
				usage();
				System.exit(1);
			}
		} else {
			usage();
			System.exit(1);
		}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

  public org.oasis.wsrf.lifetime.DestroyResponse destroy(org.oasis.wsrf.lifetime.Destroy params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"destroy");
    return portType.destroy(params);
    }
  }

  public org.oasis.wsrf.lifetime.SetTerminationTimeResponse setTerminationTime(org.oasis.wsrf.lifetime.SetTerminationTime params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"setTerminationTime");
    return portType.setTerminationTime(params);
    }
  }

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportDefinitionNotification createSafetyReportDefinitionNotification(gov.nih.nci.ess.safetyreporting.types.SafetyReportDefinitionNotification safetyReportDefinitionNotification,ess.caaers.nci.nih.gov.Id reportDefinitionId) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"createSafetyReportDefinitionNotification");
    gov.nih.nci.ess.safetyreporting.notification.stubs.CreateSafetyReportDefinitionNotificationRequest params = new gov.nih.nci.ess.safetyreporting.notification.stubs.CreateSafetyReportDefinitionNotificationRequest();
    gov.nih.nci.ess.safetyreporting.notification.stubs.CreateSafetyReportDefinitionNotificationRequestSafetyReportDefinitionNotification safetyReportDefinitionNotificationContainer = new gov.nih.nci.ess.safetyreporting.notification.stubs.CreateSafetyReportDefinitionNotificationRequestSafetyReportDefinitionNotification();
    safetyReportDefinitionNotificationContainer.setSafetyReportDefinitionNotification(safetyReportDefinitionNotification);
    params.setSafetyReportDefinitionNotification(safetyReportDefinitionNotificationContainer);
    gov.nih.nci.ess.safetyreporting.notification.stubs.CreateSafetyReportDefinitionNotificationRequestReportDefinitionId reportDefinitionIdContainer = new gov.nih.nci.ess.safetyreporting.notification.stubs.CreateSafetyReportDefinitionNotificationRequestReportDefinitionId();
    reportDefinitionIdContainer.setId(reportDefinitionId);
    params.setReportDefinitionId(reportDefinitionIdContainer);
    gov.nih.nci.ess.safetyreporting.notification.stubs.CreateSafetyReportDefinitionNotificationResponse boxedResult = portType.createSafetyReportDefinitionNotification(params);
    return boxedResult.getSafetyReportDefinitionNotification();
    }
  }

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportDefinitionNotification updateSafetyReportDefinitionNotification(gov.nih.nci.ess.safetyreporting.types.SafetyReportDefinitionNotification safetyReportDefinitionNotification) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateSafetyReportDefinitionNotification");
    gov.nih.nci.ess.safetyreporting.notification.stubs.UpdateSafetyReportDefinitionNotificationRequest params = new gov.nih.nci.ess.safetyreporting.notification.stubs.UpdateSafetyReportDefinitionNotificationRequest();
    gov.nih.nci.ess.safetyreporting.notification.stubs.UpdateSafetyReportDefinitionNotificationRequestSafetyReportDefinitionNotification safetyReportDefinitionNotificationContainer = new gov.nih.nci.ess.safetyreporting.notification.stubs.UpdateSafetyReportDefinitionNotificationRequestSafetyReportDefinitionNotification();
    safetyReportDefinitionNotificationContainer.setSafetyReportDefinitionNotification(safetyReportDefinitionNotification);
    params.setSafetyReportDefinitionNotification(safetyReportDefinitionNotificationContainer);
    gov.nih.nci.ess.safetyreporting.notification.stubs.UpdateSafetyReportDefinitionNotificationResponse boxedResult = portType.updateSafetyReportDefinitionNotification(params);
    return boxedResult.getSafetyReportDefinitionNotification();
    }
  }

  public gov.nih.nci.ess.safetyreporting.types.SafetyReportDefinitionNotification deactivateSafetyReportDefinitionNotification(ess.caaers.nci.nih.gov.Id notificationId,_21090.org.iso.ST reasonForDeactivation) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"deactivateSafetyReportDefinitionNotification");
    gov.nih.nci.ess.safetyreporting.notification.stubs.DeactivateSafetyReportDefinitionNotificationRequest params = new gov.nih.nci.ess.safetyreporting.notification.stubs.DeactivateSafetyReportDefinitionNotificationRequest();
    gov.nih.nci.ess.safetyreporting.notification.stubs.DeactivateSafetyReportDefinitionNotificationRequestNotificationId notificationIdContainer = new gov.nih.nci.ess.safetyreporting.notification.stubs.DeactivateSafetyReportDefinitionNotificationRequestNotificationId();
    notificationIdContainer.setId(notificationId);
    params.setNotificationId(notificationIdContainer);
    gov.nih.nci.ess.safetyreporting.notification.stubs.DeactivateSafetyReportDefinitionNotificationRequestReasonForDeactivation reasonForDeactivationContainer = new gov.nih.nci.ess.safetyreporting.notification.stubs.DeactivateSafetyReportDefinitionNotificationRequestReasonForDeactivation();
    reasonForDeactivationContainer.setST(reasonForDeactivation);
    params.setReasonForDeactivation(reasonForDeactivationContainer);
    gov.nih.nci.ess.safetyreporting.notification.stubs.DeactivateSafetyReportDefinitionNotificationResponse boxedResult = portType.deactivateSafetyReportDefinitionNotification(params);
    return boxedResult.getSafetyReportDefinitionNotification();
    }
  }

}
