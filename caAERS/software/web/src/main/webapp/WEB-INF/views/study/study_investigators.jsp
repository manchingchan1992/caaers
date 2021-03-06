<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@ include file="/WEB-INF/views/taglibs.jsp" %>

<html>
<head>
    <title>${tab.longTitle}</title>
    <style type="text/css">
        .label {
            width: 12em;
            text-align: right;
        }
        input.autocomplete {
            width:300px;
        }
    </style>

    <tags:dwrJavascriptLink objects="createStudy"/>

<script language="JavaScript" type="text/JavaScript">
  var invListEditor;
  function fireDelete(selected, trClass){
		var confirmation = confirm("Do you really want to delete?");
		if(!confirmation) return; //return if not agreed.
	    fireAction('removeInv', selected);
  }

  function deactivate(index) {
      fireAction("deactivate", index);
  }

  function activate(index) {
      fireAction("activate", index);
  }

  function fireAction(action, selectedInvestigator){
	  AE.formFieldModified=false;
      if (action == 'addInv') {

      } else {
          if (action == 'removeInv')  ValidationManager.validate = false;
          var form = document.getElementById('command')
          form._target.name = '_noname';
          form._action.value = action;
          form._selectedInvestigator.value = selectedInvestigator;
          form.submit();
      }
  }

  var jsInvestigator = Class.create();
  Object.extend(jsInvestigator.prototype, {
           initialize: function(index, siteInvestigatorName) {
                this.index = index;
            	this.siteIndex = $F('studySiteIndex');
                this.siteInvestigatorName = siteInvestigatorName;
            	this.siteInvestigatorPropertyName = "study.activeStudyOrganizations["  + this.siteIndex + "].studyInvestigators[" + index + "].siteInvestigator";

                this.siteInvestigatorInputId = this.siteInvestigatorPropertyName + "-input";
            	if (siteInvestigatorName) $(this.siteInvestigatorInputId).value = siteInvestigatorName;
            	AE.createStandardAutocompleter(this.siteInvestigatorPropertyName, this.siteInvestigatorPopulator.bind(this), this.siteInvestigagorSelector.bind(this));
            },
      
            siteInvestigatorPopulator: function(autocompleter, text) {
         		createStudy.matchSiteInvestigator(text, this.siteIndex, function(values) {
         			autocompleter.setChoices(values)
         		})
        	},
        	
        	siteInvestigagorSelector: function(sInvestigator) { 
        			var image;
        	 	  if(sInvestigator.investigator.externalId != null){
                          image = '&nbsp;<img src="<chrome:imageUrl name="nci_icon_22.png"/>" alt="NCI data" width="17" height="16" border="0" align="middle"/>';
                  } else {
                          image = '';
                  }
                  
        		return (image + "" + sInvestigator.investigator.fullName);
        	}
        	
  });

  Event.observe(window, "load", function() {
                  
	//observe on the change event on study site dropdown.
	Event.observe('studySiteIndex',"change", function(event){
   	    selIndex = $F('studySiteIndex');
		fireAction('changeSite', selIndex);
	 });

	 //init the list editor
	 invListEditor = new ListEditor('ssi-table-row', createStudy, "Investigator",{
             addParameters: [],
             addFirstAfter: "ssi-empty-row",
             nextIndexCallback : function(){
     			return $('_ITEM_COUNT').value;
 			 },
             addCallback: function(nextIndex) {
                 if ($('ssi-table-row-TABLE')) {
                     $('ssi-table-row-TABLE').show();
                 }

		 	   $('_ITEM_COUNT').value = parseInt($('_ITEM_COUNT').value) + 1;
          	   new jsInvestigator(nextIndex);
          	   if ($('ssi-empty-row')){
                    Effect.Fade('ssi-empty-row');
               }
             }
         
    	});  
	 
  });

  function chooseSitesfromSummary(indx){
	var siteSelBox = $('studySiteIndex')
	siteSelBox.selectedIndex = indx + 1;
	fireAction('changeSite', indx);
  }
</script>
 
</head>
<body>
<study:summary />
<tags:tabForm tab="${tab}" flow="${flow}" formName="studyInvestigatorForm" hideErrorDetails="false" willSave="${not empty command.study.id}">
<jsp:attribute name="singleFields">
 <input type="hidden" name="_action" value="">
 <input type="hidden" name="_prevSite" value="${command.studySiteIndex}">
 <input type="hidden" name="_selectedInvestigator" value="">
 <input type="hidden" id="_ITEM_COUNT" name="_ITEM_COUNT" value="${fn:length(command.study.activeStudyOrganizations[command.studySiteIndex].studyInvestigators)}">

 <table border="0" id="table1" cellspacing="1" cellpadding="0" width="100%">
	<tr>
		<td width="65%" valign="top" >
		<p><tags:instructions code="study.study_investigators.top" /></p>
		<div class="value"><tags:renderInputs field="${fieldGroups.site.fields[0]}"/><tags:indicator id="ss-chg-indicator"/></div>
		<br />
		<hr>
		<div id="content-section">
            <span id="ssi-bookmark" />

            <csmauthz:accesscontrol var="_canModifyTheSite" scope="request" domainObject="${command.study.activeStudyOrganizations[command.studySiteIndex].organization}" authorizationCheckName="siteAuthorizationCheck" hasPrivileges="study_team_administrator" />
            <csmauthz:accesscontrol var="_canModifyTheCC" scope="request" domainObject="${command.study.studyCoordinatingCenter.organization}" authorizationCheckName="siteAuthorizationCheck" hasPrivileges="study_team_administrator" />
            <csmauthz:accesscontrol var="_canModifyTheFS" scope="request" domainObject="${command.study.primaryFundingSponsor.organization}" authorizationCheckName="siteAuthorizationCheck" hasPrivileges="study_team_administrator" />

            <c:if test="${command.studySiteIndex > -1 }">
				<study:oneStudySiteInvestigator index="${command.studySiteIndex}"/>
			</c:if>

            <c:if test="${_canModifyTheSite || _canModifyTheCC || _canModifyTheFS}">
                <div id="addInvBtn" style="${command.studySiteIndex > -1 ? '' : 'display:none'}"><tags:listEditorAddButton divisionClass="ssi-table-row" label="Add Investigator" /></div>
            </c:if>

		</div>
	    </td>
	  </tr>
     <tr>
         <td valign="top" width="35%">
                  <chrome:division title="Assigned Investigators" style="margin:5px;">
                          <c:forEach var="studySite" varStatus="status" items="${command.study.activeStudyOrganizations}">
                                   <csmauthz:accesscontrol var="_isATeamAdmin" domainObject="${studySite.organization}" authorizationCheckName="siteAuthorizationCheck" hasPrivileges="study_team_administrator" />
                                   <div class="" style="font-size: 11px;">
                                       <c:choose>
                                           <c:when test="${_isATeamAdmin || _canModifyTheCC || _canModifyTheFS}">
                                               <a style="cursor:pointer;" href="javascript:chooseSitesfromSummary(${status.index});" title="click here to edit investigator assigned to study">${studySite.organization.fullName}</a>
                                           </c:when>
                                           <c:otherwise>${studySite.organization.fullName}</c:otherwise>
                                       </c:choose>
                                       <b>(${fn:length(studySite.studyInvestigators)})</b>
                                   </div>
                           </c:forEach>
                          <div>
                             <img src="<c:url value="/images/chrome/spacer.gif" />?${requestScope.webCacheId}" width="1" height="150" />
                          </div>
                  </chrome:division>
  		</td>

     </tr>
	</table>
 </jsp:attribute>	

</tags:tabForm>
</body>
</html>
