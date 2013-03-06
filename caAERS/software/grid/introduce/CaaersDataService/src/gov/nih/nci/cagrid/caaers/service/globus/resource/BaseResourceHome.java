/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cagrid.caaers.service.globus.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.impl.SingletonResourceHome;
import org.globus.wsrf.jndi.Initializable;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceContextException;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements the resource home for the resource type represented
 * by this service.
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class BaseResourceHome extends SingletonResourceHome implements Initializable {

	static final Log logger = LogFactory.getLog(BaseResourceHome.class);


	public Resource findSingleton() {
		logger.info("Creating a single resource.");
		try {
			CaaersDataServiceResource resource = new CaaersDataServiceResource();
			resource.initialize();
			return resource;
		} catch (Exception e) {
			logger.error("Exception when creating the resource: " + e);
			return null;
		}
	}
	
	public CaaersDataServiceResource getResource(){
		CaaersDataServiceResource serviceBaseResource;
		try {
			serviceBaseResource = (CaaersDataServiceResource)ResourceContext.getResourceContext().getResource();
		} catch (ResourceContextException e) {
			return null;
		} catch (ResourceException e) {
			return null;
		}
		return serviceBaseResource;
	}


	public Resource find(ResourceKey key) throws ResourceException {
		CaaersDataServiceResource resource = (CaaersDataServiceResource) super.find(key);
		// each time the resource is looked up, do a lazy refreash of
		// registration.
		resource.refreshRegistration(false);
		return resource;
	}


	/**
	 * Initialze the singleton resource, when the home is initialized.
	 */
	public void initialize() throws Exception {
		logger.info("Attempting to initialize resource.");
		Resource resource = find(null);
		if (resource == null) {
			logger.error("Unable to initialize resource!");
		} else {
			logger.info("Successfully initialized resource.");
		}
	}
	
    /**
     * Get the resouce that is being addressed in this current context
     */
    public CaaersDataServiceResource getAddressedResource() throws Exception {
        CaaersDataServiceResource thisResource;
        thisResource = (CaaersDataServiceResource) ResourceContext.getResourceContext().getResource();
        return thisResource;
    }
}
