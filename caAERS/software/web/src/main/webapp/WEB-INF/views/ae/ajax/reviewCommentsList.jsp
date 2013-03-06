<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<tags:noform>
	<c:set var="noOfComments" value="${fn:length(command.previousComments) - 1}">
	</c:set>
	<c:set var="editImage"><c:url value="/images/edit.png"/></c:set>
	<c:set var="deleteImage"><c:url value="/images/checkno.gif"/></c:set>
	<div>
		<div class="eXtremeTable" >
		<c:forEach items="${command.previousComments}" var="comment" varStatus="cIndex">
					<div class="${cIndex.index % 2 gt 0 ? 'odd' : 'even' } autoclear cmt" >
						<c:set var="dt"><tags:formatTimeStamp value="${comment.createdDate}" /></c:set>
						<c:choose>
						<c:when test="${comment.editable == true and fn:contains(comment.userId, command.userId)}">
							<chrome:division title='${comment.autoGeneratedText } on ${dt } <a href="javascript:editComment(${comment.id}, ${command.entityId })">(<img src="${editImage}" alt="edit" />edit)</a>
							 <a href="javascript:deleteComment(${comment.id}, ${command.entityId })" alt="delete" ><img src="${deleteImage}" /></a>'
							 collapsable="true" id="cmt${cIndex.index}-${command.entityId }" collapsed="${cIndex.index lt noOfComments}">
								<pre id="userComment-${comment.id}">${comment.userComment}</pre>
							</chrome:division>
						</c:when>
						<c:otherwise>
							<chrome:division title='${comment.autoGeneratedText } on ${dt }' collapsable="true" id="cmt${cIndex.index}" collapsed="${cIndex.index lt noOfComments}">
								<pre id="userComment-${comment.id}">${comment.userComment}</pre>
							</chrome:division>
						</c:otherwise>
						</c:choose>
					</div>
				</c:forEach>
			</div>
	</div>
</tags:noform>
