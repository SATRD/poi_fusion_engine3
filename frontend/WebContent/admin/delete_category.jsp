<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.delete_category");%>
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
	        
	      	//delete the categorymapping file    	    	
		   	Category category = Category.getCategoryClassById(con,new Integer(id),null); 
	      	if (category!=null){
	      		if (category.getIcon()!= null){
			   		String iconFile = category.getIcon();
			   		File file = new File(iconFile);
			   		if (file.isFile()) file.delete();   
	      		}
	      	}
    	
    		boolean res = Category.deleteCategoryById(con, new Integer(id),"base",null);   		
    		
    		if (res){
    			error = 0;
    			state = "Category deleted succesfully";
    					   	
    			
    		}else{
    			error = 1;
    			state = "There was a problema deleting the data";     			
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
      <center><font class='titulo'>Data source</font><br><br></center>
      <table border='0' cellspacing='15'>        
        <tr>
        	<td colspan='2' align='center' class='<% if (error ==1) out.println("msgerror"); else out.println("msgcorrect");   %>'>
        		<%=state%>
        	</td>
        </tr>        
        <tr>
        	<td colspan='2' align='center'>
          		<br>
          		<input type='button'  value='Return' OnClick="location.href='list_categories.jsp'" class='boton'>
        	</td>
        </tr>
      </table>
    </form>
  </div>
  </center>



  

</body>
</html>