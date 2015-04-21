<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.upv.satrd.fic2.fe.db.*, 
		org.upv.satrd.fic2.fe.config.*,
		org.upv.satrd.fic2.fe.main.*, 
		java.sql.*,java.util.*,
		org.json.*,
		com.google.gson.*  " %>

<%    
	    
//Load configuration parameters of the OCD in order to access the OutputDB database    
 
    String relativeWebPath = "/config/config.xml";
	String configPath = getServletContext().getRealPath(relativeWebPath);
	Configuration conf = new Configuration(configPath);
	
	OutputDB.setConfiguration(configPath); 
	
	//Get Connection	
	Connection con = OutputDB.connectDB(
		conf.getConnectionString(),
		conf.getUser(),
		conf.getPwd(),
		conf.getDriverName(),null);
	
	ArrayList<OCD> ocdList = OCD.getOCDListFinished(con,null);   
	OutputDB.disconnectDB(con, null);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href="../../../css/main.css">   
</head>
<body bgcolor="white" style="{ margin:0;}" background="../../../img/centerf2.png" style="{ background-repeat: no-repeat;}">   
  
  
  <center>
  <!--  <div class='main_content'>-->
    <form name='formulario' method='post' class='texto'  action="left2.jsp">
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>Config</font><br><br></center>
      <table border='0' cellspacing='15'>        
        
        
        <tr>
        	<td>&nbsp;</td>
        </tr>
        <tr>
        	<td>Select a OCD</td>
        </tr>
        <tr>        	
        	<td>
          		<select name="ocd" id="ocd" style='width: 200px;' >
<% 
					    for(int j = 0; j < ocdList.size(); j = j+1) {
					    	OCD ocd = ocdList.get(j);
%>
	                	<option value="<%=ocd.getName()%>"><%=ocd.getName()%> </option>
<%						} %>	            
	          </select>
       		</td>
       	</tr>
       	
       	
        
        <tr>
        	<td align='center'>
          		
          		<input type='submit' value='Next' class='boton'>
        	</td>
        </tr>
      </table>
    </form>
  <!--  </div>-->
  </center>

 

  

</body>
</html>