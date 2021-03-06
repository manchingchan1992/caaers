/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.ae;

import static gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory.createPastDateField;
import static gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory.createSelectField;
import static gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory.createTextField;
import static gov.nih.nci.cabig.caaers.web.utils.WebUtils.collectOptions;
import gov.nih.nci.cabig.caaers.dao.ExpeditedAdverseEventReportDao;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.expeditedfields.ExpeditedReportSection;
import gov.nih.nci.cabig.caaers.domain.repository.ConfigPropertyRepositoryImpl;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.validation.fields.validators.FieldValidator;
import gov.nih.nci.cabig.caaers.web.fields.CompositeField;
import gov.nih.nci.cabig.caaers.web.fields.DefaultInputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputField;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldAttributes;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldFactory;
import gov.nih.nci.cabig.caaers.web.utils.WebUtils;
import gov.nih.nci.cabig.ctms.domain.DomainObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Ion C. Olaru
 */

public class StudyInterventionsTab extends AeTab {
    private static final Log log = LogFactory.getLog(StudyInterventionsTab.class);
    protected ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao;

    Map<String, String> methodNameMap = new HashMap<String, String>();
    private ConfigProperty configurationProperty;
    ConfigPropertyRepositoryImpl configPropertyRepositoryImpl;

    private static final String STUDY_INTERVENTION_SURGERY = "surgery";
    private static final String STUDY_INTERVENTION_DEVICE = "device";
    private static final String STUDY_INTERVENTION_RADIATION = "radiation";
    private static final String STUDY_INTERVENTION_AGENT = "agent";
    private static final String STUDY_INTERVENTION_BEHAVIORAL = "behavioral";
    private static final String STUDY_INTERVENTION_BIOLOGICAL = "biological";
    private static final String STUDY_INTERVENTION_GENETIC = "genetic";
    private static final String STUDY_INTERVENTION_DIETARY = "dietary";
    private static final String STUDY_INTERVENTION_OTHERAE= "otherAE";

    public StudyInterventionsTab() {
        super("Study Interventions", ExpeditedReportSection.STUDY_INTERVENTIONS.getDisplayName(), "ae/studyInterventions");
        methodNameMap.put("add" + STUDY_INTERVENTION_SURGERY, "addSurgery");
        methodNameMap.put("remove" + STUDY_INTERVENTION_SURGERY, "removeSurgery");        
        methodNameMap.put("add" + STUDY_INTERVENTION_RADIATION, "addRadiation");
        methodNameMap.put("remove" + STUDY_INTERVENTION_RADIATION, "removeRadiation");        
        methodNameMap.put("add" + STUDY_INTERVENTION_DEVICE, "addDevice");
        methodNameMap.put("remove" + STUDY_INTERVENTION_DEVICE, "removeDevice");        
        methodNameMap.put("add" + STUDY_INTERVENTION_AGENT, "addAgent");
        methodNameMap.put("remove" + STUDY_INTERVENTION_AGENT, "removeAgent");        
        methodNameMap.put("add" + STUDY_INTERVENTION_BEHAVIORAL, "addBehavioral");
        methodNameMap.put("remove" + STUDY_INTERVENTION_BEHAVIORAL, "removeBehavioral");
        methodNameMap.put("add" + STUDY_INTERVENTION_BIOLOGICAL, "addBiological");
        methodNameMap.put("remove" + STUDY_INTERVENTION_BIOLOGICAL, "removeBiological");
        methodNameMap.put("add" + STUDY_INTERVENTION_GENETIC, "addGenetic");
        methodNameMap.put("remove" + STUDY_INTERVENTION_GENETIC, "removeGenetic");
        methodNameMap.put("add" + STUDY_INTERVENTION_DIETARY, "addDietary");
        methodNameMap.put("remove" + STUDY_INTERVENTION_DIETARY, "removeDietary");
        methodNameMap.put("add" + STUDY_INTERVENTION_OTHERAE, "addOtherAE");
        methodNameMap.put("remove" + STUDY_INTERVENTION_OTHERAE, "removeOtherAE");
    }
    
    @Override
    public Map<String, Object> referenceData(HttpServletRequest request,ExpeditedAdverseEventInputCommand command) {
    	Map<String, Object> refData =  super.referenceData(request, command);
    	refData.put("agentMandatorySection", command.isSectionMandatory(ExpeditedReportSection.AGENTS_INTERVENTION_SECTION));
    	refData.put("radiationMandatorySection", command.isSectionMandatory(ExpeditedReportSection.RADIATION_INTERVENTION_SECTION));
    	refData.put("surgeryMandatorySection", command.isSectionMandatory(ExpeditedReportSection.SURGERY_INTERVENTION_SECTION));
    	refData.put("deviceMandatorySection", command.isSectionMandatory(ExpeditedReportSection.MEDICAL_DEVICE_SECTION));
        //initializing some stuff
        command.getAeReport().getAdverseEventAttributions();
        return refData;
    }
    
    

