<%@ page language="java" contentType="application/json; charset=UTF-8"    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"
  import="java.sql.*,java.util.*, org.json.*, com.google.gson.*, com.google.gson.stream.*  " %>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap, javax.servlet.http.HttpServletRequest, javax.servlet.jsp.JspWriter" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.poi_search");%>
<% 
	/*
		 /fic2_fe_v3_frontend	/fiware	/search	/x
		0/1						/2		/3		/4 = index
	*/
	
	//OCD & OPERATION
	int index = 4;
	String [] path = request.getRequestURI().split("\\/");
	String ocdName = null; if(path.length>index) ocdName = path[index];
	String operation = null; if(path.length>(index+1)) operation = path[index+1];
	
	//COMMON PARAMETERS
	String component = request.getParameter("component");
	String category = request.getParameter("category");
	String max_results = request.getParameter("max_results");
	
	//RADIAL_SEARCH PARAMETERS
	String lat = request.getParameter("lat");
	String lon = request.getParameter("lon");
	String radius = request.getParameter("radius");
	
	//BBOX_SEARCH PARAMETERS	
	String north = request.getParameter("north");
	String south = request.getParameter("south");
	String east = request.getParameter("east");
	String west = request.getParameter("west");
	
	//GET_POIS PARAMETERS
	String poi_id = request.getParameter("poi_id");
	
	//DO SEARCH
	Connection con = null;
	try{
		//CHECK OCD & OPERATION
		if(ocdName == null && operation==null) throw SearchError.missingParameters("ocd","operation");
		if(ocdName == null) throw SearchError.missingParameters("ocd");
		if(operation == null) throw SearchError.missingParameters("operation");
		
		con = getConnection(getServletContext(), null);
		OCD ocd = OCD.getOCDClassByName(con, ocdName,null);
		if((ocd==null) || (!ocd.getStatus().equalsIgnoreCase(OCD.STATUS_FINISHED_OK))) throw SearchError.notFound("OCD", ocdName);
	
		OutputDB.disconnectDB(con, null);
		con = getConnection(getServletContext(), ocdName);
		
		//PERFORM OPERATION
		Map<String,ArrayList<POI>> pois = null;
		
		if(operation.equals("radial_search")) pois = Operations.radial_search(con, ocd, lat, lon, radius, category, component, max_results);
		else if(operation.equals("bbox_search")) pois = Operations.bbox_search(con, ocd, north, south, east, west, category, component, max_results);
		else if(operation.equals("get_pois")) pois = Operations.get_pois(con, ocd, poi_id, component);
		else throw SearchError.notFound("Operation", operation);
		
		//MAX RESULTS
		Integer _max_results = null;
		if(max_results!=null) try{_max_results = Integer.parseInt(max_results);}catch(Exception e){ throw SearchError.parameterFormat("max_results");}
		
		//CONVERT TO FIWARE MODEL AND PRINT
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		gson.toJson(Operations.pois_to_json(pois, _max_results), new JsonWriter(out));
		
	}catch(SearchError e){
		response.sendError(e.status, e.message); response.flushBuffer();
	}catch(Exception e){
		e.printStackTrace();
		response.sendError(500, e.getMessage()); response.flushBuffer();
	}finally{
		OutputDB.disconnectDB(con, null);
	}
%>

