/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import static gov.nih.nci.cabig.caaers.domain.Grade.DEATH;
import static gov.nih.nci.cabig.caaers.domain.Grade.LIFE_THREATENING;
import static gov.nih.nci.cabig.caaers.domain.Grade.MILD;
import static gov.nih.nci.cabig.caaers.domain.Grade.MODERATE;
import static gov.nih.nci.cabig.caaers.domain.Grade.SEVERE;
import static gov.nih.nci.cabig.caaers.domain.Grade.NORMAL;
import static gov.nih.nci.cabig.caaers.domain.Grade.getByCode;
import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 * @author Biju Joseph
 */
public class GradeTest extends TestCase {
    public void testToString() throws Exception {
        assertEquals("5: Death related to AE.", DEATH.toString());
        assertEquals("4: Life-threatening consequences; urgent intervention indicated.", LIFE_THREATENING.toString());
        assertEquals("3: Severe or medically significant but not immediately life-threatening; hospitalization or prolongation of hospitalization indicated; disabling; limiting self care ADL.", SEVERE.toString());
        assertEquals("2: Moderate; minimal, local or noninvasive intervention indicated; limiting age-appropriate instrumental ADL.", MODERATE.toString());
        assertEquals("1: Mild; asymptomatic or mild symptoms; clinical or diagnostic observations only; intervention not indicated.", MILD.toString());
        assertEquals("0: Normal", NORMAL.toString());
    }

    public void testFromCode() throws Exception {
        assertEquals(DEATH, getByCode(5));
        assertEquals(LIFE_THREATENING, getByCode(4));
        assertEquals(SEVERE, getByCode(3));
        assertEquals(MODERATE, getByCode(2));
        assertEquals(MILD, getByCode(1));
        assertNotNull(Grade.getByCode(0));
        assertEquals(Grade.NOT_EVALUATED, Grade.getByCode(-1));
        
    }
   
}
