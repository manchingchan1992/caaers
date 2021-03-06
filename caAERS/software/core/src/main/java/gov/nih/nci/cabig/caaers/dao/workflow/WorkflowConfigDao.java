/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao.workflow;

import gov.nih.nci.cabig.caaers.dao.GridIdentifiableDao;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.workflow.WorkflowConfig;
import gov.nih.nci.cabig.ctms.dao.MutableDomainObjectDao;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * This class implements the Data access related operations for the WorkflowConfig domain object.
 * 
 * @author Sameer Sawant
 */
@Transactional(readOnly=true)
public class WorkflowConfigDao extends GridIdentifiableDao<WorkflowConfig>
implements MutableDomainObjectDao<WorkflowConfig>{	
	/**
     * Get the Class representation of the domain object that this DAO is representing.
     * 
     * @return Class representation of the domain object that this DAO is representing.
     */
	 @Transactional(readOnly = true, propagation= Propagation.NOT_SUPPORTED)
    public Class<WorkflowConfig> domainClass() {
        return WorkflowConfig.class;
    }
    
    /**
     * Get the list of WorkflowConfigs based on the workflowDefinitionName.
     */
    @SuppressWarnings("unchecked")
	public  WorkflowConfig getByWorkflowDefinitionName(String workflowDefinitionName) {
        List<WorkflowConfig> results = getHibernateTemplate().find("from WorkflowConfig where workflowDefinitionName= ?", workflowDefinitionName);
        return (results == null || results.size() < 1) ? null : results.get(0);
    }
    
    /**
     * Get the list of all workflow configs.
     *
     * @return return the list of workflow configs.
     */
    @SuppressWarnings("unchecked")
    public List<WorkflowConfig> getAllWorkflowConfigs() {
        return getHibernateTemplate().find("from WorkflowConfig");
    }

}
