<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>Review and Submit</title>
<script>

function submitPage(s){
	document.getElementById("nextView").value=s;
	document.getElementById("command").submit();
}

</script>

<style>
    div.row div.label {
        width: 15em;
        padding-right: 0.5em;
    }
</style>

</head>
<body>
 <p><tags:instructions code="instruction_subject_enter.review"/></p>
<tags:tabForm tab="${tab}" flow="${flow}" title="${command.participant.lastName}, ${command.participant.firstName}" >
    <jsp:attribute name="singleFields">
        <input type="hidden" id="_finish" name="_finish"/>

        <table cellpadding="0" border="0" cellspacing="4" width="100%">
            <tr valign="top">
                <td valign="top" width="50%" align="left">
                    <chrome:division title="Subject Details" collapsable="true" id="SD_01">
                        <div class="row">
                            <div class="label">First name</div>
                            <div class="value">${command.participant.firstName}</div>
                        </div>

                        <div class="row">
                            <div class="label">Last name</div>
                            <div class="value">${command.participant.lastName}</div>
                        </div>

                        <div class="row">
                            <div class="label">Middle name</div>
                            <div class="value">${command.participant.middleName}</div>
                        </div>

                        <div class="row">
                            <div class="label">Maiden name</div>
                            <div class="value">${command.participant.maidenName}</div>
                        </div>


                        <div class="row">
                            <div class="label">Birth date</div>
                            <div class="value">${command.participant.dateOfBirth}</div>
                        </div>

                        <div class="row">
                            <div class="label">Ethnicity</div>
                            <div class="value">${command.participant.ethnicity}</div>
                        </div>

                        <div class="row">
                            <div class="label">Race</div>
                            <div class="value">${command.participant.race}</div>
                        </div>

                        <div class="row">
                            <div class="label">Gender</div>
                            <div class="value">${command.participant.gender}</div>
                        </div>
                    </chrome:division>
                </td>
            </tr>

            <tr>
                <td valign="top" width="50%">
                    <chrome:division title="Study Details" collapsable="true" id="SD_02">
                        <c:set var="studySite" value="${command.studySite}"/>

                        <div class="row">
                            <div class="label">Study primary ID</div>
                            <div class="value">${studySite.study.primaryIdentifier ne null ? studySite.study.primaryIdentifier.value : ''}</div>
                        </div>

                        <div class="row">
                            <div class="label">Study title</div>
                            <div class="value">${studySite.study.shortTitle}</div>
                        </div>
                        <div class="row">
                            <div class="label">Study long title</div>
                            <div class="value">${studySite.study.longTitle}</div>
                        </div>

                        <div class="row">
                            <div class="label">Site</div>
                            <div class="value">
 							<c:if test ="${studySite.organization.externalId != null}">
								<img src="<chrome:imageUrl name="nci_icon_22.png"/>" alt="NCI data" width="17" height="16" border="0" align="middle"/>
							</c:if>                           
                            ${studySite.organization.name}</div>
                        </div>
                        <%--<img src="<chrome:imageUrl name="spacer.gif"/>" width="1" height="1" class="heightControl">--%>

                        <div class="row">
                            <div class="label">Study subject identifer</div>
                            <div class="value">${command.studySubjectIdentifier}</div>
                        </div>

                    </chrome:division>
                </td>
            </tr>
        </table>
        
    </jsp:attribute>
</tags:tabForm>


</body>
</html>
