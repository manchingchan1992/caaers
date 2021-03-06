/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.ess.ae.service.management.client;

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

import gov.nih.nci.ess.ae.service.management.stubs.ManagementPortType;
import gov.nih.nci.ess.ae.service.management.stubs.service.ManagementServiceAddressingLocator;
import gov.nih.nci.ess.ae.service.management.common.ManagementI;
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
public class ManagementClient extends ManagementClientBase implements ManagementI {	

	public ManagementClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public ManagementClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public ManagementClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public ManagementClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(ManagementClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  ManagementClient client = new ManagementClient(args[1]);
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

  public ess.caaers.nci.nih.gov.AdverseEvent initiateAdverseEvent(ess.caaers.nci.nih.gov.Id subjectIdentifier,ess.caaers.nci.nih.gov.Id studyIdentifier,ess.caaers.nci.nih.gov.AdverseEvent adverseEvent,ess.caaers.nci.nih.gov.TsDateTime courseStartDate) throws RemoteException, gov.nih.nci.ess.ae.service.management.stubs.types.AdverseEventServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"initiateAdverseEvent");
    gov.nih.nci.ess.ae.service.management.stubs.InitiateAdverseEventRequest params = new gov.nih.nci.ess.ae.service.management.stubs.InitiateAdverseEventRequest();
    gov.nih.nci.ess.ae.service.management.stubs.InitiateAdverseEventRequestSubjectIdentifier subjectIdentifierContainer = new gov.nih.nci.ess.ae.service.management.stubs.InitiateAdverseEventRequestSubjectIdentifier();
    subjectIdentifierContainer.setId(subjectIdentifier);
    params.setSubjectIdentifier(subjectIdentifierContainer);
    gov.nih.nci.ess.ae.service.management.stubs.InitiateAdverseEventRequestStudyIdentifier studyIdentifierContainer = new gov.nih.nci.ess.ae.service.management.stubs.InitiateAdverseEventRequestStudyIdentifier();
    studyIdentifierContainer.setId(studyIdentifier);
    params.setStudyIdentifier(studyIdentifierContainer);
    gov.nih.nci.ess.ae.service.management.stubs.InitiateAdverseEventRequestAdverseEvent adverseEventContainer = new gov.nih.nci.ess.ae.service.management.stubs.InitiateAdverseEventRequestAdverseEvent();
    adverseEventContainer.setAdverseEvent(adverseEvent);
    params.setAdverseEvent(adverseEventContainer);
    gov.nih.nci.ess.ae.service.management.stubs.InitiateAdverseEventRequestCourseStartDate courseStartDateContainer = new gov.nih.nci.ess.ae.service.management.stubs.InitiateAdverseEventRequestCourseStartDate();
    courseStartDateContainer.setTsDateTime(courseStartDate);
    params.setCourseStartDate(courseStartDateContainer);
    gov.nih.nci.ess.ae.service.management.stubs.InitiateAdverseEventResponse boxedResult = portType.initiateAdverseEvent(params);
    return boxedResult.getAdverseEvent();
    }
  }

  public ess.caaers.nci.nih.gov.AdverseEvent updateAdverseEvent(ess.caaers.nci.nih.gov.AdverseEvent adverseEvent) throws RemoteException, gov.nih.nci.ess.ae.service.management.stubs.types.AdverseEventServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"updateAdverseEvent");
    gov.nih.nci.ess.ae.service.management.stubs.UpdateAdverseEventRequest params = new gov.nih.nci.ess.ae.service.management.stubs.UpdateAdverseEventRequest();
    gov.nih.nci.ess.ae.service.management.stubs.UpdateAdverseEventRequestAdverseEvent adverseEventContainer = new gov.nih.nci.ess.ae.service.management.stubs.UpdateAdverseEventRequestAdverseEvent();
    adverseEventContainer.setAdverseEvent(adverseEvent);
    params.setAdverseEvent(adverseEventContainer);
    gov.nih.nci.ess.ae.service.management.stubs.UpdateAdverseEventResponse boxedResult = portType.updateAdverseEvent(params);
    return boxedResult.getAdverseEvent();
    }
  }

  public ess.caaers.nci.nih.gov.AdverseEvent deactivateAdverseEvent(ess.caaers.nci.nih.gov.Id adverseEventIdentifier) throws RemoteException, gov.nih.nci.ess.ae.service.management.stubs.types.AdverseEventServiceException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"deactivateAdverseEvent");
    gov.nih.nci.ess.ae.service.management.stubs.DeactivateAdverseEventRequest params = new gov.nih.nci.ess.ae.service.management.stubs.DeactivateAdverseEventRequest();
    gov.nih.nci.ess.ae.service.management.stubs.DeactivateAdverseEventRequestAdverseEventIdentifier adverseEventIdentifierContainer = new gov.nih.nci.ess.ae.service.management.stubs.DeactivateAdverseEventRequestAdverseEventIdentifier();
    adverseEventIdentifierContainer.setId(adverseEventIdentifier);
    params.setAdverseEventIdentifier(adverseEventIdentifierContainer);
    gov.nih.nci.ess.ae.service.management.stubs.DeactivateAdverseEventResponse boxedResult = portType.deactivateAdverseEvent(params);
    return boxedResult.getAdverseEvent();
    }
  }

}
