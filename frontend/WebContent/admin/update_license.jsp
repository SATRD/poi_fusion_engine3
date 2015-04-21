<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.update_license");%>
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
	
	Connection con = OutputDB.connectDB(
			configFile.getConnectionString(),
			configFile.getUser(),
			configFile.getPwd(),
			configFile.getDriverName(),null);
    
	    
    
    int error = 1;
	String state = "";
    
    //Get all values from the posted form     
    String name= request.getParameter("name");     
	String description=request.getParameter("desc");
	String info=request.getParameter("info");	
	String id = request.getParameter("id_selec");
	
	
			
	
 	
    try{         
        
        if ((name!=null)&& (!name.isEmpty()) ){	
           
        //search if the new name is already there in the DB	
	       License license1 = License.getLicenseClassByName(con,name,null);  
	       
	       License license2 = License.getLicenseClassById(con,new Integer(id),null);
	        
	       //String name_db = license2.getName();
	        
	        boolean name_keeps = license2.getName().equalsIgnoreCase(name);
	        
	        //if (license1 == null || name_keeps){
	        if ((license1 == null) || name_keeps   ){
	        	
	        	license2.setName(name);
	        	license2.setDescription(description);
	        	license2.setInfo(info);
	        	
	        	
	    		boolean res = License.updateLicense(con, license2,null);  
	    		
	    		
	    		if (res){
	    			error = 0;
	    			state = "License updated properly";
	    		}else{
	    			error = 1;
	    			state = "There was a problem updating the license";
	    			//delete the file. No need to store it
	    			
	    			
	    		}
	    	}else{
	    		error = 1;
	    		state = "Error: The license already exists";  
	    		
	    	}	
        }     	
        	
        
    }catch(Exception ex) {
        System.out.println(ex);
        log.error(ex);
        
    }
	
    
    

    
    
    
%>
    
  
  
  <center>
  <div class='main_content'>
    <form name='formulario' method='post' class='texto' enctype='multipart/form-data'>
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>License</font><br><br></center>
      <table border='0' cellspacing='15'>        
        <tr>
        	<td colspan='2' align='center' class='<% if (error ==1) out.println("msgerror"); else out.println("msgcorrect");   %>'>
        		<%=state%>
        	</td>
        </tr>        
        <tr>
        	<td colspan='2' align='center'>
          		<br>
          		<input type='button'  value='Return' OnClick="location.href='list_licenses.jsp'" class='boton'>
        	</td>
        </tr>
      </table>
    </form>
  </div>
  </center>

 <%  OutputDB.disconnectDB(con,null); %>

  

</body>
</html>