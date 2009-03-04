<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="rpIndex" required="true" type="java.lang.Integer" description="The index of the Report"%>
<%@attribute name="report" required="true" type="gov.nih.nci.cabig.caaers.domain.report.Report" description="The report that is printed by this row." %>
<c:set var="repcurrClass" value="${rpIndex %2 gt 0 ? 'odd' : 'even'}" />
<c:set var="lastVersion" value="${report.lastVersion}" />
<c:set var="reportStatus" value="${lastVersion.reportStatus}" />
<c:if test="${reportStatus ne 'REPLACED'}">
	<tr align="center" id="row${rpIndex}" class="${repcurrClass}">
		<td width="5%"><chrome:collapsableElement targetID="reptable${report.id}" collapsed="true" id="ID_02"/></td>
		<td align="left" width="15%">
			<c:if test="${!report.reportDefinition.amendable or report.isLatestVersion}">
				<c:if test="${report.aeReport.reportingPeriod.reportStatus != 'Report(s) Completed'}">
					<a href="<c:url value="/pages/ae/edit?aeReport=${report.aeReport.id}"/>">
						${report.reportDefinition.label}
					</a>
				</c:if>
				<c:if test="${report.aeReport.reportingPeriod.reportStatus == 'Report(s) Completed'}">
					${report.reportDefinition.label }
				</c:if>
			</c:if>
			<c:if test="${report.reportDefinition.amendable and !report.isLatestVersion}">
				${report.reportDefinition.label}
			</c:if>
		</td>
		<c:if test="${report.reportDefinition.amendable == true}">
		            		<td align="center" width="10%"><div class="label">${report.lastVersion.reportVersionId}</div></td>
		            	</c:if>
		            	<c:if test="${report.reportDefinition.amendable == false}">
		            		<td width="10%"/>
		            	</c:if>
		<td width="10%">${report.aeReport.numberOfAes}</td>
		<td width="20%" align="left">
			${command.reportsSubmittable[report.id] ? 'Complete' : 'In-progress'}
		</td>
		<td width="20%" id="status${report.id}" align="left">
			<ae:oneListReportSubmissionStatus theReport="${report}" reportStatus="${reportStatus}" lastVersion="${lastVersion}"/>
		</td>
		<td width="20%" id="action${report.id}" align="center">
			
			<SELECT style="width:100px;" id="actions-${report.id}" name="actions" onChange="executeAction(${report.id},'<c:url value='/pages/ae/generateExpeditedfPdf?aeReport=${report.aeReport.id}'/>')">
		     	<OPTION selected value="none">None</OPTION>
		     	<c:if test="${command.study.caaersXMLType}">
		     		<OPTION value="xml">caAERS XML</OPTION>
		     	</c:if>
		     	<c:if test="${command.study.adeersPDFType}">
		     		<OPTION value="pdf">AdEERS PDF</OPTION>
		     	</c:if>
		     	<c:if test="${command.study.medwatchPDFType}">
		     		<OPTION value="medwatchpdf">MedWatch 3500A PDF</OPTION>
		     	</c:if>
		     	<c:if test="${command.study.dcpSAEPDFType}">
		     		<OPTION value="dcp">DCP SAE PDF</OPTION>
		     	</c:if>
		     		<c:if test="${command.study.ciomsPDFType}">
		     	<OPTION value="cioms">CIOMS PDF</OPTION>
		     		</c:if>
		     	<c:if test="${command.study.ciomsSaePDFType}">
		     		<OPTION value="ciomssae">DCP Safety Report PDF</OPTION>
		     	</c:if>
	 		</SELECT>
			
	 		<br>
	 		<c:if test="${report.aeReport.notificationMessagePossible}">
	   	     <span class="notify-unit" id="notify-unit-${report.aeReport.id}">
	   	          <a id="notify-${report.aeReport.id}" class="notify" href="#">notify PSC</a>
	   	     <tags:indicator id="notify-indicator-${report.aeReport.id}"/>
	   	 </span>
	   	 </c:if>
			<c:if test="${command.reportsSubmittable[report.id]}">
				<c:if test="${!report.reportDefinition.amendable or report.isLatestVersion}"> 
					<c:choose>
						<c:when test="${reportStatus eq 'PENDING' or reportStatus eq 'FAILED'}">
							<a href="#" onClick="doAction('submit', ${report.aeReport.id},${report.id})">Submit</a><br>
							<a href="#" onClick="doAction('withdraw', ${report.aeReport.id},${report.id})">Withdraw</a>
						</c:when>
						<c:when test="${reportStatus eq 'COMPLETED' and (not empty lastVersion.submissionUrl)}">
							<a href="${lastVersion.submissionUrl}" target="_blank">View in AdEERS</a> <br>
							<a href="#" onClick="doAction('amend', ${report.aeReport.id},${report.id})">Amend</a>
						</c:when>
						<c:when test="${report.reportDefinition.amendable and (reportStatus eq 'WITHDRAWN' or reportStatus eq 'COMPLETED')}">
							<a href="#" onClick="doAction('amend', ${report.aeReport.id},${report.id})">Amend</a>
						</c:when>
						<c:when test="${reportStatus eq 'INPROGRESS'}">
							<a href="#" onClick="doAction('submit', ${report.aeReport.id},${report.id})">Resubmit</a>
						</c:when>
					</c:choose>
			  	</c:if>
			</c:if>
		</td>
	</tr>
	<tr id="reptable${report.id}" style="display:none;">
		<td/><td/>
		<td colspan=5>
			<div class="eXtremeTable">
				<table width="100%" border="0" cellspacing="0" class="rpAeTableRegion">
						<thead>
						<tr align="center" class="label">
							<td class="tableHeader" width="25%">AE Term</td>
							<td class="centerTableHeader" width="25%">Grade</td>
							<td class="tableHeader" width="25%">AE Start Date</td>
							<td class="tableHeader" width="25%">Requires Expedited Reporting?</td>
						</tr>
					</thead>
							
					<c:forEach items="${report.aeReport.adverseEvents}" var="ae" varStatus="statusAE">
						<ae:oneListAeRow index="${statusAE.index}" ae="${ae}" width="25%"/>
					</c:forEach>	
				</table>
			</div>
		</td>
	</tr>
</c:if>