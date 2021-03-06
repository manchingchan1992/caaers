/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.study;

import static gov.nih.nci.cabig.caaers.CaaersUseCase.CREATE_STUDY;
import gov.nih.nci.cabig.caaers.CaaersUseCases;
import gov.nih.nci.cabig.caaers.dao.ConditionDao;
import gov.nih.nci.cabig.caaers.dao.CtcDao;
import gov.nih.nci.cabig.caaers.dao.CtcTermDao;
import gov.nih.nci.cabig.caaers.dao.DiseaseTermDao;
import gov.nih.nci.cabig.caaers.dao.EpochDao;
import gov.nih.nci.cabig.caaers.dao.InvestigationalNewDrugDao;
import gov.nih.nci.cabig.caaers.dao.MeddraVersionDao;
import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.meddra.LowLevelTermDao;
import gov.nih.nci.cabig.caaers.domain.LocalStudy;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.web.WebTestCase;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * @author Ion C. Olaru
 */
@CaaersUseCases({CREATE_STUDY})
public abstract class AbstractStudyWebTestCase extends WebTestCase {

    private static final Log log = LogFactory.getLog(AbstractStudyWebTestCase.class);
    protected StudyTab tab;
    protected Errors errors;
    protected StudyCommand command;
    protected Study study;
    ConfigProperty configurationProperty;

    protected CtcDao ctcDao;
    protected MeddraVersionDao meddraVersionDao;
    protected DiseaseTermDao diseaseTermDao;
    protected LowLevelTermDao lowLevelTermDao;
    protected CtcTermDao ctcTermDao;
    protected ConditionDao conditionDao;
    protected EpochDao epochDao;
    protected StudyDao studyDao;
    protected InvestigationalNewDrugDao investigationalNewDrugDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        ctcDao = registerDaoMockFor(CtcDao.class);
        meddraVersionDao = registerDaoMockFor(MeddraVersionDao.class);
        diseaseTermDao = registerDaoMockFor(DiseaseTermDao.class);
        lowLevelTermDao = registerDaoMockFor(LowLevelTermDao.class);
        conditionDao = registerDaoMockFor(ConditionDao.class);
        epochDao = registerDaoMockFor(EpochDao.class);
        studyDao = registerDaoMockFor(StudyDao.class);
        investigationalNewDrugDao = registerDaoMockFor(InvestigationalNewDrugDao.class);

        tab = createTab();

        command = createCommand();
        study = new LocalStudy();
        command.setStudy(study);
        errors = new BindException(command, "command");

    }

    protected StudyCommand createCommand(){
    	 StudyCommand command = new StudyCommand(studyDao, investigationalNewDrugDao);
         Study study = new LocalStudy();
         command.setStudy(study);

         return command;
    }
    protected abstract StudyTab createTab();

    public StudyTab getTab() {
        return tab;
    }

    protected InputFieldGroup getFieldGroup(String fieldGroupName) {
        return getTab().createFieldGroups(command).get(fieldGroupName);
    }

    public Errors getErrors() {
        return errors;
    }


}
