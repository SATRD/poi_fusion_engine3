<%@ page language="java" contentType="application/json; charset=UTF-8"    pageEncoding="UTF-8"%>
<%@ page  import="java.sql.*,java.util.*, org.json.*, com.google.gson.*  " %>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.categories_search");%>
<% 
	
	String list = request.getParameter("list");
	String ocdName = request.getParameter("ocdName");
	
	
	
	if ((list!=null) && (ocdName!=null)  ){
		
		if (list.equalsIgnoreCase("poi")){
			

			//Load configuration parameters of the OCD in order to access the OutputDB database
			String relativeWebPath = "/config/config.xml";
			String configPath = getServletContext().getRealPath(relativeWebPath);
			Configuration conf = new Configuration(configPath);
			
			
			//OutputDB.setConfiguration(configPath);
			
			//Get Connection
			Connection con = OutputDB.connectDB(
				conf.getConnectionString(),
				conf.getUser(),
				conf.getPwd(),
				conf.getDriverName(),null);	
			
			//Look if the city exists
			OCD ocd = OCD.getOCDClassByName(con, ocdName,null);
			
			if ( (ocd!=null) && (ocd.getStatus().equalsIgnoreCase(OCD.STATUS_FINISHED_OK))  ){
				
				//Connect to this database
				OutputDB.disconnectDB(con,null);
				conf.setDBName("ocd_"+ocdName);
				con = OutputDB.connectDB(
						conf.getConnectionString(),
						conf.getUser(),
						conf.getPwd(),
						conf.getDriverName(),null);	
				
				ArrayList<Category> categoryList = Category.getCategoryList(con,null); 
				
				con.close();
				
				JSONObject categories=new JSONObject();	
				JSONArray categories_array = new JSONArray();
				JSONObject label_item;
				JSONArray label_array;
				JSONObject label_array_item;
				
				for (int i=0;i<categoryList.size();i++){
					Category cat = categoryList.get(i);
					
					label_item=new JSONObject();
					
					
					label_array_item=new JSONObject();
					label_array_item.put("term","primary");
					label_array_item.put("value",cat.getName());
									
					label_array = new JSONArray();
					label_array.put(label_array_item);
					
					label_item=new JSONObject();
					label_item.put("label",label_array);
					
					categories_array.put(label_item);
					
				}
				
				categories.put("categories",categories_array);
				
				//Output the JSON categories Object with PrettyJson
				Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
				JsonParser parser = new JsonParser();
				JsonElement je = parser.parse(categories.toString());
				
				String prettyJson = gson.toJson(je);	        
		        
				out.println(prettyJson);	
			    out.flush();
			}else{
				out.println("City does not exist");
			}
		    
		 
		}else{
			out.println("Wrong syntax");
		}
		
	}else{
		out.println("Wrong syntax");		
	}	
	
%>