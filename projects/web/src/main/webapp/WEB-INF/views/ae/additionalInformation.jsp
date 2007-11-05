<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${tab.longTitle}</title>
    <tags:stylesheetLink name="ae"/>
    <style type="text/css">
        div.row div.label { width: 17em; } 
		div.row div.value, div.row div.extra { margin-left: 18em; }
    </style>
    <tags:includeScriptaculous/>
    <tags:dwrJavascriptLink objects="createAE"/>
    
</head>
<body>
<tags:tabForm tab="${tab}" flow="${flow}" pageHelpAnchor="section17attachments">
    <jsp:attribute name="instructions">
        <tags:instructions code="instruction_ae_additionalInfo" />
    </jsp:attribute>
    <jsp:attribute name="singleFields">
        <c:forEach items="${fieldGroups.desc.fields}" var="field" begin="0" end="11">
            <tags:renderRow field="${field}"/>
        </c:forEach>

        <div class="row">
         <div class="label"><tags:renderLabel field="${fieldGroups.desc.fields[12]}" /></div>
         <div class="value">
         	<tags:renderInputs field="${fieldGroups.desc.fields[12]}"/>
         </div>

        </div>
    </jsp:attribute>
</tags:tabForm>
</body>
</html>