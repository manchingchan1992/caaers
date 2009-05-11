<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="ui" tagdir="/WEB-INF/tags/ui" %>

<%@attribute name="path" description="The path to bind" required="true"%>
<%@attribute name="cssClass" description="The 'class' attribute in HTML" %>
<%@attribute name="validationJSClass" description="The classes required for validation framework" %>
<%@attribute name="readonly" description="Specifies the readonly attribute" %>
<%@attribute name="required" type="java.lang.Boolean" description="Tells that this field is a required (red asterisk)"%>
<%@attribute name="mandatory" type="java.lang.Boolean" description=""%>
<%@attribute name="displayNamePath" description="This path is used to display the text, when the field is readOnly, if not specified 'path' is used as default " %>
<%@attribute name="title" description="Specifies the alternate or tooltip title" %>
<%@attribute name="embededJS" description="A piece of javascript, that if specified will be embeded along with this input"%>
<%@attribute name="field" type="gov.nih.nci.cabig.caaers.web.fields.InputField"%>
<%@attribute name="size" description="Specifies the display size of the text field" %>
<%@attribute name="maxlength" description="Specifies max allowed characters" %>
<%@attribute name="disabled" type="java.lang.Boolean" description="(Deprecated) Specifies whether the field to be displayed in disabled mode" %>
<%@taglib prefix="caaers" uri="http://gforge.nci.nih.gov/projects/caaers/tags" %>

<c:if test="${field != null}"><c:set var="mandatory" value="${field.attributes.mandatory}" /></c:if>

<c:set var="fieldValue"><jsp:attribute name="value"><caaers:value path="${path}" /></jsp:attribute></c:set>
<c:if test="${empty fieldValue && required}"><c:set var="cssValue" value="required" /></c:if>
<c:if test="${empty fieldValue && mandatory}"><c:set var="cssValue" value="mandatory" /></c:if>
<c:if test="${empty fieldValue && mandatory && required}"><c:set var="cssValue" value="mandatory required" /></c:if>
<c:if test="${not empty fieldValue && (mandatory || required)}"><c:set var="cssValue" value="valueOK" /></c:if>

<ui:fieldWrapper path="${path}" cssClass="${cssClass}" validationJSClass="${validationJSClass}" readonly="${readonly}"  required="${required}" displayNamePath="${displayNamePath}" title="${title}" embededJS="${embededJS}">
        <jsp:attribute name="field">
        <form:input path="${path}" disabled="${disabled}" size="${size}" title="${title}" cssClass=" ${cssValue} ${validationCss} ${cssClass}" maxlength="${empty maxlength ? '2000' : maxlength}"/>
            <c:if test="${not empty field.attributes.help and field.categoryName ne 'autocompleter'}">
                <tags:hoverHelp path="${field.propertyName}" code="${field.attributes.help}" />
            </c:if>
        </jsp:attribute>
</ui:fieldWrapper>