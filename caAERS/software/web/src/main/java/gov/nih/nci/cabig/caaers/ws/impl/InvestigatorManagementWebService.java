/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.ws.impl;

import gov.nih.nci.cabig.caaers.ws.InvestigatorService;
import gov.nih.nci.cabig.caaers.api.impl.DefaultInvestigatorMigratorService;
import gov.nih.nci.cabig.caaers.integration.schema.common.CaaersServiceResponse;
import gov.nih.nci.cabig.caaers.integration.schema.investigator.Staff;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Exposes the web service to manage Investigators. Will delegate the request to DefaultInvestigatorMigratorService.
 * @author  Biju Joseph
 * @author  Ion C. Olaru
 */

@WebService(endpointInterface="gov.nih.nci.cabig.caaers.ws.InvestigatorService", serviceName="InvestigatorService", targetNamespace="http://schema.integration.caaers.cabig.nci.nih.gov/investigator")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class InvestigatorManagementWebService implements InvestigatorService {
    private DefaultInvestigatorMigratorService impl;

    @WebMethod
    public CaaersServiceResponse saveInvestigator(@WebParam(name="Staff", targetNamespace="http://schema.integration.caaers.cabig.nci.nih.gov/investigator") Staff staff) {
        return  impl.saveInvestigator(staff);
    }

    public DefaultInvestigatorMigratorService getImpl() {
        return impl;
    }

    public void setImpl(DefaultInvestigatorMigratorService impl) {
        this.impl = impl;
    }
}
