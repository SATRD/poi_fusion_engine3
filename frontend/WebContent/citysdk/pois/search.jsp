<%@ page language="java" contentType="application/json; charset=UTF-8"    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"
  import="java.sql.*,java.util.*, org.json.*, com.google.gson.*  " %>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.poi_search");%>
<% 
	
	//TODO. Set up this as configuration parameter
	Integer limit_result = 400;

	String categoryName = request.getParameter("category");
    String coords = request.getParameter("coords");    
    String limit = request.getParameter("limit");
    String ocdName = request.getParameter("ocdName");
    
    
    if (limit!=null) limit_result = new Integer(limit);
    
	
	
	
	if ( (categoryName!=null) && (ocdName!=null) ){	
		
			

			//Load configuration parameters of the OCD in order to access the OutputDB database
			String relativeWebPath = "/config/config.xml";
			String configPath = getServletContext().getRealPath(relativeWebPath);
			log.info(configPath);
			
			Configuration conf = new Configuration(configPath);
			OutputDB.setConfiguration(configPath);
			
			
			
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
			
				
			
				ArrayList<POI> poiList = new ArrayList<POI>(); //initially empty array
				
				
				if (coords==null){
					poiList = POI.getPOIListByCategoryName(con, categoryName,limit_result,null);   
				}else{
					//Check coords. It can be bbox (4 parameters) or position+radius (3 parameters)
					String[] geospatial = coords.split(",");
									
					if (geospatial.length ==3){
						//point+radius (lat,lon,radius). Radius is given in m
						Double lat = new Double(geospatial[0]);
						Double lon = new Double(geospatial[1]);
						Integer radius = new Integer (geospatial[2]); 
						poiList = POI.getPOIListByCategoryNameAndRadius(con, categoryName,limit_result,lat,lon,radius,null);
						
					}
					
					
					if (geospatial.length ==4){
						//bbox . bbox is supposed to be [Ymin,Xmin,Ymax,Xmax]
						Double lat_min = new Double(geospatial[0]);
						Double lon_min = new Double(geospatial[1]);
						Double lat_max = new Double(geospatial[2]);
						Double lon_max = new Double(geospatial[3]);
						
						poiList = POI.getPOIListByCategoryNameAndBbox(con, categoryName,limit_result,lat_min,lon_min,lat_max,lon_max,null); 
						
					}
					
				}
				
				
				
				
				
				//'pois' object will provide the name to 'poi_array'
				JSONObject pois = new JSONObject();
					
				JSONArray poi_array=new JSONArray();	
				
				for (int i=0;i<poiList.size();i++){
					POI poi= poiList.get(i);			
					
					
					////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					//////////////////////////////////////////////LOCATION PART/////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					//Set lat,lon
					JSONObject  posList = new JSONObject();
					posList.put("posList",poi.getLatitude()+","+poi.getLongitude());
				    
				    JSONObject  Point = new JSONObject();
				    Point.put("Point", posList);			    
				    
				    JSONArray point = new JSONArray();
				    point.put(Point);
				    
				    //Set the address value
				    JSONObject address = new JSONObject();
				    address.put("value",POILabel.getValuebyPOIidandLabelTypeName(con, poi.getId(), "address",null));			    
				    
				    JSONObject location = new JSONObject();
				    location.put("point", point);
				    location.put("address", address);
					////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					//////////////////////////////////////////////LOCATION PART/////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					
					
					
					
					////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					//////////////////////////////////////////////LABEL PART////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					JSONArray label_array = new JSONArray();
					
					//Get all labels from this poi. We may exclude address, name, position or just take those ones interesting
					ArrayList<POILabel> poilabelList = POILabel.getPOILabelListByPOIid(con, poi.getId(),null);				
								
					
					JSONObject label_item;
					
					for (int p=0;p<poilabelList.size();p++){
						
						POILabel poilabel = poilabelList.get(p);					
						
						label_item = new JSONObject();					
						
						String value = poilabel.getValue();					
						
						if (value!=null){
							
							String term = (LabelType.getLabelTypeClassById(con, poilabel.getLabelTypeId(),null)).getName();
							label_item.put("term",term);
							label_item.put("value",poilabel.getValue());
							String source = (Source.getSourceClassById(con, poilabel.getSourceId(),null)).getName();
							label_item.put("source",source);	
							
						
						}
						
						label_array.put(label_item);
					}
					////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					//////////////////////////////////////////////LABEL PART////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				    
				    
				    //Each poi_array item is formed by the 'location' par and the 'label' part
				    JSONObject poi_array_item = new JSONObject();
				    poi_array_item.put("location", location);
				    poi_array_item.put("label", label_array);
				    
				    
				    poi_array.put(poi_array_item);
				    pois.put("poi", poi_array); 			
				}
				con.close();
				
				//Output the JSON categories Object with PrettyJson
				Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
				JsonParser parser = new JsonParser();
				JsonElement je = parser.parse(pois.toString());
				
				String prettyJson = gson.toJson(je);	        
		        
				out.println(prettyJson);	
			    out.flush();
			    
			    OutputDB.disconnectDB(con, null);
			  
		    
			}
		
		
	}else{
		out.println("Wrong syntax");		
	}	
	
	
%>