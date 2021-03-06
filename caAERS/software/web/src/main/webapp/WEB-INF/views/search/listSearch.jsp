<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@include file="/WEB-INF/views/taglibs.jsp"%>

<html>
<head>
<title>Manage Reports</title>
<script>
YAHOO.example.Data = {

    rsList: [
<c:forEach items="${savedSearchList}" var="ss" varStatus="status">
        {
            //ssName: "${ss.name}",
            ssName: '<a href="<c:url value="/pages/search/advancedSearch?searchName=' + '${caaers:escapeJS(ss.name)}' + '"/>">' + '${caaers:escapeJS(ss.name)}' + '</a>',
            ssDescription: '${caaers:escapeJS(ss.description)}',
            ssCreatedDate: '${caaers:escapeJS(ss.createdDate)}',
            ssAction: '<a href="<c:url value="/pages/search/advancedSearch?searchName=' + '${caaers:escapeJS(ss.name)}' + '&_target2=2&_page=1&runSavedQuery=true"/>">Run</a>'
        }
        <c:if test="${!status.last}">,</c:if>
</c:forEach>

    ]
};

    /////////////////////////////////

YAHOO.util.Event.addListener(window, "load", function() {
    YAHOO.example.CustomSort = function() {

        var myColumnDefs = [
            {key:"ssName",             label:"Name",         sortable:true,      resizeable:true},
            {key:"ssDescription",       label:"Description",           sortable:true,      resizeable:true},
            {key:"ssCreatedDate",      label:"Saved on",       sortable:true,      resizeable:true},
            {key:"ssAction",           label:"Action",              sortable:true,      resizeable:true}
        ];

        var myDataSource = new YAHOO.util.DataSource(YAHOO.example.Data.rsList.slice(0,50));
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["ssName", "ssDescription", "ssCreatedDate", "ssAction"]
        };

        //Create config
        var oConfigs = {
				initialRequest: "results=50",
				draggableColumns:false
			};
        var myDataTable = new YAHOO.widget.DataTable("basic", myColumnDefs, myDataSource, oConfigs);

        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
    }();
});
    </script>
</head>
<body>
        <p><tags:instructions code="advancedSearch.manageSearch" /></p>
	    <div id="basic" class="yui-skin-sam"></div>
</body>
</html>
