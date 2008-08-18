<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="commons" uri="http://bioinformatics.northwestern.edu/taglibs/commons"%>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>

<html>

<head>
    <title>caAERS diagnosis , happy.jsp</title>

	<link rel="stylesheet" type="text/css"/>

    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

	<style type="text/css">

       .label { width: 12em; padding: 1px;  margin-right: 0.5em; } 

       div.row div.value  {white-space:normal;}
       
       .pane {
  			float : none
		}
		
		div.row div.pass {
		  line-height: 1.5em;
		  font-weight: bold;
		  color: green;
		}
		div.row div.fail {
		  line-height: 1.5em;
		  font-weight: bold;
		  color: red;
		}
		div.row div.error {
		  line-height: 1.5em;
		  color: red;
		}
	</style>
</head>
<body>

<div id="main">
 		
	<div class="pane">
     <div class="box">
		<div class="header"><div class="background-L"><div class="background-R">
			<h2>caAERS Diagnostics</h2>
		</div>
	 </div>
	</div>
   <div class="division ">   
      <div class="">  
	      <div class=""><h3>Configuration</h3> </div>
        	<div class="leftpanel">
        		<div class="row">
                	<div class="label">PSC URL </div>
                	<div class="value">${command.pscUrl}</div>
            	</div>

           		<div class="row">
                	<div class="label">LabViewer URL</div>
                	<div class="value">${command.labViewerUrl} </div>
            	</div>

            	<div class="row">
                	<div class="label">caExchange URL</div>
                	<div class="value">${command.caExchangeUrl}</div>
            	</div>            	
            </div>
	  </div>
  </div>
  <div class="division ">   
      <div class="content">  
	      <div class="header"><h3>Email Configuration</h3> </div>
      
        	<div class="leftpanel">
        		<div class="row">
                	<div class="label">SMTP Host</div>
                	<div class="value">${command.smtpHost}</div>
            	</div>
           		<div class="row">
                	<div class="label">SMTP Port</div>
                	<div class="value">${command.smtpPort}</div>
            	</div>
            	
			<c:if test="${command.smtpTestResult}">
				<div class="row">
                	<div class="label">Test Connection</div>
                	<div class="pass">Pass</div>
            	</div> 
			</c:if>
            <c:if test="${not command.smtpTestResult}">
				<div class="row">
                	<div class="label">Test Connection</div>
                	<div class="fail">Fail</div>
            	</div> 
            	<div class="row">
                	<div class="label">StackTrace</div>
                	<div class="error">${command.smtpError}</div>
            	</div> 
			</c:if>	
           	
            </div>
	  </div>
  </div>
  <div class="division ">   
      <div class="content">  
	      <div class="header"><h3>caExchange handshake</h3> </div>
      
        	<div class="leftpanel">
        		<div class="row">
                	<div class="label">Message to PSC</div>
                	<div class="fail">Fail </div>
            	</div>
        		<div class="row">
                	<div class="label">Error</div>
                	<div class="error">Connection time out .... </div>
            	</div>				
           		<div class="row">
                	<div class="label">Message to C3PR</div>
                	<div class="pass">Pass </div>
            	</div>           	
            </div>
	  </div>
  </div>
  <div class="content buttons autoclear"/>
</div>
</body>
</html>
