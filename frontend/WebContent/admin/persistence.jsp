<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="org.upv.satrd.fic2.fe.config.*" %>

<%	
	String relativeWebPath = "/config/config.xml";
	String configPath = getServletContext().getRealPath(relativeWebPath);
	
	Configuration configFile = new Configuration(configPath);
	
	
	String firstTime = request.getParameter("firstTime");
	
	String target_jsp = "update_persistence.jsp";
	if ((firstTime != null) && (firstTime.equalsIgnoreCase("yes"))) target_jsp="update_persistence.jsp?firstTime=yes";
	
	


%>


<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href="../css/main.css">
  <link rel='stylesheet' type='text/css' href="../css/table.css"> 
  <script type='text/javascript' language='javascript'>
  
  
  function check_submit(){        

      		//TODO: Check that all fields are valid (not empty, numbers, etc.)         
        	  document.formulario.submit();          
       
    }

  </script>

</head>
<body bgcolor="white" style="{ margin:0;}" background="../img/centerf2.png" style="{ background-repeat: no-repeat;}">

  
  <center>
  <div class='main_content'> 
  
 <% 			
	if (configFile.getFirstTime().equalsIgnoreCase("yes")){
		    
%>
	<h5 style="color: #FF0000; font-size: 10pt">This is the first time you access the Fusion Engine, so you need to configure some parameters.</br>
	Be sure you have POSTGIS 2.x installed and provide the required database information.</br>
	The POSTGIS root account is needed to create new databases (for each new OCD).</br>
	Fill in the form below and press 'Save'.</h5>
		
<%}else{ %>  

	<h5 style="color: #FF0000; font-size: 10pt">Changing database parameters is extremely discouraged. </br>
	Changing the name of the database will result in loosing all data, similar as by the time you deployed the WAR for the first time.</br>
	You may change the port and the root credentials if your database configuration has changed.</br>
	If you change the database  host(IP address) be sure that you have previously exported the ocd_base and all related databases to this new host .</h5>
		


<%} %>  
  
  <font class='titulo'>Configuration parameters</font>
  <form name='formulario' method='post' class='texto' action="<%=target_jsp%>">
  
   <table border="0" valign="middle" cellspacing="10">       
    	      		
    	      			<tr>
    	      				<td width="130px">Host</td>
    	      				<td width="200px">
    	      					<input type="text" name="dbHost" style="width: 230px;" value="<%=configFile.getHost() %>" />
                			</td>
                		</tr>
						<tr>
							<td>Port</td>
							<td>
                  				<input type="text" name="dbPort" style="width: 100px;" value="<%=configFile.getPort() %>" />
                			</td>
                		</tr>
                		<tr>
                			<td>Root credentials</td>
                			<td>
                  				<input type="text" name="dbUser" style="width: 100px;" value="<%=configFile.getUser() %>" /> 
		  						<input type="password" name="dbPwd" style="width: 100px;" value="<%=configFile.getPwd() %>" />
                			</td>
                		</tr>
                		<tr>
							<td>DB name</td>
							<td>
                  				<input type="text" name="dbName" style="width: 100px;" value="<%=configFile.getDBName() %>" />
                  				<!--  <input type="button" name="reset" onclick="location.href='reset_fe_base.jsp'" value="Reset"/>-->
                			</td>
                		</tr> 
               
 
    </table>
    
    <br>
    <% 			
	if (!configFile.getFirstTime().equalsIgnoreCase("yes")){
		    
%>
	<input type="button"  value="Return" OnClick="location.href='configuration.jsp'" class="boton" style="width: 160px;">
		
<%} %>  
    
    
    
    
    <input type="button"  value="Save" OnClick="check_submit()" class="boton" style="width: 160px;">

</form>
   </div> 
  </center>

</body>
</html>