<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

 <form:form  method="post" modelAttribute="participantCommand">
<input type="hidden" name="CSRF_TOKEN" value="${CSRF_TOKEN }"/>

<chrome:division title="Participant Details">



        <form:input path="${participantCommand.firstName}" id="participantCommand.firstName"></form:input>

                <form:input path="${participantCommand.lastName}" id="participantCommand.lastName"></form:input>

    <div>


    </div>

<chrome:division title="Subject Details">
    <table id="test2" class="single-fields" width="100%">
        <tr>
            <td>
                <c:forEach begin="0" end="3" items="${fieldGroups.participant.fields}" var="field">
                    <tags:renderRow field="${field}"/>
                </c:forEach>
            </td>
            <td>
                <div class="row" id="participant.dateOfBirth-row">
                    <div class="label"><tags:renderLabel field="${fieldGroups.participant.fields[4]}"/></div>
                    <div class="value"><tags:renderInputs field="${fieldGroups.participant.fields[4]}"/></div>
                </div>
                <c:forEach begin="5" end="7" items="${fieldGroups.participant.fields}" var="field">
                    <tags:renderRow field="${field}"/>
                </c:forEach>
            </td>
        </tr>

    </table>

    <td><a href="${flowExecutionUrl}&_eventId=save">Save</a></td>


</chrome:division>


</chrome:division>
    </form:form>



