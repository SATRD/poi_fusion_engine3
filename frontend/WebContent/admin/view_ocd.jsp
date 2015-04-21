<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.edit_ocd");%>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">



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
            
    ArrayList<Source> sources = Source.getSourceList(con,null);
    int num_sources = sources.size();  
    
    ArrayList<Category> categories = Category.getCategoryList(con,null);
    int num_categories = categories.size();  
    
        
    //Get the corresponding source and its associated values (sources and licenses)
    	
   	OCD ocd = OCD.getOCDClassById(con,new Integer(id_selec),null);
   	String id = ocd.getId().toString();
   	String name = ocd.getName();
   	
   	String selectedCity = (City.getCityClassById(con,ocd.getCityId(),null)).getName();
   	String description = ocd.getDescription();
   	if (description == null) description = "";
   	  	
   	   	  	
   	String fusionRules = ocd.getFusionRulesPath();
   	fusionRules = fusionRules.substring(fusionRules.lastIndexOf("/")+1);   
   	
   	String accesskey = ocd.getAccessKey();
   	   	
   	
   	ArrayList<OCDSource> selectedSources = OCDSource.getOCDSourceListByOCDId(con,new Integer(id),null);
   	int num_selectedSources = selectedSources.size();   
   	
   	
   	ArrayList<OCDCategory> selectedCategories = OCDCategory.getOCDCategoryListByOCDId(con,new Integer(id),null);
   	int num_selectedCategories = selectedCategories.size();   
%>
    
 <html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href="../css/main.css"> 
  <script type='text/javascript' language='javascript'>
function showRules(){
	 
	window.open("../config/fusionRules/<%=fusionRules%>");
    			
    }
      
     
    
    

  </script>
</head>
<body bgcolor="white" style="{ margin:0;}" background="../img/centerf2.png" style="{ background-repeat: no-repeat;}"> 
  
  <center>
  <div class='main_content'>
    <form name='formulario' method='post' class='texto'>
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>View OCD</font><br><br></center>
      <table border='0' cellspacing='15'>        
        <tr>
        	<td width=100px>Name</td>
        	<td width=200px> <input type='text' name='name' value="<%=name%>" style="width: 400px;" readonly/> </td>
        </tr>
        
        <tr>
        	<td valign="top">City</td>
        	<td>
        		<select id="city" name="city" style="width: 400px;" disabled >
<% 
					    for(int j = 0; j < num_cities; j = j+1) {
					    	City city = sourceCities.get(j);
					    	String name_city = city.getName();
%>
		                	<option value="<%= city.getId()%>"  <% if (name_city.equalsIgnoreCase(selectedCity)) out.println("selected"); %> >
		                		<%= city.getName()%>
		                	</option>
<%						} %>	
          		</select>         
        	</td>
        </tr>
        
        
        <tr>
        	<td valign="top">Description</td>
        	<td>
          		<textarea name="desc" style="width: 400px; height:55px; resize:none;" readonly><%=description%></textarea>
        	</td>
        </tr>
        
              
        <tr>        
        	<td>Fusion rules</td> 
        		<td>
          			<%=fusionRules%> <input type="button" name="rules" width=" 400px" value="View" onclick="showRules()">
          			
          			
        		</td>
        </tr>
        
          <tr>
        	<td width=100px>Accesskey</td>
        	<td width=200px> <input type='text' name='accesskey' value="<%=accesskey%>" style="width: 400px;" readonly/> </td>
        </tr>    
                
        
        <tr>
        	<td valign="top">Sources</td>
        	<td>
          		<select multiple id="sources" name="sources" size="6" style="width: 200px;" disabled>
<% 
					    for(int j = 0; j < num_sources; j = j+1) {
					    	Source source = sources.get(j);
					    	String sourceId = source.getId().toString();
					    	String sourceName = source.getName();
					    	
					    	//check if this source is selected
					    	boolean selectedSource = false;
					    	boolean aux;
					    	for(int k = 0; k < num_selectedSources; k = k+1) {
								
					    		String selectedSourceId = (selectedSources.get(k)).getSourceId().toString();
								aux = selectedSourceId.equalsIgnoreCase(sourceId);
								if ((aux) && (!selectedSource) ) selectedSource = true;
					    	}
%>
	                	<option value="<%= sourceId%>" <% if (selectedSource) out.println("selected");%>>
	                		<%= sourceName%> 
	                	</option>
<%						} %>	
          		</select>
        	</td>
        </tr>
        
        <tr>
        	<td valign="top">Categories</td>
        	<td>
          		<select multiple id="categories" name="categories" size="6" style="width: 200px;" disabled>
<% 
					    for(int j = 0; j < num_categories; j = j+1) {
					    	Category category = categories.get(j);
					    	String categoryId = category.getId().toString();
					    	String categoryName = category.getName();
					    	
					    	//check if this category is selected
					    	boolean selectedCategory = false;
					    	boolean aux;
					    	for(int k = 0; k < num_selectedCategories; k = k+1) {
								
					    		String selectedCategoryId = (selectedCategories.get(k)).getCategoryId().toString();
								aux = selectedCategoryId.equalsIgnoreCase(categoryId);
								if ((aux) && (!selectedCategory) ) selectedCategory = true;
					    	}
%>
	                	<option value="<%= categoryId%>" <% if (selectedCategory) out.println("selected");%>>
	                		<%= categoryName%> 
	                	</option>
<%						} %>	
          		</select>
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

 <%  OutputDB.disconnectDB(con,null); %>

  

</body>
</html>