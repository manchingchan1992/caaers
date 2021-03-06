<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@include file="/WEB-INF/views/taglibs.jsp"%>

<c:set var="MAX_COLS" value="${4}"/>
<html>
<head>
    <title>${tab.longTitle}</title>
    <tags:dwrJavascriptLink objects="createAE"/>
    
	<tags:slider renderComments="${command.associatedToWorkflow }" renderAlerts="${command.associatedToLabAlerts}" reports="${command.selectedReportsAssociatedToWorkflow}" 
		display="${(command.associatedToWorkflow or command.associatedToLabAlerts) ? '' : 'none'}" workflowType="report">
    	<jsp:attribute name="labs">
    		<div id="labs-id" style="display:none;">
    			<tags:labs labs="${command.assignment.labLoads}"/>
    		</div>
    	</jsp:attribute>
    </tags:slider> 
    <style type="text/css">
        hr.attrib-divider { border: 2px solid #6E81A6; /*margin: 0.4em 1em;*/ }
        .attribution { border-spacing: 0; border-collapse: collapse; /*margin: 1em 2em;*/ }
        .attribution col { border: 1px solid #6E81A6; }
        .attribution col.cause { width: 10em; }
        .attribution col.ae-attrib { width: 10em; }
        .attribution th { text-align: right; padding: 2px 5px; font-weight: normal; }
        .attribution tr.top th.ae, .attribution tr.fields td { border: solid #6E81A6; border-width: 0 1px; }
        .attribution tr.top th { text-align: center; vertical-align: top; }
        .attribution tr.top th.cause { vertical-align: bottom; }
        .attribution tr.top th .index { font-weight: normal; border-bottom: 1px solid #6E81A6; }
        .attribution tr.top th .grade { }
        .attribution tr.top th .term { font-weight: bold;/*font-size: 120%;*/ }
        .attribution tr.subhead th { text-align: left; font-weight: bold; /*font-size: 120%;*/ background-color: #D7D9E3; color: black; border-top: 1px solid #6E81A6; }
        .attribution td { text-align: center; padding: 2px; }
        .attribution tr.fields:hover th { /*background-color: #6E81A6;*/ /*color: white;*/ }
        .attribution tr.fields td, .attribution tr.fields th { border-bottom: 1px solid #6E81A6; }
        .attribution td.unrelated { background-color: #fff }
        .attribution td.unlikely  { background-color: #ccc }
        .attribution td.possible  { background-color: #A0BBF2 }
        .attribution td.probable  { background-color: #879ECC }
        .attribution td.definite  { background-color: #6E81A6 }
    </style>
    <script type="text/javascript">
    	ValidationManager.validate = false; //do not perform validation 
    	
    	var routingHelper = new RoutingAndReviewHelper(createAE, 'aeReport');
    
        function updateCodeClass(evt) {
            var attrib_level = $F(Event.element(evt))
            var td = Event.findElement(evt, "TD")
            if (td) {
                // deliberately overwriting all classes here -- this is so we don't have to
                // hard-code the class names to remove
                td.className = attrib_level.toLowerCase()
            }
        }

        Event.observe(window, "load", function() {
            $$(".attribution tr.fields select").each(function(sel) {
                updateCodeClass({ target: sel, srcElement: sel })
                sel.observe("change", updateCodeClass)
            })
            
             //only show the workflow tab, if it is associated to workflow
            var associatedToWorkflow = ${command.associatedToWorkflow};
            if(associatedToWorkflow){
            	<c:forEach items="${command.selectedReportsAssociatedToWorkflow}" var="report" varStatus="status">
	 	          	routingHelper.retrieveReviewCommentsAndActions('${report.id}');
 	          	</c:forEach>
            }
        })
    </script>
</head>
<body>

<tags:tabForm tab="${tab}" flow="${flow}" pageHelpAnchor="section16attribution">
    <jsp:attribute name="instructions">
    <tags:instructions code="instruction_ae_attribution" />
    <tags:instructions code="instruction_ae_attributionNote"  heading="Note: "/>
    </jsp:attribute>
    <jsp:attribute name="singleFields">
        <c:forEach var="offset" begin="0" end="${fn:length(command.aeReport.adverseEvents) - 1}" step="${MAX_COLS}">
            <ae:attributionTable adverseEvents="${command.aeReport.adverseEvents}" blocks="${blocks}"maxAEs="${MAX_COLS}" offset="${offset}"/>
        </c:forEach>
		<ae:reportingContext allReportDefinitions="${command.applicableReportDefinitions}" selectedReportDefinitions="${command.selectedReportDefinitions}" />
    </jsp:attribute>
</tags:tabForm>
</body>
</html>
