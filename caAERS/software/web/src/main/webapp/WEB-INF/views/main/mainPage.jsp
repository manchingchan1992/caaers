<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@ taglib prefix="study" tagdir="/WEB-INF/tags/study" %>

<%@ taglib prefix="csmauthz" uri="http://csm.ncicb.nci.nih.gov/authz" %>
<!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <title>Welcome to caAERS</title>
    <link rel="icon" href="../images/caaers.ico"/>
    <style>
        .division .header { margin-bottom:5px; }
    </style>
</head>
<body>



<c:set var="_regularTasksVisible" value="${false}" />
<c:set var="_regularTasks">
    <jsp:attribute name="value">
        <div class="tasksSubheader">
            <table width='100%' cellpadding="0" cellspacing="0">
                <tr><td><h3 class='subHeader'>REGULAR TASKS</h3></tr>
            </table>
        </div>

        <div style="margin-left: 1px; margin-right:2px;" class="subSubheader">
            <table width="100%" cellpadding="0" cellspacing="0" border="0">
            <tr>
                <c:forEach begin="0" end="3" items="${taskgroups}" var="taskGroup" varStatus="index">
                    <csmauthz:accesscontrol domainObject="${taskGroup}" authorizationCheckName="taskGroupAuthorizationCheck">
                    <c:if test="${index.index != 2}">
                    <c:set var="_regularTasksVisible" value="${true}" />
                    <td align="center" valign="top">

                        <table width="100%" cellpadding="0" cellspacing="0" border="0">
                        <tr><td colspan="2" headers="35px" class="tasksSubSubheader" align="center"><span class="tasksSubSubheaderText">${taskGroup.displayName}</span></td></tr>

                        <c:forEach items="${taskGroup.taskList}" var="task">
                            <csmauthz:accesscontrol domainObject="${task}" authorizationCheckName="taskAuthorizationCheck">
                            <tr><td class="taskItemImage"><img src="<c:url value="/images/blue/icons/${task.linkName}_icon2.png" />"></td><td width="100%" class="taskItem" style="margin-right:1px;"><a href="<c:url value="${task.url}"/>">${task.displayName}</a></td></tr>
                            </csmauthz:accesscontrol>
                        </c:forEach>

                        </table>
                    </td>
                    </c:if>
                    </csmauthz:accesscontrol>
                </c:forEach>
            </tr>
            </table>
        </div>
    </jsp:attribute>
</c:set>

<c:set var="_adminTasksVisible" value="${false}" />
<c:set var="_adminTasks">
    <jsp:attribute name="value">
        <div class="tasksSubheader">
            <table width='100%' cellpadding="0" cellspacing="0">
                <tr><td><h3 class='subHeader'>Setup and Administration Tasks</h3></tr>
            </table>
        </div>

        <div style="margin-left: 1px; margin-right:2px;" class="subSubheader">
            <table width="100%" cellpadding="0" cellspacing="0" border="0">
            <tr>
                <c:forEach begin="4" end="9" items="${taskgroups}" var="taskGroup" varStatus="index">
                    <csmauthz:accesscontrol domainObject="${taskGroup}" authorizationCheckName="taskGroupAuthorizationCheck">
                    <c:set var="_adminTasksVisible" value="${true}" />
                    <td align="center" valign="top">

                        <table width="100%" cellpadding="0" cellspacing="0" border="0">
                        <tr><td colspan="3" headers="35px" class="tasksSubSubheader" align="center"><span class="tasksSubSubheaderText">${taskGroup.displayName}</span></td></tr>

                        <c:forEach items="${taskGroup.taskList}" var="task">
                            <csmauthz:accesscontrol domainObject="${task}" authorizationCheckName="taskAuthorizationCheck">
                            <tr><td class="taskItemImage"><img src="<c:url value="/images/blue/icons/${task.linkName}_icon2.png"/>"></td><td width="100%" class="taskItem"><a href="<c:url value="${task.url}"/>">${task.displayName}</a></td></tr>
                            </csmauthz:accesscontrol>
                        </c:forEach>

                        </table>
                    </td>
                    </csmauthz:accesscontrol>
                </c:forEach>
            </tr>
            </table>
        </div>
    </jsp:attribute>
