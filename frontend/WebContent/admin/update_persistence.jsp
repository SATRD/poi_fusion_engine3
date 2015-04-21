<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.upate_persistence");%>
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
	    
    boolean error=true;
	String state="";
	boolean first =false;
	
	
	String relativeWebPath = "/config/config.xml";
	String configPath = getServletContext().getRealPath(relativeWebPath);
	
	Configuration configFile = new Configuration(configPath);	
	
	String dbName_old = configFile.getDBName();
	
		
 	//Get parameters 
    String dbHost = request.getParameter("dbHost");		
	String dbPort = request.getParameter("dbPort");
	String dbUser= request.getParameter("dbUser");	
	String dbPwd= request.getParameter("dbPwd");
	String dbName= request.getParameter("dbName");
	
	String firstTime = request.getParameter("firstTime");
	if ((firstTime !=null) && (firstTime.equalsIgnoreCase("yes"))) first = true;
	
	
	
	if ((dbHost !=null) && (!dbHost.isEmpty())) configFile.setHost(dbHost);
	if ((dbPort !=null) && (!dbPort.isEmpty())) configFile.setPort(dbPort);
	if ((dbUser !=null) && (!dbUser.isEmpty())) configFile.setUser(dbUser);
	if ((dbPwd !=null) && (!dbPwd.isEmpty())) configFile.setPwd(dbPwd);
	if ((dbName !=null) && (!dbName.isEmpty())) configFile.setDBName(dbName);
	
	
	
	error = !(configFile.save(configPath)); 
	if (error){
		state = "There was an error updating the configuration file";
	}
	else{		
		
		
		// If it is the firstTime or the dbName has changed we need to generate the database again
		if ((first) || (!dbName_old.equalsIgnoreCase(dbName))){
			
			//Create the DB, enable postgis extension and insert the initial tables
			OutputDB.setConfiguration(configPath); 
			
			
			
			
			// 1.Create the DB, enable postgis extension and insert the initial tables
			OutputDB.setConfiguration(configPath); 			
			String sqlscript2D = getServletContext().getRealPath(configFile.getDBScriptsDir()+configFile.getEnable2DScript());			
			boolean bool = OutputDB.createDBAndEnable2D(configFile, configFile.getDBName(),"postgres",sqlscript2D);		
					
						
			if (bool){
				
				// 2. Reset the DB (create the tables)
				String resetScriptFile = getServletContext().getRealPath(configFile.getDBScriptsDir()+configFile.getResetBaseScript()); 
				bool = OutputDB.resetDB(configFile, configFile.getDBName(),resetScriptFile);
				
				if (bool){
													
						//3. Insert the sources in the source table. This cannot be done previously (automatized SQL) as the path to the categorymapping
						// depends on the instalation directory
						String categoryMappingDir = getServletContext().getRealPath(configFile.getMappingDir());
						bool = OutputDB.insertOCDBaseSources(configFile, categoryMappingDir);
						
						if (bool){
							
							//4. Insert the Valencia city example
							String fusionDir = getServletContext().getRealPath(configFile.getFusionDir());
							bool = OutputDB.insertOCDBaseValenciaDemoCity(configFile,fusionDir);
							
							if (bool){		
									
									//We need to initialize the Valencia DB and dump all data from ocd_valencia_demo.sql
									// This is similar to initialize_ocd.jsp but we can go faster making the dump
									bool = OutputDB.createDBAndEnable2D(configFile, "ocd_valencia_demo","postgres",sqlscript2D);
									
									if (bool){
										
										String dumpScript = getServletContext().getRealPath(configFile.getDBScriptsDir()+"ocd_valencia_demo.sql");
										bool = OutputDB.dumpData(configFile.getHost(), "ocd_valencia_demo","postgres",dumpScript,null);
										
										if (bool){
											
											//Update the status of the OCD in OCD_BASE
											Connection con = OutputDB.connectDB(
											configFile.getConnectionString(),
											configFile.getUser(),
											configFile.getPwd(),
											configFile.getDriverName(),null);
											
											OCD ocd = OCD.getOCDClassByName(con, "valencia_demo", null);
											ocd.setStatus(OCD.STATUS_FINISHED_OK);
											bool = OCD.updateOCDStatus(con, ocd, null);
											
											OutputDB.disconnectDB(con, null);
											
											if (bool){
												
												//5. Insert the Tenerife city example. Repeat the same process as for Valencia
												
												bool = OutputDB.insertOCDBaseTenerifeDemoCity(configFile,fusionDir); 
											
											    if (bool){
											    	
											    	bool = OutputDB.createDBAndEnable2D(configFile, "ocd_tenerife_demo","postgres",sqlscript2D);
											    	
											    	if (bool){
											    	
											    		dumpScript = getServletContext().getRealPath(configFile.getDBScriptsDir()+"ocd_tenerife_demo.sql");
														bool = OutputDB.dumpData(configFile.getHost(), "ocd_tenerife_demo","postgres",dumpScript,null);
												    	
												    	if (bool){
															
												    		//Update the status of the OCD in OCD_BASE
															con = OutputDB.connectDB(
															configFile.getConnectionString(),
															configFile.getUser(),
															configFile.getPwd(),
															configFile.getDriverName(),null);
															
															ocd = OCD.getOCDClassByName(con, "tenerife_demo", null);
															ocd.setStatus(OCD.STATUS_FINISHED_OK);
															bool = OCD.updateOCDStatus(con, ocd, null);
															
															OutputDB.disconnectDB(con, null);
															
												
														    configFile.setFirstTime("no");
															configFile.save(configPath);
															state = "Configuration file successfully updated";
															error = false;
												    	}
											    	}
											    }
											}
										}
									}
							}							
						}
				}
			}
			
			
		}
			
		
		
	}
    
%>
    
  
  
  <center>
  <div class='main_content'>
    <form name='formulario' method='post' class='texto' enctype='multipart/form-data'>
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>Configuration file</font><br><br></center>
      <table border='0' cellspacing='15'>        
        <tr>
        	<td colspan='2' align='center' class='<% if (error) out.println("msgerror"); else out.println("msgcorrect");   %>'>
        		<%=state %>
        	</td>
        </tr>        
        <tr>
        	<td colspan='2' align='center'>
          		<br>
          		<input type='button'  value='Return' OnClick="location.href='../menu.jsp'" class='boton'>
        	</td>
        </tr>
      </table>
    </form>
  </div>
  </center>



  

</body>
</html>