    private void createRadiationFieldGroups(AeInputFieldCreator creator, ExpeditedAdverseEventInputCommand command){

        //fields for studyRadiation
        InputField studyRadiationField = InputFieldFactory.createSelectField("studyRadiation", "Study radiation", false, WebUtils.collectOptions(command.getStudy().getActiveStudyRadiations(), "id", "name", "Please select"));

        //fields for Radiation
        Map<Object, Object> statusOpts = new LinkedHashMap<Object, Object>();
        statusOpts.put("", "Please select");
        statusOpts.putAll(collectOptions(Arrays.asList(RadiationAdministration.values()), null, "displayName"));
        InputField doseUOMField = InputFieldFactory.createSelectField("dosageUnit", "Unit of measure", false, WebUtils.sortMapByKey(WebUtils.collectOptions(configurationProperty.getMap().get("radiationDoseUMORefData"), "code", "desc", "Please select"), true));
        InputField fractionNumberField = createTextField("fractionNumber", "Schedule number of fractions", false);
        fractionNumberField.getAttributes().put(InputField.HELP, "ae.radiationIntervention.aeReport.radiationInterventions.fractionNumber");

        creator.createRepeatingFieldGroup("radiationIntervention", "radiationInterventions", new SimpleNumericDisplayNameCreator("Radiation"),
                createSelectField("administration", "Type of radiation administration", true, statusOpts),
                createTextField("dosage", "Total dose (to date)", FieldValidator.SIGN_VALIDATOR, 
                		FieldValidator.createPatternBasedValidator("[0-9]{1,14}([.][0-9]{1,6})?", "DECIMAL")),
                doseUOMField,
                createPastDateField("lastTreatmentDate", "Date of last treatment", false),
                fractionNumberField,
                createTextField("daysElapsed", " Number of elapsed days", false),
                createSelectField("adjustment", "Adjustment", false, WebUtils.collectOptions(configurationProperty.getMap().get("radiationAdjustmentRefData"), "code","desc", "Please select")),
                studyRadiationField
        );
    }

    private void createSurgeryFieldGroups(AeInputFieldCreator creator, ExpeditedAdverseEventInputCommand command){

        //fields for study surgery
        InputField studySurgeriesField = InputFieldFactory.createSelectField("studySurgery", "Study surgery", true, WebUtils.collectOptions(command.getStudy().getActiveStudySurgeries(), "id", "name", "Please select"));

        //surgery fields
        String code = command.getAeReport().getTreatmentInformation().getTreatmentAssignment() != null ? command .getAeReport().getTreatmentInformation().getTreatmentAssignment().getCode() : null;
        String description = code != null ? command.getAeReport().getTreatmentInformation().getTreatmentAssignmentDescription() : command.getAeReport().getTreatmentInformation().getTreatmentDescription();
        InputField descField = InputFieldFactory.createTextArea("description", "Treatment arm description", false);
        InputFieldAttributes.setColumns(descField, 45);
        InputFieldAttributes.setDetails(descField, description);
        InputField codeField = createTextField("treatmentArm", "Treatment arm", false);
        InputFieldAttributes.setDetails(codeField, code);
        creator.createRepeatingFieldGroup("surgeryIntervention", "surgeryInterventions", new SimpleNumericDisplayNameCreator("Surgery"),
                codeField,
                descField,
                InputFieldFactory.createAutocompleterField("interventionSite", "Intervention site", true),
                InputFieldFactory.createPastDateField("interventionDate", "Date of intervention", false),
                studySurgeriesField);
    }

    private void createOtherInterventionsFieldGroups(AeInputFieldCreator creator, ExpeditedAdverseEventInputCommand command) {
        InputField studyBehavioralsField = InputFieldFactory.createSelectField("studyIntervention", "Study behavioral", true, WebUtils.collectOptions(command.getStudy().getActiveStudyBehavioralInterventions(), "id", "name", "Please select"));
        creator.createRepeatingFieldGroup("behavioralIntervention", "behavioralInterventions", new SimpleNumericDisplayNameCreator("Behavioral"), studyBehavioralsField);

        InputField studyBiologicalsField = InputFieldFactory.createSelectField("studyIntervention", "Study biological", true, WebUtils.collectOptions(command.getStudy().getActiveStudyBiologicalInterventions(), "id", "name", "Please select"));
        creator.createRepeatingFieldGroup("biologicalIntervention", "biologicalInterventions", new SimpleNumericDisplayNameCreator("Biological"), studyBiologicalsField);

        InputField studyGeneticsField = InputFieldFactory.createSelectField("studyIntervention", "Study genetic", true, WebUtils.collectOptions(command.getStudy().getActiveStudyGeneticInterventions(), "id", "name", "Please select"));
        creator.createRepeatingFieldGroup("geneticIntervention", "geneticInterventions", new SimpleNumericDisplayNameCreator("Genetic"), studyGeneticsField);

        InputField studyDietaryField = InputFieldFactory.createSelectField("studyIntervention", "Study dietary", true, WebUtils.collectOptions(command.getStudy().getActiveStudyDietaryInterventions(), "id", "name", "Please select"));
        creator.createRepeatingFieldGroup("dietaryIntervention", "dietaryInterventions", new SimpleNumericDisplayNameCreator("Dietary"), studyDietaryField);

        InputField studyOtherInterventionsField = InputFieldFactory.createSelectField("studyIntervention", "Study other", true, WebUtils.collectOptions(command.getStudy().getActiveStudyOtherInterventions(), "id", "name", "Please select"));
        creator.createRepeatingFieldGroup("otherAEIntervention", "otherAEInterventions", new SimpleNumericDisplayNameCreator("Other"), studyOtherInterventionsField);
    }

