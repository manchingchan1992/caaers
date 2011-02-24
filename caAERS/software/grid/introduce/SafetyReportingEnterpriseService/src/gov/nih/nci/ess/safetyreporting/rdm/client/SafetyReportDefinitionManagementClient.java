package gov.nih.nci.ess.safetyreporting.rdm.client;

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

import gov.nih.nci.ess.safetyreporting.rdm.stubs.SafetyReportDefinitionManagementPortType;
import gov.nih.nci.ess.safetyreporting.rdm.stubs.service.SafetyReportDefinitionManagementServiceAddressingLocator;
import gov.nih.nci.ess.safetyreporting.rdm.common.SafetyReportDefinitionManagementI;
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
public class SafetyReportDefinitionManagementClient extends SafetyReportDefinitionManagementClientBase implements SafetyReportDefinitionManagementI {	

	public SafetyReportDefinitionManagementClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public SafetyReportDefinitionManagementClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public SafetyReportDefinitionManagementClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public SafetyReportDefinitionManagementClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(SafetyReportDefinitionManagementClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  SafetyReportDefinitionManagementClient client = new SafetyReportDefinitionManagementClient(args[1]);
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

  public void createSafetyReportDefinition(gov.nih.nci.ess.safetyreporting.types.ReportDefinition reportDefinition) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"createSafetyReportDefinition");
    gov.nih.nci.ess.safetyreporting.rdm.stubs.CreateSafetyReportDefinitionRequest params = new gov.nih.nci.ess.safetyreporting.rdm.stubs.CreateSafetyReportDefinitionRequest();
    gov.nih.nci.ess.safetyreporting.rdm.stubs.CreateSafetyReportDefinitionRequestReportDefinition reportDefinitionContainer = new gov.nih.nci.ess.safetyreporting.rdm.stubs.CreateSafetyReportDefinitionRequestReportDefinition();
    reportDefinitionContainer.setReportDefinition(reportDefinition);
    params.setReportDefinition(reportDefinitionContainer);
    gov.nih.nci.ess.safetyreporting.rdm.stubs.CreateSafetyReportDefinitionResponse boxedResult = portType.createSafetyReportDefinition(params);
    }
  }

  public void updateSafetyReportDefinitionDetails(gov.nih.nci.ess.safetyreporting.types.ReportDefinition reportDefinition) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateSafetyReportDefinitionDetails");
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDetailsRequest params = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDetailsRequest();
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDetailsRequestReportDefinition reportDefinitionContainer = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDetailsRequestReportDefinition();
    reportDefinitionContainer.setReportDefinition(reportDefinition);
    params.setReportDefinition(reportDefinitionContainer);
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDetailsResponse boxedResult = portType.updateSafetyReportDefinitionDetails(params);
    }
  }

  public void updateSafetyReportDefinitionDeliveryDetails(gov.nih.nci.ess.safetyreporting.types.ReportDefinition reportDefinition) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateSafetyReportDefinitionDeliveryDetails");
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDeliveryDetailsRequest params = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDeliveryDetailsRequest();
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDeliveryDetailsRequestReportDefinition reportDefinitionContainer = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDeliveryDetailsRequestReportDefinition();
    reportDefinitionContainer.setReportDefinition(reportDefinition);
    params.setReportDefinition(reportDefinitionContainer);
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionDeliveryDetailsResponse boxedResult = portType.updateSafetyReportDefinitionDeliveryDetails(params);
    }
  }

  public void updateSafetyReportDefinitionMandatoryFields(gov.nih.nci.ess.safetyreporting.types.ReportDefinition reportDefinition) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateSafetyReportDefinitionMandatoryFields");
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionMandatoryFieldsRequest params = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionMandatoryFieldsRequest();
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionMandatoryFieldsRequestReportDefinition reportDefinitionContainer = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionMandatoryFieldsRequestReportDefinition();
    reportDefinitionContainer.setReportDefinition(reportDefinition);
    params.setReportDefinition(reportDefinitionContainer);
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportDefinitionMandatoryFieldsResponse boxedResult = portType.updateSafetyReportDefinitionMandatoryFields(params);
    }
  }

  public void deactivateSafetyReportDefinition(ess.caaers.nci.nih.gov.Id reportDefinitionId,_21090.org.iso.ST reasonForDeactivation) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"deactivateSafetyReportDefinition");
    gov.nih.nci.ess.safetyreporting.rdm.stubs.DeactivateSafetyReportDefinitionRequest params = new gov.nih.nci.ess.safetyreporting.rdm.stubs.DeactivateSafetyReportDefinitionRequest();
    gov.nih.nci.ess.safetyreporting.rdm.stubs.DeactivateSafetyReportDefinitionRequestReportDefinitionId reportDefinitionIdContainer = new gov.nih.nci.ess.safetyreporting.rdm.stubs.DeactivateSafetyReportDefinitionRequestReportDefinitionId();
    reportDefinitionIdContainer.setId(reportDefinitionId);
    params.setReportDefinitionId(reportDefinitionIdContainer);
    gov.nih.nci.ess.safetyreporting.rdm.stubs.DeactivateSafetyReportDefinitionRequestReasonForDeactivation reasonForDeactivationContainer = new gov.nih.nci.ess.safetyreporting.rdm.stubs.DeactivateSafetyReportDefinitionRequestReasonForDeactivation();
    reasonForDeactivationContainer.setST(reasonForDeactivation);
    params.setReasonForDeactivation(reasonForDeactivationContainer);
    gov.nih.nci.ess.safetyreporting.rdm.stubs.DeactivateSafetyReportDefinitionResponse boxedResult = portType.deactivateSafetyReportDefinition(params);
    }
  }

  public void updateSafetyReportTerminologyForStudy(ess.caaers.nci.nih.gov.Id reportDefinitionId,ess.caaers.nci.nih.gov.Id studyId,ess.caaers.nci.nih.gov.Id reportTerminologyId) throws RemoteException, gov.nih.nci.ess.safetyreporting.management.stubs.types.SafetyReportingServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateSafetyReportTerminologyForStudy");
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyRequest params = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyRequest();
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyRequestReportDefinitionId reportDefinitionIdContainer = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyRequestReportDefinitionId();
    reportDefinitionIdContainer.setId(reportDefinitionId);
    params.setReportDefinitionId(reportDefinitionIdContainer);
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyRequestStudyId studyIdContainer = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyRequestStudyId();
    studyIdContainer.setId(studyId);
    params.setStudyId(studyIdContainer);
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyRequestReportTerminologyId reportTerminologyIdContainer = new gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyRequestReportTerminologyId();
    reportTerminologyIdContainer.setId(reportTerminologyId);
    params.setReportTerminologyId(reportTerminologyIdContainer);
    gov.nih.nci.ess.safetyreporting.rdm.stubs.UpdateSafetyReportTerminologyForStudyResponse boxedResult = portType.updateSafetyReportTerminologyForStudy(params);
    }
  }

}
