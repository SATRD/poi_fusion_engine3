<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.new_source");%>
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
      var x = 0; var i = 0;
      var city = document.getElementById("city");

      for (i=0;i<city.length;i++){ if(city[i].selected){ x++; }}

      if (document.formulario.name.value != ""){
        if (document.formulario.access.value != ""){
          if (document.formulario.mapping.value != ""){
            if (x>0){
              document.formulario.submit();
            }
            else
              alert("Select some city");
          }
          else
            alert("Insert a category mapping file");
        }
        else
          alert("Insert a url to access to the source");
      }
      else
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
    
    ArrayList<License> sourceLicenses = License.getLicenseList(con,null);
    int num_licenses = sourceLicenses.size();
    
    ArrayList<APIType> apitypes = APIType.getAPITypeList(con,null);
    int num_apitypes = apitypes.size(); 
    
%>
    
  
  
  <center>
  <div class='main_content'>
    <form name='formulario' method='post' class='texto' enctype='multipart/form-data' action="save_source.jsp">
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>New Data source</font><br><br></center>
      <table border='0' cellspacing='15'>        
        <tr>
        	<td width=100px>Name</td>
        	<td width=200px> <input type='text' name='name' style='width: 400px;' /> </td>
        </tr>
        <tr>
        	<td valign="top">Description</td>
        	<td>
          		<textarea name="desc" style="width: 400px; height:55px; resize:none;"></textarea>
        	</td>
        </tr>
        <tr>
        	<td>Url access</td>
        	<td> <input type="text" name="access"  width="400px"> </td>
        </tr>
              
        <tr>        
        	<td>Category mapping</td> 
        		<td>
          			<input type='file' name='mapping' width=" 400px">
          			
          			
        		</td>
        </tr>
        <tr>
        	<td>API</td>
        	<td>
          		<select name='api' style='width: 400px;' >
<% 
					    for(int j = 0; j < num_apitypes; j = j+1) {
					    	APIType apitype = apitypes.get(j);
%>
	                	<option value="<%=apitype.getId().toString()%>"><%=apitype.getName()%> </option>
<%						} %>	            
	          </select>
       		</td>
       	</tr>
        <tr>	
        	<td valign="top">Cities</td>
        	<td>
          		<select multiple id='city' name='city' size='6' style='width: 200px;' >
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
        	<td valign="top">Licences</td>
        	<td>
          		<select multiple name='license' size='6' style='width: 200px;' >
<% 
					    for(int j = 0; j < num_licenses; j = j+1) {
					    	License license = sourceLicenses.get(j);
%>
	                	<option value="<%= license.getId().toString()%>"><%= license.getName()%> </option>
<%						} %>	
          		</select>
        	</td>
        </tr>
        <tr>
        	<td colspan='2' align='center'>
          		<br>
          		<input type='button'  value='Return' OnClick="location.href='list_sources.jsp'" class='boton'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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