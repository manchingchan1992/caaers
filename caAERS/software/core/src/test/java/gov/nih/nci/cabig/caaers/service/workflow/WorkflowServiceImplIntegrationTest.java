/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.workflow;

import gov.nih.nci.cabig.caaers.CaaersDbTestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Does the integration test on workflow service. 
 * 
 * @author Biju Joseph
 *
 */
public class WorkflowServiceImplIntegrationTest extends CaaersDbTestCase {
	
	
	WorkflowServiceImpl wfService;
	Map<String, Object > variables = new HashMap<String, Object>();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		wfService = (WorkflowServiceImpl)getDeployedApplicationContext().getBean("workflowService");
		
	}

	public void testCreateProcessInstance(){
		
	}
	/*
	
    //creation of a process instance is tested here. 
	public void testCreateProcessInstance() {
		ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EVALUATION_PERIOD_COORDINATING_CENTER, variables);
		assertNotNull(pInstance);
		assertEquals( "Submit Reporting Period for Data Coordinator Review" ,pInstance.getRootToken().getNode().getName());
	}

    //creating and testing whether the task instances are created
	public void testFetchTaskInstances() {
		ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EVALUATION_PERIOD_COORDINATING_CENTER, variables);
		assertNotNull(pInstance);
		assertEquals( "Submit Reporting Period for Data Coordinator Review" ,pInstance.getRootToken().getNode().getName());
		List<TaskInstance>tasks = wfService.fetchTaskInstances("bush@def.com");
		assertNotNull(tasks);
		assertFalse(tasks.isEmpty());
		assertEquals("Submit Reporting Period for Data Coordinator Review",tasks.get(0).getName());
	}

    //test creation and feting the process and tasks correctly
	public void testFetchProcessInstance() {
		Long id = null;
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EVALUATION_PERIOD_COORDINATING_CENTER, variables);
			assertNotNull(pInstance);
			id = pInstance.getId();
		}
		interruptSession();
		{
			ProcessInstance loadedInstance = wfService.fetchProcessInstance(id);
			assertNotNull(loadedInstance);
		}
	}


    //makes sure that the CRA login see only the transitions designated to him.
	public void testNextTransitions_SiteCRA() {
		Integer id = null;
		String loginId = "pc@def.com";
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EVALUATION_PERIOD_COORDINATING_CENTER, variables);
			Long l = pInstance.getId();
			id = new Integer(l.intValue());
		}
		interruptSession();
		{
			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
			assertFalse(nextTransitions.isEmpty());
			assertEquals(1, nextTransitions.size());
			assertEquals("Approve Reporting Period", nextTransitions.get(0).getName());
			assertEquals("Approved", nextTransitions.get(0).getTo().getName());
		}
	}
	
	//makes sure that the DC see only the designated transitions. 
	public void testNextTransitions_DataCoordinator() {
		Integer id = null;
		String loginId = "aec@def.com";
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EVALUATION_PERIOD_COORDINATING_CENTER, variables);
			Long l = pInstance.getId();
			id = new Integer(l.intValue());
		}
		interruptSession();
		{
 			List<Transition> nextTransitions = wfService.nextTransitions(id, loginId);
			assertNotNull(nextTransitions);
            System.out.println(nextTransitions);
			assertTrue(nextTransitions.isEmpty());
		}
	}


    //when the workflow is terminated, makessure that the opent tasks are terminated properly. 
	public void testAdvanceWorkflow_CloseAllOpenTaskInstances() {
		Long id = null;
		{
			List<TaskInstance>tasks = wfService.fetchTaskInstances("bush@def.com");
			assertTrue(tasks.isEmpty());
		}
		
		{
			ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EVALUATION_PERIOD_COORDINATING_CENTER, variables);
			assertNotNull(pInstance);
			id = pInstance.getId();
			assertEquals( "Submit Reporting Period for Data Coordinator Review" ,pInstance.getRootToken().getNode().getName());
			List<TaskInstance>tasks = wfService.fetchTaskInstances("bush@def.com");
			assertNotNull(tasks);
			assertFalse(tasks.isEmpty());
			assertEquals("Submit Reporting Period for Data Coordinator Review",tasks.get(0).getName());
		}
		interruptSession();
		String nextTransition ="Approve Reporting Period";
		{
			ReviewStatus status = wfService.advanceWorkflow(id.intValue(), nextTransition);
			assertEquals(ReviewStatus.APPROVED, status);
			List<TaskInstance> taskInstances = wfService.fetchTaskInstances("datacoordinator@abc.com");
			assertNotNull(taskInstances);
			assertTrue(taskInstances.isEmpty());
		}
		interruptSession();
		{
			List<TaskInstance>tasks = wfService.fetchTaskInstances("bush@def.com");
			assertTrue(tasks.isEmpty());
		}
		
	}

    
    //retrieves the assignees of the tasks
	public void testFindTaskAssignees() {
		ProcessInstance pInstance  = wfService.createProcessInstance(WorkflowService.WORKFLOW_EVALUATION_PERIOD_COORDINATING_CENTER, variables);
		assertNotNull(pInstance);
		assertEquals( "Submit Reporting Period for Data Coordinator Review" ,pInstance.getRootToken().getNode().getName());
		
		List<User> assignees = wfService.findTaskAssignees(pInstance, "Submit Reporting Period for Data Coordinator Review");
		assertNotNull(assignees);
		assertEquals(2, assignees.size());
		assertEquals("abc@def.com", assignees.get(0).getLoginId());
		assertEquals("bush@def.com", assignees.get(1).getLoginId());
	}

*/
}
