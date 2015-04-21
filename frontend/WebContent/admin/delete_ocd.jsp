<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.delete_ocd");%>
<%@ page import="org.apache.commons.io.output.*" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*, java.text.*" %>
<%@ page import="javax.servlet.http.*" %>

<%     
    int error = 1;
	String state = "Wrong parameters given";	
    
    String id = request.getParameter("id_selected");
    
    if (id !=null){        
       
	    	 //Connect to the database
	       	String relativeWebPath = "/config/config.xml";
			String configPath = getServletContext().getRealPath(relativeWebPath);
			Configuration configFile = new Configuration(configPath);
			
			Connection con = OutputDB.connectDB(
					configFile.getConnectionString(),
					configFile.getUser(),
					configFile.getPwd(),
					configFile.getDriverName(),null);
	        
	      	 	    	
		   	OCD ocd = OCD.getOCDClassById(con,new Integer(id),null); 
		   	
		   
	      	
		    
		   	
		   	//delete the fusionRules file   
	      	if (ocd!=null){
	      		
	      		//1. Get the name of the city and delete de ocd_<citydatabase>. We need to be root user for this. This will only apply for initialized cities and above
			   	boolean bool=true;
	      		if (!ocd.getStatus().equalsIgnoreCase(OCD.STATUS_NEW)) 	bool = OutputDB.deleteDB(configFile, "ocd_"+ocd.getName());
	      				
	      		if (bool){
	      			
	      			// 2. Delete the FusionRulesfile
			   		String fusionRules = ocd.getFusionRulesPath();
			   		File file = new File(fusionRules);
			   		if (file.isFile()) file.delete();   
			   		
			   		bool = OCD.deleteOCDById(con, new Integer(id),null);   		
		    		
		    		if (bool){
		    			error = 0;
		    			state = "OCD deleted succesfully";
		    					   	
		    			
		    		}else{
		    			error = 1;
		    			state = "There was a problema deleting the OCD";     			
		    		}	 
		    		 
	      		}else{
	      			error = 1;
	    			state = "There was a problema deleting the OCD ocd_"+ocd.getName();
	      		}
		   		
		   		
	      	}else{
	      		error = 1;
    			state = "There was a problema deleting the OCD. OCD does not exist";     
	      	}
		   	
	      	OutputDB.disconnectDB(con,null);
		   	
    	
    		
    } 
	  
    
    
%>
 <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href="../css/main.css">  
</head>
<body bgcolor="white" style="{ margin:0;}" background="../img/centerf2.png" style="{ background-repeat: no-repeat;}">
  
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