<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.edit_apitype");%>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href="../css/main.css"> 
  <script type='text/javascript' language='javascript'>

  function check_submit(){        

      if (document.formulario.name.value != ""){
        	  document.formulario.submit();   
      }
      else
        alert("Insert a name");
    }
    
    

  </script>
</head>
<body bgcolor="white" style="{ margin:0;}" background="../img/centerf2.png" style="{ background-repeat: no-repeat;}">

<%    


	String id_selec = request.getParameter("id_selected");
	

	String relativeWebPath = "/config/config.xml";
	String configPath = getServletContext().getRealPath(relativeWebPath);
	Configuration configFile = new Configuration(configPath);
	
	Connection con = OutputDB.connectDB(
			configFile.getConnectionString(),
			configFile.getUser(),
			configFile.getPwd(),
			configFile.getDriverName(),null);
    
        
        
    //Get the corresponding apitype and its associated values 
    	
   	APIType apitype = APIType.getAPITypeClassById(con,new Integer(id_selec),null);
   	String id = apitype.getId().toString();
   	String name = apitype.getName();
   	String description = apitype.getDescription();
   	if (description == null) description = "";
   	   	
   	
   	
   	
   	String apirules; //it is an optional field and may be empty
   	if (apitype.getAPIRules() == null){
   		apirules = " [empty] ";
   	}else{ 
   		apirules = apitype.getAPIRules();
   		//show just the filename, hide the directory where the file is
   		apirules = apirules.substring( apirules.lastIndexOf("/")+1 );
   		
   		//TODO: Add a button to open the content in a window or editor
   	}  
    
%>
    
  
  
  <center>
  <div class='main_content'>
    <form name='formulario' method='post' class='texto' enctype='multipart/form-data' action="update_apitype.jsp">
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>Edit APIType</font><br><br></center>
      <table border='0' cellspacing='15'>        
        <tr>
        	<td width=100px>Name</td>
        	<td width=200px> <input type='text' name='name' value="<%=name%>" style='width: 400px;' readonly/> </td>
        </tr>
        <tr>
        	<td valign="top">Description</td>
        	<td>
          		<textarea name="desc" style="width: 400px; height:55px; resize:none;"><%=description%></textarea>
        	</td>
        </tr>
        
              
        <tr>        
        	<td>API Rules file</td> 
        		<td>
          			<input type='file' name='icon' width=" 400px" ><%=apirules%>
          			
          			
        		</td>
        </tr>
        
        
        
        <tr>
        	<td colspan='2' align='center'>
          		<br>
          		<input type='button'  value='Return' OnClick="location.href='list_apitypes.jsp'" class='boton'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          		&nbsp;&nbsp;&nbsp;<input type='button' onclick='check_submit()' value='Update' class='boton'>
        	</td>
        </tr>
      </table>
      <input type="hidden" name="id_selec" value="<%=id_selec%>">
    </form>
  </div>
  </center>

 <%  OutputDB.disconnectDB(con,null); %>

  

</body>
</html>