<%!
static class Operations{
	public static Map<String,ArrayList<POI>> radial_search(Connection con, OCD ocd, String lat, String lon, String radius, String category, String component, String max_results) throws SearchError{
		//CHECK LAT/LON CENTER
		if(lat == null && lon == null) throw SearchError.missingParameters("lat", "lon");
		if(lat == null) throw SearchError.missingParameters("lat");
		if(lon == null) throw SearchError.missingParameters("lon");
		Double _lat = null, _lon = null;
		try{_lat = Double.parseDouble(lat);}catch(Exception e){ throw SearchError.parameterFormat("lat");}
		try{_lon = Double.parseDouble(lon);}catch(Exception e){ throw SearchError.parameterFormat("lon");}
		//CHECK RADIUS
		Integer _radius = 10000;
		if(radius!=null) try{_radius = Integer.parseInt(radius);}catch(Exception e){ throw SearchError.parameterFormat("radius");}
		//CHECK MAX RESULTS
		Integer _max_results = null;
		if(max_results!=null) try{_max_results = Integer.parseInt(max_results);}catch(Exception e){throw SearchError.parameterFormat("max_results");}
		//CHECK COMPONENTS
		if(component!=null && !component.equals("fw_core")) throw SearchError.notFound("Component",component); //AQUÍ HABRÁ QUE CAMBIAR SI SE SOPORTAN MÁS COMPONENTES
		//CHECK CATEGORIES
		ArrayList<String> categories;
		if(category == null) categories = checkCategories(con, null);
		else categories = checkCategories(con, category.split("\\,"));
		//PERFORM SEARCH
		Map<String,ArrayList<POI>> category_poi_map = new HashMap<String,ArrayList<POI>>();
		for(String categoryName : categories){
			ArrayList<POI> poi_list = POI.getPOIListByCategoryNameAndRadius(con, categoryName,_max_results,_lat,_lon,_radius,null);
			category_poi_map.put(categoryName, poi_list);
		}
		return category_poi_map;
	}
	public static Map<String,ArrayList<POI>> bbox_search(Connection con, OCD ocd, String north, String south, String east, String west, String category, String component, String max_results) throws SearchError{
		//CHECK NORTH-SOUTH-EAST-WEST
		if(north == null || south == null || east == null || west == null){
			ArrayList<String> missing = new ArrayList<String>();
			if(north == null) missing.add("north");
			if(south == null) missing.add("south");
			if(east == null) missing.add("east");
			if(west == null) missing.add("west");
			throw SearchError.missingParameters(missing.toArray(new String[]{}));
		}
		Double lat_min = null, lat_max = null, lon_min = null, lon_max = null;
		try{lat_min = Double.parseDouble(south);}catch(Exception e){throw SearchError.parameterFormat("south");}
		try{lat_max = Double.parseDouble(north);}catch(Exception e){throw SearchError.parameterFormat("north");}
		try{lon_min = Double.parseDouble(west);}catch(Exception e){throw SearchError.parameterFormat("west");}
		try{lon_max = Double.parseDouble(east);}catch(Exception e){throw SearchError.parameterFormat("east");}
		//CHECK MAX RESULTS
		Integer _max_results = null;
		if(max_results!=null) try{_max_results = Integer.parseInt(max_results);}catch(Exception e){throw SearchError.parameterFormat("max_results");}
		//CHECK COMPONENTS
		if(component!=null && !component.equals("fw_core")) throw SearchError.notFound("Component",component); //AQUÍ HABRÁ QUE CAMBIAR SI SE SOPORTAN MÁS COMPONENTES
		//CHECK CATEGORIES
		ArrayList<String> categories;
		if(category == null) categories = checkCategories(con, null);
		else categories = checkCategories(con, category.split("\\,"));
		//PERFORM SEARCH
		Map<String,ArrayList<POI>> category_poi_map = new HashMap<String,ArrayList<POI>>();
		for(String categoryName : categories){
			ArrayList<POI> poi_list = POI.getPOIListByCategoryNameAndBbox(con, categoryName,_max_results,lat_min,lon_min,lat_max,lon_max,null);
			category_poi_map.put(categoryName, poi_list);
		}
		return category_poi_map;
	}
	public static Map<String,ArrayList<POI>> get_pois(Connection con, OCD ocd, String poi_id, String component) throws SearchError{
		//CHECK COMPONENTS
		if(component!=null && !component.equals("fw_core")) throw SearchError.notFound("Component",component); //AQUÍ HABRÁ QUE CAMBIAR SI SE SOPORTAN MÁS COMPONENTES
		Map<String,ArrayList<POI>> category_poi_map = new HashMap<String,ArrayList<POI>>();
		String not_found = "";
		for(String _poi_id : poi_id.split("\\,")){
			POI poi = POI.getPOIClassById(con, (int)UUID.fromString(_poi_id.trim()).getLeastSignificantBits(), null);
			if(poi == null) not_found+=_poi_id+", ";
			else{
				Category category = POI.getCategoryListByPOIId(con, poi.getId(), 1, null).get(0);
				if(!category_poi_map.containsKey(category.getName())) category_poi_map.put(category.getName(),new ArrayList<POI>());
				category_poi_map.get(category.getName()).add(poi);
			}
		}
		if(not_found.length()>0) throw SearchError.notFound("POI", not_found.substring(0,not_found.length()-2));
		return category_poi_map;
	}
	public static ArrayList<String> checkCategories(Connection con, String [] categories) throws SearchError{
		ArrayList<Category> _categories = Category.getCategoryList(con, null);
		ArrayList<String> category_names = new ArrayList<String>();
		for(Category category : _categories) category_names.add(category.getName());
		if(categories == null) return category_names;
		ArrayList<String> _checked_categories = new ArrayList<String>();
		for(String category : categories) if(category_names.contains(category.trim())) _checked_categories.add(category.trim()); else throw SearchError.notFound("Category", category.trim());
		return _checked_categories;
	}
	public static JsonObject pois_to_json(Map<String,ArrayList<POI>> pois, Integer limit){
		JsonObject root = new JsonObject();
		JsonObject jpois = new JsonObject();
		int i = 0;
		for(String category : pois.keySet()){
			for(POI poi : pois.get(category)){
				i++; if(limit!=null && limit>-1 && i>limit) break;
				JsonObject fwcore = new JsonObject();
				fwcore.add("category", new JsonPrimitive(category));
				JsonObject wgs84 = new JsonObject();
				wgs84.add("lat", new JsonPrimitive(poi.getLatitude()));
				wgs84.add("lon", new JsonPrimitive(poi.getLongitude()));
				JsonObject location = new JsonObject();
				location.add("wgs84", wgs84);
				fwcore.add("location", location);
				if(poi.getName().length()>31) fwcore.add("name", new JsonPrimitive(poi.getName().substring(0,31)));
				else fwcore.add("name", new JsonPrimitive(poi.getName()));
				JsonObject jpoi = new JsonObject();
				jpoi.add("fw_core", fwcore);
				jpois.add(new UUID(0, poi.getId()).toString(), jpoi);
			}
			if(limit!=null && limit>-1 && i > limit) break;
		}
		root.add("pois", jpois);
		return root;
	}
}
%>

