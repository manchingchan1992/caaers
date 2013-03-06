/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao;

import gov.nih.nci.cabig.caaers.DaoTestCase;
import gov.nih.nci.cabig.caaers.domain.LabCategory;

import java.util.List;

public class LabCategoryDaoTest extends DaoTestCase<LabCategoryDao> {

	
	public void testGetDomainClass(){
		Object obj = getDao().domainClass();
		assertNotNull(obj);
	}
	
	public void testGetAll(){
		List<LabCategory> labCats = getDao().getAll();
		assertNotNull(labCats);
		assertEquals(3,labCats.size());
	}
}
