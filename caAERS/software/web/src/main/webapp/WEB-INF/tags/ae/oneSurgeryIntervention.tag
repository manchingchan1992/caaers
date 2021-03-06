<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="ui" tagdir="/WEB-INF/tags/ui" %>
<%@ taglib prefix="caaers" uri="http://gforge.nci.nih.gov/projects/caaers/tags" %>

<%@attribute name="index" required="true" type="java.lang.Integer" %>
<%@attribute name="style"%>
<%@attribute name="surgery" type="gov.nih.nci.cabig.caaers.domain.SurgeryIntervention" %>
<%@attribute name="collapsed" type="java.lang.Boolean" %>

<c:set var="v" value="aeReport.surgeryInterventions[${index}]" />
<ae:fieldGroupDivision fieldGroupFactoryName="surgeryIntervention" index="${index}" enableDelete="true" deleteParams="'surgery', ${index}, '_surgeries'" collapsed="${!empties[v] && collapsed}">
    <tags:errors path="aeReport.surgeryInterventions[${index}]"/>

    <ui:row path="aeReport.surgeryInterventions[${index}].studySurgery">
         <jsp:attribute name="label"><ui:label path="${fieldGroup.fields[4].propertyName}" text="${fieldGroup.fields[4].displayName}"
                                               mandatory="${fieldGroup.fields[4].attributes.mandatory}" required="true"/></jsp:attribute>
         <jsp:attribute name="value"><ui:select path="${fieldGroup.fields[4].propertyName}" options="${fieldGroup.fields[4].attributes.options}"
                                                field="${fieldGroup.fields[4]}"/></jsp:attribute>
    </ui:row>

    <ui:row path="aeReport.surgeryInterventions[${index}].studySurgery.description">
        <jsp:attribute name="label">
            <caaers:message code="LBL_aeReport.surgeryInterventions.studySurgery.description" text="Study surgery description" />
        </jsp:attribute>
        <jsp:attribute name="value">
            <span id="aeReport.surgeryInterventions[${index}].studySurgery.description_content">
                <c:out value="${empty surgery.studySurgery.description ? '' : surgery.studySurgery.description}" />
           </span>
        </jsp:attribute>
    </ui:row>

    <ui:row path="aeReport.surgeryInterventions[${index}].interventionSite">
         <jsp:attribute name="label"><tags:renderLabel field="${fieldGroup.fields[2]}"/></jsp:attribute>
         <jsp:attribute name="value">
             <c:set var="initValue" value="${not empty surgery.interventionSite ? surgery.interventionSite.name : 'Begin typing here'}"/>
             <ui:autocompleter path="aeReport.surgeryInterventions[${index}].interventionSite" initialDisplayValue="${initValue}" enableClearButton="true" 
             	field="${fieldGroup.fields[2]}">
						<jsp:attribute name="populatorJS">
							function(autocompleter, text){
                                createAE.matchInterventionSites(text, function(values) {
                                    autocompleter.setChoices(values)
                                })
							}
						</jsp:attribute>
						<jsp:attribute name="selectorJS">
							function(interventionSiteCondition) {
            					return interventionSiteCondition.name;
        					}
						</jsp:attribute>
             </ui:autocompleter>
         </jsp:attribute>
    </ui:row>

    <ui:row path="aeReport.surgeryInterventions[${index}].interventionDate">
         <jsp:attribute name="label"><tags:renderLabel field="${fieldGroup.fields[3]}"/></jsp:attribute>
         <jsp:attribute name="value"><ui:date path="aeReport.surgeryInterventions[${index}].interventionDate" field="${fieldGroup.fields[3]}"/></jsp:attribute>
    </ui:row>

<script language="JavaScript">
    AE.registerCalendarPopups();
</script>

</ae:fieldGroupDivision>

<script>
    function setTitleSurgery_${index}() {
        var titleID = "titleOf_surgeryIntervention-" + ${index};
        var fieldObject = $("aeReport.surgeryInterventions[${index}].interventionSite-input");
        var selectedValue = fieldObject.value;
        var surgeryInterventionDate = $("aeReport.surgeryInterventions[${index}].interventionDate").value;
        
        if (selectedValue != "Begin typing here")
            $(titleID).innerHTML = "" + selectedValue + " (" + surgeryInterventionDate + ")";

    }

    setTitleSurgery_${index}.defer();
    Event.observe($("aeReport.surgeryInterventions[${index}].interventionSite-input"), "blur", function() {
        setTitleSurgery_${index}();
    });

    Event.observe($("aeReport.surgeryInterventions[${index}].interventionDate"), "change", function() {
        setTitleSurgery_${index}();
    });

    Event.observe($("aeReport.surgeryInterventions[${index}].studySurgery"), "change", function(event) {
        updateOtherInterventionDescription(Event.element(event), "aeReport.surgeryInterventions[${index}].studySurgery.description_content" );
    });

</script>
