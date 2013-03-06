/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.repository;

import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;


/*
* @author Ion C. Olaru
*
* */
public class ExpeditedAdverseEventReportRepository {

    private ExpeditedAdverseEventReportDao aeReportDao;

    public ExpeditedAdverseEventReportDao getAeReportDao() {
        return aeReportDao;
    }

    public void setAeReportDao(ExpeditedAdverseEventReportDao aeReportDao) {
        this.aeReportDao = aeReportDao;
    }
}
