<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="caaers" uri="http://gforge.nci.nih.gov/projects/caaers/tags" %>
<%@attribute name="path" description="This path is used by the render decision manager to determinie the visibility" %>
<%@attribute name="title"%>
<%@attribute name="titleFragment" fragment="true" description="This is a fragment, that is put beside the title" %>
<%@attribute name="additionalInfo" %>
<%@attribute name="id"%>
<%@attribute name="cssClass"%>
<%@attribute name="style"%>
<%@attribute name="enableDelete" type="java.lang.Boolean"%>
<%@attribute name="deleteParams" %>
<%@attribute name="collapsable" required="false" %>
<%@attribute name="collapsed" required="false" %>
<caaers:renderFilter elementID="${empty path ? 'dummyPath' : path}" uiType="DIVISION">
<div class="division ${cssClass} <c:if test='${collapsable}'>COLLAPSABLE-DIVISION</c:if>" <tags:attribute name="id" value="${id}"/> <tags:attribute name="style" value="${style}"/>>
    <div class="header">
    <c:if test="${not empty title or not empty titleFragment}">

        <c:if test="${enableDelete || collapsable}">
			<h3>
                <table cellspacing="1" cellpadding="1" border="0" width="100%">
                    <tr>
                        <c:if test="${collapsable}"><td align="left"><a style="cursor:pointer;" onClick="SwitchCollapsableState('contentOf-${id}', '${id}')"><img id="image-${id}" src="<c:url value="/images/arrow-${collapsed ? 'right' : 'down'}.png" />" border="0" style="padding-right:5px;"/></a></td></c:if>
                        <td width="100%"><span id="titleOf_${id}">${title}</span><jsp:invoke fragment="titleFragment" /></td>
                        <td align="right">${additionalInfo}</td>
                         <c:if test="${enableDelete and not empty deleteParams}">
                             <td align="left"><a style='cursor:pointer;' onclick="fireAction(<c:out value="${deleteParams}, '${id}', '${cssClass}'" />);">
                                <tags:button color="blue" value="" size="small" icon="x" type="button"/> 
                            </a></td>
                         </c:if>
                    </tr>
                </table>
				</h3>
           </c:if>

        <c:if test="${!enableDelete && !collapsable}">
	   		<h3>${title}</h3><jsp:invoke fragment="titleFragment" />
	   	</c:if>

    </c:if>
    </div>

    <div class="content" id="contentOf-${id}" style="display:${collapsed ? "none" : "block"};">
        <c:if test="${collapsable && (empty id)}"><h1 style="color:#ff0000; padding-bottom:20px;">Please give an unique ID to your Division Element.</h1></c:if>
        <jsp:doBody/>
    </div>
</div>
</caaers:renderFilter>
