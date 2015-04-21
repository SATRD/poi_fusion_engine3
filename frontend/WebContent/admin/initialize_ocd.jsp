<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.initialize_ocd");%>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*, java.text.*" %>
<%@ page import="javax.servlet.http.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href="../css/main.css">  
</head>
<body bgcolor="white" style="{ margin:0;}" background="../img/centerf2.png" style="{ background-repeat: no-repeat;}">

<%    
	    
	String relativeWebPath = "/config/config.xml";
	String configPath = getServletContext().getRealPath(relativeWebPath);
	Configuration configFile = new Configuration(configPath);
	
	
	Connection con1 = OutputDB.connectDB(
			configFile.getConnectionString(),
			configFile.getUser(),
			configFile.getPwd(),
			configFile.getDriverName(),null);
    
	    
    
    int error = 1;
	String state = "";
    
    
    String ocdid= request.getParameter("id_selected");     
	
	OCD ocd = OCD.getOCDClassById(con1, new Integer(ocdid),null);
	
	OutputDB.disconnectDB(con1,null);	
	
	
	if ((ocd!=null) && (ocd.getStatus().equalsIgnoreCase(OCD.STATUS_NEW)) ){
		
								
		// 1.Create the DB, enable postgis extension 
		OutputDB.setConfiguration(configPath); 			
		String sqlscript2D = getServletContext().getRealPath(configFile.getDBScriptsDir()+configFile.getEnable2DScript());			
		boolean bool = OutputDB.createDBAndEnable2D(configFile, "ocd_"+ocd.getName(),"postgres",sqlscript2D);	
		
		
		if (bool){
			
			// 2. Reset the DB (create the tables)
			String resetScriptFile = getServletContext().getRealPath(configFile.getDBScriptsDir()+configFile.getResetOCDScript()); 
			log.debug(resetScriptFile);
			bool = OutputDB.resetDB(configFile, "ocd_"+ocd.getName(),resetScriptFile);
				
			if (bool){	
									
					//3 Copy all ocd related data from ocd_base to this new database					
					bool = OutputDB.initializeOCD(configFile,ocd); 

								
					if (bool){			
						error = 0;
						state = "OCD "+ocd.getName()+" successfully initialized";
					}else{
						error = 1;
						state = "There was an error initializing the ocd";
					}
			}
		}
	}else{
		
		error = 1;
		state = "The selected OCD cannot be initializated. Check the status";
	}
	
		
	 
    
%>
    
  
  
  <center>
  <div class='main_content'>
    <form name='formulario' method='post' class='texto' enctype='multipart/form-data'>
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>OCD</font><br><br></center>
      <table border='0' cellspacing='15'>        
        <tr>
        	<td colspan='2' align='center' class='<% if (error ==1) out.println("msgerror"); else out.println("msgcorrect");   %>'>
        		<%=state%>
        	</td>
        </tr>        
        <tr>
        	<td colspan='2' align='center'>
          		<br>
          		<input type='button'  value='Return' OnClick="location.href='list_ocds.jsp'" class='boton'>
        	</td>
        </tr>
      </table>
    </form>
  </div>
  </center>
</body>
</html>