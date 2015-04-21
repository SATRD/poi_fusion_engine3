<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.edit_source");%>

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
          
            if (x>0){
              document.formulario.submit();
            }
            else
              alert("Select some city");
          }
          
        else
          alert("Insert a url to access to the source");
      }
      else
        alert("Insert a name");
    }
    
    function activate_mapping(){
    	document.formulario.mapping_selec.value =1;
    }
    
    

  </script>
</head>
<body bgcolor="white" style="{ margin:0;}" background="../img/centerf2.png" style="{ background-repeat: no-repeat;}">

<%    


	String id_selec = request.getParameter("id_selected");
	

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
    
        
    //Get the corresponding source and its associated values (sources and licenses)
    	
   	Source source = Source.getSourceClassById(con,new Integer(id_selec),null);
   	String id = source.getId().toString();
   	String name = source.getName();
   	String description;
   	description = source.getDescription();
   	if (description == null) description = "";
   	   	
   	
   	String urlaccess = source.getUrlaccess();
   	String apiselected =  APIType.getAPITypeClassById(con, source.getAPITypeId(),null).getName(); //    source.getAPITypeId().toString();  
   	
   	String categoryMapping = source.getCategorymapping();
   	categoryMapping = categoryMapping.substring(categoryMapping.lastIndexOf("/")+1);
   	
   	ArrayList<SourceCity> selectedsourceCities =  SourceCity.getSourceCityListBySourceId(con,new Integer(id),null);
   	int num_selectedsourceCities = selectedsourceCities.size();
   	
   	ArrayList<LicenseSource> selectedsourceLicenses = LicenseSource.getLicenseSourceListBySourceId(con,new Integer(id),null);
   	int num_selectedsourceLicenses = selectedsourceLicenses.size();
   
    
%>
    
  
  
  <center>
  <div class='main_content'>
    <form name='formulario' method='post' class='texto' enctype='multipart/form-data' action="update_source.jsp">
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>Edit Data source</font><br><br></center>
      <table border='0' cellspacing='15'>        
        <tr>
        	<td width=100px>Name</td>
        	<td width=200px> <input type='text' name='name' value="<%=name%>" style='width: 400px;' /> </td>
        </tr>
        <tr>
        	<td valign="top">Description</td>
        	<td>
          		<textarea name="desc" style="width: 400px; height:55px; resize:none;"><%=description%></textarea>
        	</td>
        </tr>
        <tr>
        	<td>Url access</td>
        	<td width=200px> <input type="text" name="access" value="<%=urlaccess%>" style='width: 400px;'> </td>
        </tr>
              
        <tr>        
        	<td>Category mapping</td> 
        		<td>
          			<input type='file' name='mapping' width=" 400px" onchange="activate_mapping()"><%=categoryMapping%>
          			
          			
        		</td>
        </tr>
        <tr>
        	<td>API</td>
        	<td>
          		<select name='api' style='width: 400px;' >
<% 
					    for(int j = 0; j < num_apitypes; j = j+1) {
					    	APIType apitype = apitypes.get(j);
					    	boolean selected = false;
					    	//String aux = apitype.getId().toString();
					    	//if (aux.equalsIgnoreCase(apiselected)) selected = true;
					    	if (apitype.getId().equals(source.getAPITypeId())) selected = true;
%>
	                	<option value="<%=apitype.getId().toString()%>" <% if (selected) out.println("selected");%> ><%=apitype.getName()%> </option>
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
					    	//Check if this city is selected
					    	boolean selected = false;
					    	for(int k = 0; k < num_selectedsourceCities; k = k+1) {
					    		SourceCity selec = selectedsourceCities.get(k);
					    		//String aux =  City.getCityClassById(con, selec.getCityId()).getName();                          //selec.get("cityid").toString();
					    		//String aux2 = city.getId().toString();
					    		//if (aux.equalsIgnoreCase(aux2)) selected = true;	
					    		
					    		if (city.getId().equals(selec.getCityId()))	selected = true;
					    		
					    	}
					    	
%>
	                	<option value="<%=city.getId().toString()%>" <% if (selected) out.println("selected");%>   ><%= city.getName()%> </option>
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
					    	//Check if this license is selected 
					    	boolean selected = false;
					    	for(int k = 0; k < num_selectedsourceLicenses; k = k+1) {
					    		LicenseSource selec = selectedsourceLicenses.get(k);
					    		//String aux =   License.getLicenseClassById(con, selec.getLicenseId()).getName();                      // selec.get("idlicense").toString();
					    		//String aux2 = license.getId().toString();
					    		//if (aux.equalsIgnoreCase(aux2)) selected = true;	
					    		if (license.getId().equals(selec.getLicenseId())) selected = true;
					    	}
%>
	                	<option value="<%= license.getId().toString()%>"  <% if (selected) out.println("selected");%> ><%= license.getName()%> </option>
<%						} %>	
          		</select>
        	</td>
        </tr>
        <tr>
        	<td colspan='2' align='center'>
          		<br>
          		<input type='button'  value='Return' OnClick="location.href='list_sources.jsp'" class='boton'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          		&nbsp;&nbsp;&nbsp;<input type='button' onclick='check_submit()' value='Update' class='boton'>
        	</td>
        </tr>
      </table>
      <input type="hidden" name="id_selec" value="<%=id_selec%>">
      <input type="hidden" name="mapping_selec" value="0">  
      
    </form>
  </div>
  </center>

 <%  OutputDB.disconnectDB(con,null); %>

  

</body>
</html>