<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.update_city");%>
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
    
    //Get all values from the posted form (name, des, access, mapping, mapping2,delete2, api,city,license)
    // request.getParameter will return null as it is a multipart message
    String name= request.getParameter("name");     
	String Bbox=request.getParameter("bbox");
	String id = request.getParameter("id_selec");
	
	
 	
    try{  
    	
    	        
        if ((Bbox!=null)&& (!Bbox.isEmpty()) ){	
        	
	        City city = City.getCityClassById(con, new Integer(id),null);
	        
	        if (city!= null){
	        	
	        	city.setBbox(Bbox);
        	
	    		boolean res = City.updateCity(con, city,null);   
	    		
	    		
	    		if (res ){
	    			error = 0;
	    			state = "Data stored properly";
	    		}else{
	    			error = 1;
	    			state = "There was a problem updating the city";
	    			
	    		}
	        }else{
	        	error = 1;
    			state = "There was a problem updating the city";
    			
	        }
	    			
	    	
        }     	
        	
        
    }catch(Exception ex) {
        System.out.println(ex);
        
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
          		<input type='button'  value='Return' OnClick="location.href='list_cities.jsp'" class='boton'>
        	</td>
        </tr>
      </table>
    </form>
  </div>
  </center>

 <%  OutputDB.disconnectDB(con,null); %>

  

</body>
</html>