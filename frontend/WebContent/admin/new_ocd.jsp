<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.new_ocd");%>
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
	 
      var x_city = 0;      
      var x_sources=0;
      var i = 0;
      var city = document.getElementById("city");

      for (i=0;i<city.length;i++){ if(city[i].selected){ x_city++; }}      
      for (i=0;i<sources.length;i++){ if(sources[i].selected){ x_sources++; }}

      //check name
      if (document.formulario.name.value != ""){
    	
    	//check city selected.
    	if (x_city > 0){
    		
    		//check fusion rules.
    		if (document.formulario.rules.value != ""){    			   			
    				
    				//check sources.
    				if (x_sources > 1){
    					document.formulario.submit();
    				}else 
    					alert("Select at least two sources");    			
    			
    		}else 
    			alert("Insert a fusion rules file");
    	}else
    		alert("Select a city");
        
      }else
        alert("Insert a name");
    }
      
     
    
    

  </script>
</head>
<body bgcolor="white" style="{ margin:0;}" background="../img/centerf2.png" style="{ background-repeat: no-repeat;}">

<%    
	    
    //Connect to the database
    String relativeWebPath = "/config/config.xml";
	String configPath = getServletContext().getRealPath(relativeWebPath);
	Configuration configFile = new Configuration(configPath);
	
	Connection con = OutputDB.connectDB(
			configFile.getConnectionString(),
			configFile.getUser(),
			configFile.getPwd(),
			configFile.getDriverName(),null);
    
    //Initialize variables
    ArrayList<City> sourceCities   = City.getCityList(con,null);
    int num_cities = sourceCities.size();    
    
    //ArrayList<HashMap<String, Object>> sourceCategories = OutputDB.getCategoryList(con);
    //int num_categories = sourceCategories.size();
    
    ArrayList<Source> sources = Source.getSourceList(con,null);
    int num_sources = sources.size();
    
    ArrayList<Category> categories = Category.getCategoryList(con,null);
    int num_categories = categories.size();
    
%>
    
  
  
  <center>
  <div class='main_content'>
    <form name='formulario' method='post' class='texto' enctype='multipart/form-data' action="save_ocd.jsp">
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>New OCD</font><br><br></center>
      <table border='0' cellspacing='15'>        
        <tr>
        	<td width=100px>Name</td>
        	<td width=200px> <input type='text' name='name' style='width: 400px;' /> </td>
        </tr>
        
        <tr>
        	<td valign="top">City</td>
        	<td>
        		<select id='city' name='city' style='width: 400px;' >
<% 
					    for(int j = 0; j < num_cities; j = j+1) {
					    	City city = sourceCities.get(j);
%>
	                	<option value="<%= city.getId().toString()%>"><%= city.getName()%> </option>
<%						} %>	
          		</select>         
        	</td>
        </tr>
        
        
        <tr>
        	<td valign="top">Description</td>
        	<td>
          		<textarea name="desc" style="width: 400px; height:55px; resize:none;"></textarea>
        	</td>
        </tr>
        
        <tr>
        	<td colspan="2">
        		<h5 style="color: #FF0000; font-size: 10pt">Be specially cautious with the fusionRules file. </br>
				It is by far the most important configuration parameter for the fusion.</br>        	
        	</td>
        
        </tr>
        
        
              
        <tr>        
        	<td>Fusion rules</td> 
        		<td>
        		
          			<input type='file' name='rules' width=" 400px">
          			
          			
        		</td>
        </tr>
        
        <tr>
        	<td width=100px>Accesskey</td>
        	<td width=200px> <input type='text' name='accesskey' style='width: 400px;' /> </td>
        </tr>        
        
        
        <tr>
        	<td valign="top">Sources</td>
        	<td>
          		<select multiple id='sources' name='sources' size='6' style='width: 200px;' >
<% 
					    for(int j = 0; j < num_sources; j = j+1) {
					    	Source source = sources.get(j);
%>
	                	<option value="<%= source.getId().toString()%>"><%= source.getName()%> </option>
<%						} %>	
          		</select>
        	</td>
        </tr>
        
        <tr>
        	<td valign="top">Categories</td>
        	<td>
          		<select multiple id='categories' name='categories' size='6' style='width: 200px;' >
<% 
					    for(int j = 0; j < num_categories; j = j+1) {
					    	Category category = categories.get(j);
%>
	                	<option value="<%= category.getId().toString()%>"><%= category.getName()%> </option>
<%						} %>	
          		</select>
        	</td>
        </tr>
        
        
        <tr>
        	<td colspan='2' align='center'>
          		<br>
          		<input type='button'  value='Return' OnClick="location.href='list_ocds.jsp'" class='boton'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          		&nbsp;&nbsp;&nbsp;<input type='button' onclick='check_submit()' value='Save' class='boton'>
        	</td>
        </tr>
      </table>
    </form>
  </div>
  </center>

 <%  OutputDB.disconnectDB(con,null); %>

  

</body>
</html>