<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/extremecomponents.css"/>">
<html>
<head>
<tags:stylesheetLink name="tabbedflow"/>
<tags:stylesheetLink name="participant"/>
<tags:includeScriptaculous />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<style type="text/css">
        /* Override default lable length */
         div.row div.label { width: 9em; } 
        div.row div.value { margin-left: 10em; }
        div.content {
            padding: 5px 15px;
        }        
</style>
<script type="text/javascript" src="/caaers/js/extremecomponents.js"></script>
<tags:dwrJavascriptLink objects="search"/>
</head>
<body>
<div class="tabpane">
  <ul id="workflow-tabs" class="tabs autoclear">
    <li class="tab selected"><div>
        <a href="createResearchStaff">Create Research Staff</a>
    </div></li>
    <li class="tab"><div>
        <a href="searchResearchStaff">Search Research Staff</a>
    </div></li>
  </ul>
  <br />

<tags:tabForm tab="${tab}" flow="${flow}"  formName="researchStaffForm">

    
		 <jsp:attribute name="singleFields">
            <div>
			<input type="hidden" name="_action" value="">
			<input type="hidden" name="_selected" value="">
		</div>
		       <c:if test="${(empty command.id) or ( command.id le 0) }"><input type="hidden" name="_finish" value="true"/></c:if>
		
			<table id="test2" class="single-fields" >
        	<tr >
    				<td> 
    				<c:forEach begin="0" end="3" items="${fieldGroups.researchStaff.fields}" var="field">
                    <tags:renderRow field="${field}"/>
                	</c:forEach>
    				</td>
    				<td><c:forEach begin="4" end="6" items="${fieldGroups.researchStaff.fields}" var="field">
                    <tags:renderRow field="${field}"/>
                	</c:forEach>
    				</td>
    			</tr>
    			
    		</table> 
             </jsp:attribute>
    
    
</tags:tabForm>

 </body>
</html>
