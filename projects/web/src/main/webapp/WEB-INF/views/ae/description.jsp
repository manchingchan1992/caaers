<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${tab.longTitle}</title>
    <tags:stylesheetLink name="ae"/>
    <tags:includeScriptaculous/>
    <style type="text/css">
        
        div.row div.label { width: 18em; } 
		div.row div.value, div.row div.extra { margin-left: 19em; }
    </style>
</head>
<body>
<tags:tabForm tab="${tab}" flow="${flow}" pageHelpAnchor="section5describeevent">
    <jsp:attribute name="instructions">
    <tags:instructions code="instruction_ae_description" />
    </jsp:attribute>
    <jsp:attribute name="singleFields">
        <c:forEach items="${fieldGroups.desc.fields}" var="field">
            <tags:renderRow field="${field}"/>
        </c:forEach>
    </jsp:attribute>
</tags:tabForm>
</body>
</html>