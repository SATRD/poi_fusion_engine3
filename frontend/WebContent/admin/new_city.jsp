<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.new_city");%>
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
         	
    	  	//TODO Check that it is a valid bounding box 
        	if  (document.formulario.bbox.value != ""){ 
    	  
    	  		document.formulario.submit();     
        	}else 
        		 alert("Insert a Bbox");
      }
      else
        alert("Insert a name");
    }
    
    

  </script>
</head>
<body bgcolor="white" style="{ margin:0;}" background="../img/centerf2.png" style="{ background-repeat: no-repeat;}">

 
  
  
  <center>
  <div class='main_content'>
    <form name='formulario' method='post' class='texto' action="save_city.jsp">
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>New City</font><br><br></center>
      <table border='0' cellspacing='15'>        
        <tr>
        	<td width=100px>Name</td>
        	<td width=200px> <input type='text' name='name' style='width: 400px;' /> </td>
        </tr>
        
        <tr>
        	<td width=100px>Bbox</td>
        	<td width=200px> <input type='text' name='bbox' style='width: 400px;' /> </td>
        </tr>   
        
         <tr>
        	<td colspan='2' align='center'>
          		<br>
          		<input type='button'  value='Return' OnClick="location.href='list_cities.jsp'" class='boton'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          		&nbsp;&nbsp;&nbsp;<input type='button' onclick='check_submit()' value='Save' class='boton'>
        	</td>
        </tr>     
        
       
      </table>
    </form>
  </div>
  </center>
  

</body>
</html>