<%!
public static Connection getConnection(ServletContext sc, String ocdName) throws SearchError{
	try{
		String relativeWebPath = "/config/config.xml";
		String configPath = sc.getRealPath(relativeWebPath);
		Configuration conf = new Configuration(configPath);
		OutputDB.setConfiguration(configPath);
		if(ocdName!=null) conf.setDBName("ocd_"+ocdName);
		return OutputDB.connectDB(conf.getConnectionString(),conf.getUser(),conf.getPwd(),conf.getDriverName(),null);
	}catch(Exception e){throw SearchError.internal(e);}
}
%>

<%!
static class SearchError extends Exception{
	public final int status;
	public final String message;
	public SearchError(int status, String message){this.status=status;this.message=message;}
	public static SearchError parameterFormat(String...parameters){
		String msg = "Format error: "; for(int i=0; i<(parameters.length-1); i++) msg+=parameters[i]+", "; msg+=parameters[parameters.length-1];
		return new SearchError(400,msg);
	}
	public static SearchError missingParameters(String...parameters){
		String msg = "Missing parameters: "; for(int i=0; i<(parameters.length-1); i++) msg+=parameters[i]+", "; msg+=parameters[parameters.length-1];
		return new SearchError(400,msg);
	}
	public static SearchError notFound(String key, String value){
		return new SearchError(404, key+" not found: "+value);
	}
	public static SearchError internal(Exception e){
		return new SearchError(500, e.getMessage());
	}
}
%>