    private void createAgentFieldGroups(AeInputFieldCreator creator, ExpeditedAdverseEventInputCommand command){
          InputField agentField = InputFieldFactory.createSelectField("studyAgent", "Study agent", false, WebUtils.collectOptions(command.getStudy().getActiveStudyAgents(), "id", "agentName", "Please select"));

        InputField totalDoseField = InputFieldFactory.createTextField("dose.amount", "Total dose administered this course", 
        		FieldValidator.SIGN_VALIDATOR, FieldValidator.createPatternBasedValidator("[0-9]{1,14}([.][0-9]{1,6})?", "DECIMAL"));

        InputField totalUOMField = InputFieldFactory.createSelectField("dose.units","Unit of measure", false, WebUtils.sortMapByKey(WebUtils.collectOptions(configPropertyRepositoryImpl.getByType(ConfigPropertyType.AGENT_UOM),"code", "name", "Please select"), true));
        CompositeField adminDelayField = new CompositeField(null, new DefaultInputFieldGroup(null,"Administration delay").addField(InputFieldFactory.createTextField("administrationDelayAmount", "", false)).addField(InputFieldFactory.createSelectField("administrationDelayUnits", "", false,WebUtils.collectOptions(Arrays.asList(DelayUnits.values()), null, "displayName"))));
        InputField commentsField = InputFieldFactory.createTextArea("comments", "Comments", false);
        InputFieldAttributes.setColumns(commentsField, 70);
        InputFieldAttributes.setRows(commentsField, 4);
        //InputField modifiedDoseField = createDoseField("modifiedDose", "Modified dose", false, true);
        InputField modifiedDoseField = InputFieldFactory.createSelectField("agentAdjustment", "Dose Modification?", false, WebUtils.collectOptions(Arrays.asList(AgentAdjustment.values()), null, "displayName","Please select"));
       // modifiedDoseField.getAttributes().put(InputField.HELP,"ae.treatment.aeReport.treatmentInformation.courseAgents.modifiedDose");
        InputField investigationalAgentAdministeredField = InputFieldFactory.createSelectField("treatmentInformation.investigationalAgentAdministered", "Was an investigational agent administered on this protocol?" , false, createInvestigationalAgentAdministeredOptions());
        investigationalAgentAdministeredField.getAttributes().put(InputField.HELP, "ae.treatment.aeReport.treatmentInformation.investigationalAgentAdministered");
        creator.createFieldGroup("agentAdministered", investigationalAgentAdministeredField);
        
        creator.createRepeatingFieldGroup("courseAgent", "treatmentInformation.courseAgents",
                new SimpleNumericDisplayNameCreator("Study Agent"), agentField, InputFieldFactory.createTextField("formulation", "Formulation"),
                InputFieldFactory.createTextField("lotNumber", "Lot # (if known)"),
                totalDoseField,
                totalUOMField,
                InputFieldFactory.createPastDateField("firstAdministeredDate", "Date first administered", false),
                InputFieldFactory.createPastDateField("lastAdministeredDate", "Date last administered", false),
                adminDelayField,
                commentsField,
                modifiedDoseField);
    }