</c:set>




<%-- RENDERING PART --%>

<jsp:include page="/pages/dashboard" />

<chrome:boxIPhone title="Quick Links" style="width:100%;">
<jsp:body>

    <c:if test="${_regularTasksVisible}">
        ${_regularTasks}
    </c:if>

    <c:if test="${_adminTasksVisible}">
        ${_adminTasks}
    </c:if>

</jsp:body>
</chrome:boxIPhone>

<style>

    td.taskItem {
        height: 37px;
        background-image: url(../images/iphone2/taskItemImage.jpg);
    }

    td.taskItemImage {
        height: 37px;
        background-image: url(../images/iphone2/quick-links_up.jpg);
        padding-left:3px;
        padding-right:10px;
    }

    td.taskItem a {
        font-family: Lucida Sans Unicode, Lucida Grande, sans-serif;
        font-size: 11px;
        color: #094c86;
        text-shadow: 0 1px white;
        font-weight: bold;
        text-decoration: none;
    }

    .tasksSubheader h3.subHeader {
        font-family: Lucida Sans Unicode, Lucida Grande, sans-serif;
        font-size: 14px;
        font-weight: bold;
        color: #094c86;
        text-shadow: 0 1px white;
        text-transform: uppercase;
    }

    .subSubheader span.tasksSubSubheaderText {
        font-family: Lucida Sans Unicode, Lucida Grande, sans-serif;
        font-size: 14px;
        font-weight: bold;
        color: #fff;
        text-shadow: 0 -1px #4a4a4a;
        margin: 5px;
    }
    
    .reportActivity { background-color: #99cc00; color: #f00; border:1px #EEEEFF solid; padding: 1px 3px 1px 1px;}
    .pastDue { background-color: #ff6666; color: #ff6666; border:0px solid #000; border:1px #EEEEFF solid; padding: 1px 3px 1px 1px;}
    .calendar tbody .rowhilite td.pastDue { background: #ff6666; border:1px #EEEEFF solid; padding: 1px 3px 1px 1px;}
    .calendar tbody .rowhilite td.pastDue.hilite { background: #ff6666; border:1px #BBB solid; padding: 1px 3px 1px 1px;}
    .calendar tbody .rowhilite td.reportActivity { background: #99cc00; border:1px #EEEEFF solid; padding: 1px 3px 1px 1px;}
    .calendar tbody .rowhilite td.reportActivity.hilite { background: #99cc00; border:1px #BBB solid; padding: 1px 3px 1px 1px;}
    .calendar tbody .reportActivity {font-weight: bold; color:white; border:1px #EEEEFF solid; padding: 1px 3px 1px 1px;}
    .calendar tbody .pastDue {font-weight: bold; color:white; border:1px #EEEEFF solid; padding: 1px 3px 1px 1px;}
    tr.last { border-bottom : 1px black solid; }
    .scroller { height: 70px; overflow-x: hidden; overflow-y: scroll; margin: 0px; }
    .scrollerTask { height: 227px; overflow-x: hidden; overflow-y: scroll; margin: 0px; }
    .scroller h4 { color: #933; display: inline; }
    .scroller div { height: 22px; padding: 8px; margin-top: -1px; }
    .scroller div.first { margin-top: 0; }
    .scrollerdiv.last { margin-bottom: 0; }
    .scroller p { font-size: 11px; margin-left: 0 !important; display: inline; }


    .boxIPhone {background-color: #fff; margin-bottom: 12px; background-image: url(../images/iphone2/box_bg.png); background-repeat: repeat-x; }
    .boxIPhone .header .bg-L { background: url(../images/iphone2/box_header_bg_l.png) no-repeat top left; }
    .boxIPhone .header .bg-R { background: url(../images/iphone2/box_header_bg_r.png) no-repeat top right; }
    .boxIPhone .subheader {background: url(../images/iphone2/box_subheader_bg.png) repeat-x; margin-left:2px; margin-right:2px; }
    .boxIPhone .tasksSubheader {background: url(../images/iphone2/secondary-header_bg2.jpg) repeat-x;  margin-left:2px; margin-right:2px; margin-bottom:0px; margin-top:0px; height:32px;}
    .boxIPhone .tasksSubSubheader {background: url(../images/iphone2/tertiary-header_bg.jpg) no-repeat; margin-left:2px; margin-right:2px; height:35px;}

    .b-T {}
    .b-L { background: url(../images/iphone2/box_l.png) repeat-y left;}
    .b-R { background: url(../images/iphone2/box_r.png) repeat-y right; }
    .b-B { background: url(../images/iphone2/box_bottom.png) repeat-x bottom; }
    .b-TL {}
    .b-TR {}
    .b-BL { background: url(../images/iphone2/box_bl.png) no-repeat bottom left; }
    .b-BR { background: url(../images/iphone2/box_br.png) no-repeat bottom right; }
    .boxIPhone .header h2 { text-align: left; padding: 7px 0 4px 20px; font: 20px Arial, Helvetica, sans-serif; color: #fff; text-shadow: 0 -2px #064d8c; }

    .boxIPhone .interior {
        margin: 0px 0px 0px 0px;
        padding: 1px;
        padding-bottom: 10px;
        min-height: 28px;
    }

    .boxIPhone.tabbed .interior {
        padding-top: 0;
    }

    .boxIPhone .content {
        padding: 8px;
    }

    .boxIPhone h3 {
        font: bold 15px Arial, Helvetica, sans-serif;
        color: #ea4b4b;
        text-shadow: 0 -1px white;
        padding-top: 8px;
        padding-right: 0px;
        padding-bottom: 7px;
        padding-left: 20px;
    }

    .boxIPhone h3.blue {
        font: bold 15px Arial, Helvetica, sans-serif;
        color: blue;
        text-shadow: 0 -1px white;
        padding-top: 8px;
        padding-right: 0px;
        padding-bottom: 7px;
        padding-left: 20px;
    }

    .dashboard_table {
        font-family: Arial, Helvetica, sans-serif;
        margin: 0 1px 0 2px;
        /*border-collapse: collapse;*/
        border-bottom: 0px solid #dddddd;
        border-top: 0px solid #dddddd;
        padding-top: 1px;

    }

    .dashboard_table td, .dashboard_table th {
        font-size: 11px;
        /*border-right: 1px solid #dddddd;*/
        padding: 5px 7px 4px 4px;
        color: #3a3a3a;
    }

    .dashboard_table a {
        font-weight: bold;
        color: #3a3a3a;
        text-decoration: none;
    }

    .dashboard_table a:hover {
        color: #09589d;
        text-decoration: none;
    }

    .dashboard_table a:active {
        position: relative;
        top: 1px;
        color: #09589d;
        text-decoration: none;
    }

    .dashboard_table th {
        font-size: 12px;
        text-align: left;
        border: 0;
        padding-top: 4px;
        padding-bottom: 4px;
        background: url(../images/iphone/table-header_bg.png) center no-repeat;
        color: #0c62ac;
    }

    .dashboard_table tr.alt td {
        color: #3a3a3a;
        background-color: #f2f9ff;
    }

    a.quickLink, a.quickLink:visited {
        color : #518EC2;
        font-weight: bold;
        font-size: 14px;
        text-decoration: none;
    }

    img.quickLink {
    /*
        padding-right: 20px;
        padding-left: 20px;
    */
    }

    div.quickLinkRow {
        display: block;
        clear: both;
    }

    div.quickLinkRow div.quickLinkPicture {
        float: left;
        width : 40px;
        text-align: right;
    }

    div.quickLinkRow div.quickLinkLabel {
        margin-left: 50px;
        text-align: left;
        vertical-align: middle;
    }

    td.quickLinkBGon {
        background-image: url("../images/iphone2/quickLinkBGon.png")
    }

    td.quickLinkBGoff {
        background-image: url("../images/iphone2/quickLinkBGoff.png")
    }

    tr.taskTitleRow th {
        color : #518EC2;
        font-weight: bold;
    }

    tr.taskTitleRow td, tr.taskTitleRow th {
        border-bottom: 1px #ccc solid;
    }

    a.linkHere, a.linkHere:hover {
        color : blue;
        text-decoration: underline;
    }
    
</style>

</body>
</html>