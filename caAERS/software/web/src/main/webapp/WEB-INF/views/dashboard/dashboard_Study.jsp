<%@include file="/WEB-INF/views/taglibs.jsp" %>
<html>
<head>
</head>
  
  <chrome:boxIPhone title="My Studies" style="width:700px;">
  	<jsp:body>
  		<form action = "dummy">
                <div id="myStudies" class="scrollerTask">
                    <table border="0" cellpadding="0" cellspacing="0" class="dashboard_table" width="99%">
                        <tr class="taskTitleRow">
                            <th>Study Id
                            <th width="50%">Title
                            <th>Status
                            <th>Action
                        </tr>
                         <c:forEach var ="study" items="${studyList}" varStatus = "loopStatus">
                         	<tr class="${loopStatus.index % 2 == 0 ? 'alt' : ''}">
                         		<td>${study.primaryIdentifier}</td>
                         		<td>${study.shortTitle}</td>
                         		<td>${study.status}</td>
                        		<c:set var="studyId" value="${study.id}" />
                         		<td > 
                         			<c:url var="url" value="/pages/study/edit" >
                         				<c:param name="studyId" value="${studyId}" />
                         			</c:url>
									 <a href="${url}"><strong>View\Edit</strong></a>
								</td>
                           </tr>
                         </c:forEach>
                     </table>
                </div>
          </form>      
  	</jsp:body>               
   </chrome:boxIPhone>
   <html>
  
         