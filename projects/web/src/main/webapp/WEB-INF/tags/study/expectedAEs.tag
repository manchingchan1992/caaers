<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="caaers" uri="http://gforge.nci.nih.gov/projects/caaers/tags" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@taglib prefix="study" tagdir="/WEB-INF/tags/study" %>

<style>
    #termsTable .even {
    }

    #termsTable .odd {
        /*background-color: #e8e8ff;*/
    }
</style>

<chrome:division title="" collapsable="true" id="studyTermsID">
    <c:if test="${not empty command.aeTerminology.meddraVersion}">
        <c:set var="terms" value="${command.expectedAEMeddraLowLevelTerms}" />
		<p><tags:instructions code="study_expectedaes_meddra"/></p>
    </c:if>

    <c:if test="${not empty command.aeTerminology.ctcVersion}">
        <c:set var="terms" value="${command.expectedAECtcTerms}" />
		<p><tags:instructions code="study_expectedaes_ctc"/></p>
    </c:if>
               <tags:aeTermQuery
                       isMeddra="${not empty command.aeTerminology.meddraVersion}"
                       noBackground="true"
                       callbackFunctionName="rpCreator.addStudyTerm"
                       ignoreOtherSpecify="false"
                       isAjaxable="true"
                       version="${not empty command.aeTerminology.meddraVersion ? command.aeTerminology.meddraVersion.id : command.aeTerminology.ctcVersion.id}"
                       title="">
               </tags:aeTermQuery>

               <table id="termsTable" width="100%" class="tablecontent">
                   <tr>
                       <th scope="col" align="left"><b>Term Full Name</b></th>
                       <th scope="col" align="right"><b></b></th>
                   </tr>
                   <tbody id="termsDiv">
                           <c:forEach items="${terms}" var="studyTerm" varStatus="status">
                               <tr class="ae-section ${status.index % 2 gt 0 ? 'odd' : 'even'}" id="STUDY_TERM_-${status.index}" >
                                   <study:oneExpectedAE isOtherSpecify="${studyTerm.otherRequired}" index="${status.index}" studyTerm="${studyTerm}"/>
                                   <td style="text-align:center;"><img src="<c:url value="/images/checkno.gif" />" id="DELETE_<c:out value="${status.index}" />" onclick="removeTerm(${status.index})" style="cursor:pointer;""></td>
                               </tr>
                           </c:forEach>
                    <tr id="observedBlankRow" />
                   </tbody>
               </table>

    </chrome:division>
