<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.index");%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
   <title>Fusion Engine</title>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">   
   <meta http-equiv="Content-Script-Type" content="text/javascript">
   <link rel="icon" href="img/logo_ficontent_img.png" type="image/png" >
   <link rel='stylesheet' type='text/css' href="css/main.css">
   
   <script language="javascript">
   		function main(){
   			document.getElementById("iframe_fic2").src="menu.jsp";
   		}
   
   </script>
   
</head>

<body bgcolor="#2b2b2b">

  <center>
  <table  cellspacing="0" cellspacing="0" cellpadding="0" >

	<tr align="center" background="img/up.png" height="163" style="{ background-repeat: no-repeat;}" onclick="main()" title="Click here to go to the main menu">
		<td>&nbsp;</td>
	</tr>
	
	<tr height="800">	 
		<!--  The first time the DB parameters have to be entered -->
		<%    
	    
		//Load configuration parameters of the OCD in order to access the OutputDB database
			String relativeWebPath = "/config/config.xml";
			String configPath = getServletContext().getRealPath(relativeWebPath);
			//log.debug(configPath);
			Configuration conf = new Configuration(configPath);
			
			//log.debug(conf.toString());
			
			//log.debug(conf.getFirstTime());
			
			if (conf.getFirstTime().equalsIgnoreCase("yes")){
		    
		%>
			<td> <iframe src="admin/persistence.jsp?firstTime=yes" width="1024" height="800" scrolling="no" name="iframe_fic2" id="iframe_fic2"></iframe></td>
		
		<%} else{ %>	
	 
	  		<td> <iframe src="menu.jsp" width="1024" height="800" scrolling="yes" name="iframe_fic2" id="iframe_fic2"></iframe></td>
	  	<%} %>
	</tr>
  </table>
  </center>
</body>


</html>