    private void createDeviceFieldGroups(AeInputFieldCreator creator, ExpeditedAdverseEventInputCommand command){
        //fields for study device
        InputField studyDeviceField = InputFieldFactory.createSelectField("studyDevice", "Study device", true, WebUtils.collectOptions(command.getStudy().getActiveStudyDevices(), "id", "displayName", "Please select"));
        //fields for medical device
        InputField brandName = InputFieldFactory.createTextField("brandName", "Brand name", false);
        InputFieldAttributes.setSize(brandName, 45);
        InputField commonName = InputFieldFactory.createTextField("commonName", "Common name", false);
        InputFieldAttributes.setSize(commonName, 45);
        InputField deviceType = InputFieldFactory.createTextField("deviceType", "Device type", false);
        InputFieldAttributes.setSize(deviceType, 45);
        InputField manName = InputFieldFactory.createTextField("manufacturerName", "Manufacturer name", false);
        InputFieldAttributes.setSize(manName, 45);
        InputField manCity = InputFieldFactory.createTextField("manufacturerCity", "Manufacturer city", false);
        InputFieldAttributes.setSize(manCity, 45);
        InputField manState = InputFieldFactory.createSelectField("manufacturerState", "Manufacturer state", false, WebUtils.collectOptions(configurationProperty.getMap().get("stateRefData"), "code", "desc", "Please select"));
        InputFieldAttributes.setSize(manState, 45);
        InputField modelNumber = InputFieldFactory.createTextField("modelNumber", "Model number", false);
        InputField otherDeviceOperator = InputFieldFactory.createTextField("otherDeviceOperator", "Other device operator", false);
        InputFieldAttributes.setSize(otherDeviceOperator, 45);
        InputField reprocessorName = InputFieldFactory.createTextField("reprocessorName", " Reprocessor name", false);
        InputFieldAttributes.setSize(reprocessorName, 45);
        reprocessorName.getAttributes().put(InputField.HELP, "ae.medicalDevice.aeReport.medicalDevices.reprocessorName");
        InputField reprocessorAddress = InputFieldFactory.createTextField("reprocessorAddress", " Reprocessor address", false);
        InputFieldAttributes.setSize(reprocessorAddress, 45);
        InputField deviceReprocessedField = InputFieldFactory.createSelectField("deviceReprocessed", "Device reprocessed", false, WebUtils.collectOptions(Arrays.asList(ReprocessedDevice.values()), null, "displayName", "Please select"));
        deviceReprocessedField.getAttributes().put(InputField.HELP, "ae.medicalDevice.aeReport.medicalDevices.deviceReprocessed");
        InputField evaluationAvailabilityField = InputFieldFactory.createSelectField("evaluationAvailability", "Evaluation availability", false, WebUtils.collectOptions(Arrays.asList(Availability.values()), null, "displayName", "Please select"));
        evaluationAvailabilityField.getAttributes().put(InputField.HELP, "ae.medicalDevice.aeReport.medicalDevices.evaluationAvailability");

        creator.createRepeatingFieldGroup("medicalDevice", "medicalDevices", new SimpleNumericDisplayNameCreator("Medical device"),
                        brandName,
                        commonName,
                        deviceType,
                        manName,
                        manCity,
                        manState,
                        modelNumber,
                        InputFieldFactory.createTextField("lotNumber", "Lot number", false),
                        InputFieldFactory.createTextField("catalogNumber", "Catalog number", false),
                        InputFieldFactory.createDateField("expirationDate", "Expiration date", false),
                        InputFieldFactory.createTextField("serialNumber", "Serial number", false),
                        InputFieldFactory.createTextField("otherNumber", "Other number", false),
                        InputFieldFactory.createSelectField("deviceOperator", "Device operator", false, WebUtils.collectOptions(Arrays.asList(DeviceOperator.values()),null, "displayName", "Please select")),
                        otherDeviceOperator,
                        InputFieldFactory.createPastDateField("implantedDate", "If implanted, enter a date", false),
                        InputFieldFactory.createPastDateField("explantedDate", "IF explanted, enter a date", false),
                        deviceReprocessedField,
                        reprocessorName,
                        reprocessorAddress,
                        evaluationAvailabilityField,
                        InputFieldFactory.createPastDateField("returnedDate", "Returned date", false),
                        studyDeviceField
        );

    }

    @Override
    protected void createFieldGroups(AeInputFieldCreator creator, ExpeditedAdverseEventInputCommand command) {
        createDeviceFieldGroups(creator, command);
        createRadiationFieldGroups(creator, command);
        createSurgeryFieldGroups(creator, command);
        createAgentFieldGroups(creator, command);
        createOtherInterventionsFieldGroups(creator, command);
    }

    @Override
    protected boolean methodInvocationRequest(HttpServletRequest request) {
    	return org.springframework.web.util.WebUtils.hasSubmitParameter(request, "currentItem") && org.springframework.web.util.WebUtils.hasSubmitParameter(request, "task");
    }

    @Override
    public String getMethodName(HttpServletRequest request) {
    	String currentItem = request.getParameter("currentItem");
    	String task = request.getParameter("task");
    	return methodNameMap.get(task + currentItem);
    }
    
