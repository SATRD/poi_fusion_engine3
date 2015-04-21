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
	
    String ocdName = request.getParameter("ocd");
 
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
	
	
	
	
	OCD ocd = OCD.getOCDClassByName(con,ocdName,null);
	
	//disconnnect from ocd_base and connect to this ocd
	OutputDB.disconnectDB(con, null);
	
	conf.setDBName("ocd_"+ocd.getName());
	con = OutputDB.connectDB(
			conf.getConnectionString(),
			conf.getUser(),
			conf.getPwd(),
			conf.getDriverName(),null);	
	
	
	ArrayList<Category> categoryList = Category.getCategoryList(con,null);
    
%>





<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href="../../../css/main.css"> 
  <script type='text/javascript' language='javascript'>

    function check_submit(){
      var x = 0; var i = 0;
      var category = document.getElementById("category");

      for (i=0;i<category.length;i++){ if(category[i].selected){ x++; }}

      
            if (x>0){
              document.formulario.submit();
            }
            else
              alert("Select some category");
          
    }
    
    

  </script>
</head>
<body bgcolor="white" style="{ margin:0;}" background="../../../img/centerf2.png" style="{ background-repeat: no-repeat;}">


    
  
  
  <center>
 
    <form name='formulario' method='post' class='texto' target="right" action="right.jsp">
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>Config</font><br><br></center>
      <table border='0' cellspacing='15'>        
        
        
        <tr>
        	<td>&nbsp;</td>
        </tr>
        <tr>
        	<td>Selected OCD:</td>
        </tr>
        <tr>        	
        	<td><b><%=ocdName %></b></td>
       	</tr>
       	 <tr>        	
        	<td>&nbsp;</td>
       	</tr>
       	
       	<tr>
        	<td>Select category:</td>
        </tr>
        <tr>        	
        	<td>
          		<select multiple id='category' name='category' size='6' style='width: 200px;' >
<% 
					    for(int j = 0; j < categoryList.size(); j = j+1) {
					    	Category category = categoryList.get(j); 
%>
	                	<option value="<%= category.getName()%>"><%= category.getName()%> </option>
<%						} %>	
          		</select>
        	</td>
        </tr>
        
        <tr>
        	<td align='center'>
          		
          		<input type='button' onclick='check_submit()' value='Show' class='boton'>
          		<input type="hidden" name="ocdName" value="<%=ocdName %>" >
        	</td>
        </tr>
        
         <tr>        	
        	<td>&nbsp;</td>
       	</tr>
       	
       	 <tr>        	
        	<td align='center'><input type='button' onclick="location.href='left1.jsp'" value='Back' class='boton'></td>
       	</tr>
       	
       	
      </table>
    </form>
 
  </center>

 <%  con.close(); %>

  

</body>
</html>