    @Override
    public ExpeditedReportSection[] section() {
        return new ExpeditedReportSection[] {
                ExpeditedReportSection.STUDY_INTERVENTIONS,
                ExpeditedReportSection.AGENTS_INTERVENTION_SECTION,
    			ExpeditedReportSection.RADIATION_INTERVENTION_SECTION,
    			ExpeditedReportSection.SURGERY_INTERVENTION_SECTION,
    			ExpeditedReportSection.MEDICAL_DEVICE_SECTION};
    }
	protected Map<Object, Object> createInvestigationalAgentAdministeredOptions() {
		Map<Object, Object> options = new LinkedHashMap<Object, Object>();
        options.put("", "Please select");
        options.put(Boolean.TRUE, "Yes");
        options.put(Boolean.FALSE, "No");
        return options;
    }
    public void setConfigurationProperty(ConfigProperty configurationProperty) {
        this.configurationProperty = configurationProperty;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public ModelAndView addSurgery(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        SurgeryIntervention si = new SurgeryIntervention();
        List<SurgeryIntervention> surgeries = cmd.getAeReport().getSurgeryInterventions();
        cmd.getAeReport().addSurgeryIntervention(si);
        si.setReport(cmd.getAeReport());
        ModelAndView modelAndView = new ModelAndView("ae/ajax/surgeryInterventionFormSection");
        modelAndView.getModel().put("surgeries", surgeries);
        modelAndView.getModel().put("indexes", new Integer[]{surgeries.size() - 1});
        return modelAndView;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public ModelAndView addBehavioral(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        BehavioralIntervention si = new BehavioralIntervention();
        List<BehavioralIntervention> behaviorals = cmd.getAeReport().getBehavioralInterventions();
        cmd.getAeReport().addBehavioralIntervention(si);
        si.setReport(cmd.getAeReport());
        ModelAndView modelAndView = new ModelAndView("ae/ajax/behavioralInterventionFormSection");
        modelAndView.getModel().put("behaviorals", behaviorals);
        modelAndView.getModel().put("indexes", new Integer[]{behaviorals.size() - 1});
        return modelAndView;
    }

     public ModelAndView addBiological(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        BiologicalIntervention si = new BiologicalIntervention();
        List<BiologicalIntervention> biologicals = cmd.getAeReport().getBiologicalInterventions();
        cmd.getAeReport().addBilogicalIntervention(si);
        si.setReport(cmd.getAeReport());
        ModelAndView modelAndView = new ModelAndView("ae/ajax/biologicalInterventionFormSection");
        modelAndView.getModel().put("biologicals", biologicals);
        modelAndView.getModel().put("indexes", new Integer[]{biologicals.size() - 1});
        return modelAndView;
    }

    public ModelAndView addDietary(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        DietarySupplementIntervention si = new DietarySupplementIntervention();
        List<DietarySupplementIntervention> dietaries = cmd.getAeReport().getDietaryInterventions();
        cmd.getAeReport().addDietarySupplementalIntervention(si);
        si.setReport(cmd.getAeReport());
        ModelAndView modelAndView = new ModelAndView("ae/ajax/dietaryInterventionFormSection");
        modelAndView.getModel().put("dietaries", dietaries);
        modelAndView.getModel().put("indexes", new Integer[]{dietaries.size() - 1});
        return modelAndView;
    }

    public ModelAndView addGenetic(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        GeneticIntervention si = new GeneticIntervention();
        List<GeneticIntervention> genetics = cmd.getAeReport().getGeneticInterventions();
        cmd.getAeReport().addGeneticIntervention(si);
        si.setReport(cmd.getAeReport());
        ModelAndView modelAndView = new ModelAndView("ae/ajax/geneticInterventionFormSection");
        modelAndView.getModel().put("genetics", genetics);
        modelAndView.getModel().put("indexes", new Integer[]{genetics.size() - 1});
        return modelAndView;
    }

    public ModelAndView addOtherAE(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        OtherAEIntervention si = new OtherAEIntervention();
        List<OtherAEIntervention> otherAEInterventions = cmd.getAeReport().getOtherAEInterventions();
        cmd.getAeReport().addOtherAEIntervention(si);
        si.setReport(cmd.getAeReport());
        ModelAndView modelAndView = new ModelAndView("ae/ajax/otherAEInterventionFormSection");
        modelAndView.getModel().put("otherAEInterventions", otherAEInterventions);
        modelAndView.getModel().put("indexes", new Integer[]{otherAEInterventions.size() - 1});
        return modelAndView;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public ModelAndView addRadiation(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        RadiationIntervention ri = new RadiationIntervention();
        List<RadiationIntervention> radiations = cmd.getAeReport().getRadiationInterventions();
        cmd.getAeReport().addRadiationIntervention(ri);
        ri.setReport(cmd.getAeReport());
        ModelAndView modelAndView = new ModelAndView("ae/ajax/radiationInterventionFormSection");
        modelAndView.getModel().put("radiations", radiations);
        modelAndView.getModel().put("indexes", new Integer[]{radiations.size() - 1});
        return modelAndView;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public ModelAndView addDevice(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        MedicalDevice md = new MedicalDevice();
        List<MedicalDevice> devices = cmd.getAeReport().getMedicalDevices();
        cmd.getAeReport().addMedicalDevice(md);
        md.setReport(cmd.getAeReport());
        ModelAndView modelAndView = new ModelAndView("ae/ajax/medicalDeviceFormSection");
        modelAndView.getModel().put("devices", devices);
        modelAndView.getModel().put("indexes", new Integer[]{devices.size() - 1});
        return modelAndView;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public ModelAndView addAgent(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        CourseAgent ca = new CourseAgent();
        List<CourseAgent> agents = cmd.getAeReport().getTreatmentInformation().getCourseAgents();
        cmd.getAeReport().getTreatmentInformation().addCourseAgent(ca);
        ca.getTreatmentInformation().setReport(cmd.getAeReport());
        ModelAndView modelAndView = new ModelAndView("ae/ajax/courseAgentFormSection");
        modelAndView.getModel().put("agents", agents);
        modelAndView.getModel().put("indexes", new Integer[]{agents.size() - 1});
        return modelAndView;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public ModelAndView removeSurgery(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        List<SurgeryIntervention> surgeries = cmd.getAeReport().getSurgeryInterventions();

        int index;
        try {
            index = Integer.parseInt(request.getParameter("index"));
        } catch (NumberFormatException e) {
            index = -1;
            log.debug("Wrong <index> for <surgeries> list: " + e.getMessage());
        }

        if (surgeries.size() - 1 < index) {
            log.debug("Wrong <index> for <surgeries> list.");
        } else if (index >=0) {
            SurgeryIntervention object = (SurgeryIntervention)surgeries.get(index);
            surgeries.remove(object);
            deleteAttributions(object, (ExpeditedAdverseEventInputCommand)command);
        }

        int size = surgeries.size();
    	Integer[] indexes = new Integer[size];
    	for(int i = 0 ; i < size ; i++){
    		indexes[i] = size - (i + 1);
    	}
        ModelAndView modelAndView = new ModelAndView("ae/ajax/surgeryInterventionFormSection");
        modelAndView.getModel().put("surgeries", surgeries);
        modelAndView.getModel().put("indexes", indexes);

        return modelAndView;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public ModelAndView removeBehavioral(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        List<BehavioralIntervention> bs = cmd.getAeReport().getBehavioralInterventions();

        int index;
        try {
            index = Integer.parseInt(request.getParameter("index"));
        } catch (NumberFormatException e) {
            index = -1;
            log.debug("Wrong <index> for <behaviorals> list: " + e.getMessage());
        }

        if (bs.size() - 1 < index) {
            log.debug("Wrong <index> for <behaviorals> list.");
        } else if (index >=0) {
            BehavioralIntervention object = (BehavioralIntervention)bs.get(index);
            bs.remove(object);
            deleteAttributions(object, (ExpeditedAdverseEventInputCommand)command);
        }

        int size = bs.size();
    	Integer[] indexes = new Integer[size];
    	for(int i = 0 ; i < size ; i++) {
    		indexes[i] = size - (i + 1);
    	}
        ModelAndView modelAndView = new ModelAndView("ae/ajax/behavioralInterventionFormSection");
        modelAndView.getModel().put("behaviorals", bs);
        modelAndView.getModel().put("indexes", indexes);

        return modelAndView;
    }

    public ModelAndView removeBiological(HttpServletRequest request, Object command, Errors errors) {
            ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
            List<BiologicalIntervention> bs = cmd.getAeReport().getBiologicalInterventions();

            int index;
            try {
                index = Integer.parseInt(request.getParameter("index"));
            } catch (NumberFormatException e) {
                index = -1;
                log.debug("Wrong <index> for <biologicals> list: " + e.getMessage());
            }

            if (bs.size() - 1 < index) {
                log.debug("Wrong <index> for <biologicals> list.");
            } else if (index >=0) {
                BiologicalIntervention object = (BiologicalIntervention)bs.get(index);
                bs.remove(object);
                deleteAttributions(object, (ExpeditedAdverseEventInputCommand)command);
            }

            int size = bs.size();
            Integer[] indexes = new Integer[size];
            for(int i = 0 ; i < size ; i++) {
                indexes[i] = size - (i + 1);
            }
            ModelAndView modelAndView = new ModelAndView("ae/ajax/biologicalInterventionFormSection");
            modelAndView.getModel().put("biologicals", bs);
            modelAndView.getModel().put("indexes", indexes);

            return modelAndView;
        }





    public ModelAndView removeDietary(HttpServletRequest request, Object command, Errors errors) {
               ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
               List<DietarySupplementIntervention> bs = cmd.getAeReport().getDietaryInterventions();

               int index;
               try {
                   index = Integer.parseInt(request.getParameter("index"));
               } catch (NumberFormatException e) {
                   index = -1;
                   log.debug("Wrong <index> for <dietaries> list: " + e.getMessage());
               }

               if (bs.size() - 1 < index) {
                   log.debug("Wrong <index> for <dietaries> list.");
               } else if (index >=0) {
                   DietarySupplementIntervention object = (DietarySupplementIntervention)bs.get(index);
                   bs.remove(object);
                   deleteAttributions(object, (ExpeditedAdverseEventInputCommand)command);
               }

               int size = bs.size();
               Integer[] indexes = new Integer[size];
               for(int i = 0 ; i < size ; i++) {
                   indexes[i] = size - (i + 1);
               }
               ModelAndView modelAndView = new ModelAndView("ae/ajax/dietaryInterventionFormSection");
               modelAndView.getModel().put("dietaries", bs);
               modelAndView.getModel().put("indexes", indexes);

               return modelAndView;
           }

    public ModelAndView removeGenetic(HttpServletRequest request, Object command, Errors errors) {
                ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
                List<GeneticIntervention> bs = cmd.getAeReport().getGeneticInterventions();

                int index;
                try {
                    index = Integer.parseInt(request.getParameter("index"));
                } catch (NumberFormatException e) {
                    index = -1;
                    log.debug("Wrong <index> for <genetics> list: " + e.getMessage());
                }

                if (bs.size() - 1 < index) {
                    log.debug("Wrong <index> for genetics> list.");
                } else if (index >=0) {
                    GeneticIntervention object = (GeneticIntervention)bs.get(index);
                    bs.remove(object);
                    deleteAttributions(object, (ExpeditedAdverseEventInputCommand)command);
                }

                int size = bs.size();
                Integer[] indexes = new Integer[size];
                for(int i = 0 ; i < size ; i++) {
                    indexes[i] = size - (i + 1);
                }
                ModelAndView modelAndView = new ModelAndView("ae/ajax/geneticInterventionFormSection");
                modelAndView.getModel().put("genetics", bs);
                modelAndView.getModel().put("indexes", indexes);

                return modelAndView;
            }


    public ModelAndView removeOtherAE(HttpServletRequest request, Object command, Errors errors) {
                ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
                List<OtherAEIntervention> bs = cmd.getAeReport().getOtherAEInterventions();

                int index;
                try {
                    index = Integer.parseInt(request.getParameter("index"));
                } catch (NumberFormatException e) {
                    index = -1;
                    log.debug("Wrong <index> for <otherAEinterventions> list: " + e.getMessage());
                }

                if (bs.size() - 1 < index) {
                    log.debug("Wrong <index> for genetics> list.");
                } else if (index >=0) {
                   OtherAEIntervention object = (OtherAEIntervention)bs.get(index);
                    bs.remove(object);
                    deleteAttributions(object, (ExpeditedAdverseEventInputCommand)command);
                }

                int size = bs.size();
                Integer[] indexes = new Integer[size];
                for(int i = 0 ; i < size ; i++) {
                    indexes[i] = size - (i + 1);
                }
                ModelAndView modelAndView = new ModelAndView("ae/ajax/otherAEInterventionFormSection");
                modelAndView.getModel().put("otherAEInterventions", bs);
                modelAndView.getModel().put("indexes", indexes);

                return modelAndView;
            }

    // ----------------------------------------------------------------------------------------------------------------

    public ModelAndView removeRadiation(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        List<RadiationIntervention> radiations = cmd.getAeReport().getRadiationInterventions();

        int index;
        try {
            index = Integer.parseInt(request.getParameter("index"));
        } catch (NumberFormatException e) {
            index = -1;
            log.debug("Wrong <index> for <radiations> list: " + e.getMessage());
        }

        if (radiations.size() - 1 < index) {
            log.debug("Wrong <index> for <radiations> list.");
        } else if (index >=0) {
            RadiationIntervention object = (RadiationIntervention)radiations.get(index);
            radiations.remove(object);
            deleteAttributions(object, (ExpeditedAdverseEventInputCommand)command);
        }

        int size = radiations.size();
    	Integer[] indexes = new Integer[size];
    	for(int i = 0 ; i < size ; i++){
    		indexes[i] = size - (i + 1);
    	}
        ModelAndView modelAndView = new ModelAndView("ae/ajax/radiationInterventionFormSection");
        modelAndView.getModel().put("radiations", radiations);
        modelAndView.getModel().put("indexes", indexes);

        return modelAndView;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public ModelAndView removeDevice(HttpServletRequest request, Object command, Errors errors) {
        ExpeditedAdverseEventInputCommand cmd = (ExpeditedAdverseEventInputCommand)command;
        List<MedicalDevice> devices = cmd.getAeReport().getMedicalDevices();

        int index;
        try {
            index = Integer.parseInt(request.getParameter("index"));
        } catch (NumberFormatException e) {
            index = -1;
            log.debug("Wrong <index> for <devices> list: " + e.getMessage());
        }

        if (devices.size() - 1 < index) {
            log.debug("Wrong <index> for <devices> list.");
        } else if (index >=0) {
            MedicalDevice object = (MedicalDevice)devices.get(index);
            devices.remove(object);
            deleteAttributions(object, (ExpeditedAdverseEventInputCommand)command);
        }

        int size = devices.size();
    	Integer[] indexes = new Integer[size];
    	for(int i = 0 ; i < size ; i++){
    		indexes[i] = size - (i + 1);
    	}
        ModelAndView modelAndView = new ModelAndView("ae/ajax/medicalDeviceFormSection");
        modelAndView.getModel().put("devices", devices);
        modelAndView.getModel().put("indexes", indexes);

        return modelAndView;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public ModelAndView removeAgent(HttpServletRequest request, Object command, Errors errors) {
        AbstractExpeditedAdverseEventInputCommand cmd = (AbstractExpeditedAdverseEventInputCommand)command;
        List<CourseAgent> agents = cmd.getAeReport().getTreatmentInformation().getCourseAgents();

        int index;
        try {
            index = Integer.parseInt(request.getParameter("index"));
        } catch (NumberFormatException e) {
            index = -1;
            log.debug("Wrong <index> for <agents> list: " + e.getMessage());
        }

        if (agents.size() - 1 < index) {
            log.debug("Wrong <index> for <agents> list.");
        } else if (index >=0) {
        	cmd.deleteAttribution(agents.get(index));
            agents.remove(agents.get(index));
        };

        int size = agents.size();
    	Integer[] indexes = new Integer[size];
    	for(int i = 0 ; i < size ; i++){
    		indexes[i] = size - (i + 1);
    	}
        ModelAndView modelAndView = new ModelAndView("ae/ajax/courseAgentFormSection");
        modelAndView.getModel().put("agents", agents);
        modelAndView.getModel().put("indexes", indexes);

        return modelAndView;
    }

    public void deleteAttributions(ExpeditedAdverseEventReportChild child, ExpeditedAdverseEventInputCommand command) {
        command.getAeReport().cascaeDeleteToAttributions((DomainObject) child);
        child.setReport(null);
    }

    public void insertAttributions(ExpeditedAdverseEventReportChild child, ExpeditedAdverseEventInputCommand command) {
        command.getAeReport().addAttributionsToAEs((DomainObject) child);
    }

    public ExpeditedAdverseEventReportDao getExpeditedAdverseEventReportDao() {
        return expeditedAdverseEventReportDao;
    }

    public void setExpeditedAdverseEventReportDao(ExpeditedAdverseEventReportDao expeditedAdverseEventReportDao) {
        this.expeditedAdverseEventReportDao = expeditedAdverseEventReportDao;
    }

    @Override
    public boolean hasEmptyMandatoryFields(ExpeditedAdverseEventInputCommand command, HttpServletRequest request) {
    	boolean hasEmptyFields =  super.hasEmptyMandatoryFields(command, request);
    	boolean hasAtleastAnIntervention = CollectionUtils.isNotEmpty(command.getAeReport().getRadiationInterventions()) ||
    								CollectionUtils.isNotEmpty(command.getAeReport().getSurgeryInterventions()) ||
    								CollectionUtils.isNotEmpty(command.getAeReport().getMedicalDevices()) ||
    								CollectionUtils.isNotEmpty(command.getAeReport().getTreatmentInformation().getCourseAgents());
    	
    	return hasEmptyFields || !hasAtleastAnIntervention;
    }

	public ConfigPropertyRepositoryImpl getConfigPropertyRepositoryImpl() {
		return configPropertyRepositoryImpl;
	}

	public void setConfigPropertyRepositoryImpl(
			ConfigPropertyRepositoryImpl configPropertyRepositoryImpl) {
		this.configPropertyRepositoryImpl = configPropertyRepositoryImpl;
	}

    @Override
    public void postProcess(HttpServletRequest request, ExpeditedAdverseEventInputCommand command, Errors errors) {
        super.postProcess(request, command, errors);
        AbstractExpeditedAdverseEventInputCommand c = (AbstractExpeditedAdverseEventInputCommand)command;

        // check interventions
        for (CourseAgent ca : c.getAeReport().getTreatmentInformation().getCourseAgents()) c.addAttribution(ca);
        for (SurgeryIntervention si : c.getAeReport().getSurgeryInterventions()) insertAttributions(si, c);
        for (BehavioralIntervention si : c.getAeReport().getBehavioralInterventions()) insertAttributions(si, c);
        for (BiologicalIntervention si : c.getAeReport().getBiologicalInterventions()) insertAttributions(si, c);
        for (DietarySupplementIntervention si : c.getAeReport().getDietaryInterventions()) insertAttributions(si, c);
        for (GeneticIntervention si : c.getAeReport().getGeneticInterventions()) insertAttributions(si, c);
        for (OtherAEIntervention si : c.getAeReport().getOtherAEInterventions()) insertAttributions(si, c);
        for (RadiationIntervention si : c.getAeReport().getRadiationInterventions()) insertAttributions(si, c);
        for (MedicalDevice si : c.getAeReport().getMedicalDevices()) insertAttributions(si, c);
    }
}
