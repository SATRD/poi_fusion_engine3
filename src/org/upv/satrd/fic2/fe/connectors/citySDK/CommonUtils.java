package org.upv.satrd.fic2.fe.connectors.citySDK;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.upv.satrd.fic2.fe.db.POISource;
import org.upv.satrd.fic2.fe.db.Source;

import com.wcohen.ss.MongeElkan;

public class CommonUtils {

	
	private static org.apache.log4j.Logger log = Logger.getLogger(org.upv.satrd.fic2.fe.connectors.citySDK.CommonUtils.class);;
	
	/// Send a request to POI Server
	private static JSONObject request(String url, Logger log){
		
		 
		if (log == null) log = CommonUtils.log;
		
		try{
			//Get data
			URL obj = new URL(url);
			
			simpleLog("==============================================");
			simpleLog("Request to URL: " + url);
			InputStream is = obj.openStream();
			
			simpleLog("Stream opened.");
			
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(is,
							Charset.forName(
							"UTF-8")));

			StringBuilder sb = new StringBuilder();
			char[] char_buffer = new char[8 * 1024];
		    int cp;
		    while ((cp = rd.read(char_buffer)) != -1) {
		    // while ((cp = rd.read()) != -1) {
		    	//simpleLog("Receiving...");
		    	sb.append(char_buffer, 0, cp);
		    	// sb.append((char) cp);
		    }
		    
		    simpleLog("Receiving done.");
		    String fix_encod = fixEncoding(sb.toString(), true); 
		    JSONObject json = new JSONObject(fix_encod);
	  
		    is.close();
		    rd.close();
		    
		    //System.out.println(json);
			return json;
			
		} catch ( Exception e) {
			System.out.println("Error CommonUtils.request(): "+e.getMessage());
			log.error("Error CommonUtils.request(): "+e.getMessage());
		}
		
		return null;	
	}
	
	private static String fixEncoding(String string, boolean doit) {
		
		if (!doit) {
			return string;
		}
		
		String resp = string;
		resp = resp.replace("ïż½a", "ía");
		resp = resp.replace("ïż½", "ó");
		return resp;
	}

	/*********************************************************************************************************/
	
	/// Get a list of categories from one source (osm, dbpedia, poiproxy)
	public static ArrayList<String> getPOICategoryList(String server, Logger log){
		
		String url = server+"categories/search?list=poi";
		JSONObject response = request(url,log);		
		
		ArrayList<String> categories = new ArrayList<String>();
		if (log == null) log = CommonUtils.log;
		
		try{
			JSONArray cat = response.getJSONArray("categories");
			for(int i=0; i<cat.length(); i++){
				String value = cat.getJSONObject(i).getJSONArray("label").getJSONObject(0).getString("value");
				categories.add(value);
			}			
			
		} catch ( Exception e) {
			System.out.println("Error CommonUtils.getPOICategoryList(): "+e.getMessage());
			log.error("Error CommonUtils.getPOICategoryList(): "+e.getMessage());
		}
		
		return categories;		
	}
	
	/*********************************************************************************************************/
	
	/// Get a list of POIs of one source (osm, dbpedia, poiproxy) and one category
	public static JSONObject getPOIListBySourceAndCategory(String server, String source, String category, String bbox, String limit, String city, Logger log){
		
		String url;
		if (log == null) log = CommonUtils.log;
		url = server+"pois/search?category="+category+"&limit="+limit+"&coords="+bbox;
		if (server.contains("osm")){
			//TODO fix this for any city. This is done in order to make OSM data provider run faster for the cities of Valencia and Tenerife
			if (city.equalsIgnoreCase("tenerife_demo")){
				url = server+"pois/search?category="+category+"&limit="+limit+"&city=sctenerife"+"&coords="+bbox;
			}
			if (city.equalsIgnoreCase("valencia_demo")){
				url = server+"pois/search?category="+category+"&limit="+limit+"&city=valencia"+"&coords="+bbox;
			}
			
		}
		JSONObject response = request(url,log);
		
		
		log.info("CommonUtils.getPOIListBySourceAndCategory(). Using URLaccess for this category: "+url);
		
		try{
			
			
			//if (source.equalsIgnoreCase("poiproxy")) source = "poiproxy.local";
			
			//Add source field
			JSONArray poi = response.getJSONArray("poi");		
			
			for(int i=0; i<poi.length(); i++){
				
				//Add source in label
				JSONArray value = poi.getJSONObject(i).getJSONArray("label");
				
				if(value.length() > 0){
					for(int j=0; j<value.length(); j++){
						
						JSONObject aux =  poi.getJSONObject(i).getJSONArray("label").getJSONObject(j);
						aux.put("base", source);
						
					}
				}	
				
				//Add source in description, if it exists		
				try{
					
					value = poi.getJSONObject(i).getJSONArray("description");				
					if(  (value!=null) && (value.length() > 0) ){
						for(int j=0; j<value.length(); j++){
							JSONObject aux =  poi.getJSONObject(i).getJSONArray("description").getJSONObject(j);
							aux.put("base", source);
						}
					}
				}catch(JSONException e){					
					//log.warn(e.getMessage());					
				}
					
				
				
				//Add source in category, if it exists
				try{
					value = poi.getJSONObject(i).getJSONArray("category");
					if(  (value!=null) && (value.length() > 0) ){
						for(int j=0; j<value.length(); j++){
							JSONObject aux =  poi.getJSONObject(i).getJSONArray("category").getJSONObject(j);
							aux.put("base", source);
						}
					}
				}catch(JSONException e){						
						log.error(e.getMessage());					
					
				}
				
				
				//Add source in link, if it exists
				try{ 
					value = poi.getJSONObject(i).getJSONArray("link");
					if(  (value!=null) && (value.length() > 0) ){
						for(int j=0; j<value.length(); j++){
							JSONObject aux =  poi.getJSONObject(i).getJSONArray("link").getJSONObject(j);
							aux.put("base", source);
						}
					}		
				}catch(JSONException e){					
					//log.warn(e.getMessage());					
				
				}
			}
			
			
		} catch ( Exception e) {
			System.out.println("Error CommonUtils.getPOIListBySourceAndCategory(): "+e.getMessage());
			log.error("Error CommonUtils.getPOIListBySourceAndCategory(): "+e.getMessage());
		}		
		
		
		return response;
		
	}
	
	/*********************************************************************************************************/
	
		/// Get a list of POIs from poiproxy. Here instead of bbox we have point and radius
		public static JSONObject getImgPOIList(String server, String category, String point_radius, String limit, String city, Logger log){
					
			if (log == null) log = CommonUtils.log;
			String url;
			url = server+"pois/search?category="+category+"&limit="+limit+"&coords="+point_radius;
			if (server.contains("osm")){
				//TODO fix this for any city. This is done in order to make OSM run faster
				if (city.equalsIgnoreCase("tenerife_demo")){
					url = server+"pois/search?category="+category+"&limit="+limit+"&city=sctenerife"+"&coords="+point_radius;
				}
				if (city.equalsIgnoreCase("valencia_demo")){
					url = server+"pois/search?category="+category+"&limit="+limit+"&city=valencia"+"&coords="+point_radius;
				}			
			}
			
			JSONObject response = request(url,log);			
			
			log.info("CommonUtils.getImgPOIList(). Using URLaccess for this category: "+url);		
			
			return response;
			
		}
		
	
	
	
	/// Get a list of POIs in a zone of one source (osm, dbpedia)
	public static ArrayList<JSONObject> getZonePOIs(String server, String source, String category, String lat, String lon, String r, 
			String sourceName, String percent, String city, Logger log){
		
		if (log == null) log = CommonUtils.log;
		String url;
		url = server+"pois/search?category="+category+"&coords="+lat+","+lon+","+r;
		if (server.contains("osm")){		
			//TODO fix this for any city. This is done in order to make OSM run faster
			if (city.equalsIgnoreCase("tenerife_demo")){
				url = server+"pois/search?category="+category+"&city=sctenerife"+"&coords="+lat+","+lon+","+r;
			}
			if (city.equalsIgnoreCase("valencia_demo")){
				url = server+"pois/search?category="+category+"&city=valencia"+"&coords="+lat+","+lon+","+r;
			}				
		}
		
		
		log.info("CommonUtils.getZonePOIs(). Using URLaccess for potential matches: "+url);
		
		JSONObject response = request(url,log);		
				
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		
		try{	
			//Search the correct POI
			JSONArray poiList = response.getJSONArray("poi");
			
			for(int i=0; i<poiList.length(); i++){
				
				JSONObject poi = poiList.getJSONObject(i);
				
				//Filter by name
				if(sourceName != null){
					if(percent == null || !org.apache.commons.lang3.math.NumberUtils.isNumber(percent)){ 
						log.warn("CommonUtils.getZonePOIs(). Given a wrong value of similarity percentage. Using default value (60)..");
						percent = "60"; 
					}   //Default value
			    			
					int namePos = CommonUtils.getPropertyPos(poi,"name",log);
					
					// POI has a name
        			if(namePos != -1){
        				String name = poi.getJSONArray("label").getJSONObject(namePos).getString("value");
        				
    					double per = similar(name,sourceName);
    					log.debug("CommonUtils.getZonePOIs(). Similarity between "+sourceName+" - "+name+" : "+per);
    					
    					if(per >= Double.parseDouble(percent)){
    						//We have a matching POI here. Add it
    						log.debug("Commonutils.getZonePOIs(). Matching POI found");
    						result.add(poi);						
    					}
        			}
					
					
				}
			}
			
			
			//if (source.equalsIgnoreCase("poiproxy")) source = "poiproxy.local";
			
			//Add source field		
			for(int i=0; i<result.size(); i++){
				JSONObject poi = result.get(i);
				
				//Add source in label
				JSONArray value = poi.getJSONArray("label");
				if(value.length() > 0){
					for(int j=0; j<value.length(); j++){
						JSONObject aux =  poi.getJSONArray("label").getJSONObject(j);
						aux.put("base", source);
					}
				}
				
				//Add source in description
				try{
					value = poi.getJSONArray("description");
					if(  (value!=null) && (value.length() > 0) ){
						for(int j=0; j<value.length(); j++){
							JSONObject aux =  poi.getJSONArray("description").getJSONObject(j);
							aux.put("base", source);
						}
					}
				}catch(JSONException e){					
					log.error(e.getMessage());					
				
				}
				
				//Add source in category
				try{
					value = poi.getJSONArray("category");
					if(  (value!=null) && (value.length() > 0) ){
						for(int j=0; j<value.length(); j++){
							JSONObject aux =  poi.getJSONArray("category").getJSONObject(j);
							aux.put("base", source);
						}
					}
				}catch(JSONException e){					
					log.error(e.getMessage());					
				
				}
				
				//Add source in link
				try{
					value = poi.getJSONArray("link");
					if(  (value!=null) && (value.length() > 0) ){
						for(int j=0; j<value.length(); j++){
							JSONObject aux =  poi.getJSONArray("link").getJSONObject(j);
							aux.put("base", source);
						}
					}	
				}catch(JSONException e){					
					log.warn(e.getMessage());					
				
				}
			}
			
		} catch ( Exception e) {
			System.out.println("Error CommonUtils.getZonePOIs(): "+e.getMessage());
			log.error("Error CommonUtils.getZonePOIs(): "+e.getMessage());
		}
		
		return result;
	}
	
	

	// Get a value of similarity between two strings
	private static double similar(String s1, String s2){
				
		MongeElkan simil = new MongeElkan();
		double simil13 = simil.score(s1, s2);
		simil13 = simil13 * 100;
		
		return simil13;
	}
	
	
	/// Get the key array of a property in a POITertType
	public static int getPropertyPos(JSONObject poi,String term, Logger log){
		int result = -1;
		if (log == null) log = CommonUtils.log;
		try{
			JSONArray label = poi.getJSONArray("label");
			for(int i=0; i<label.length(); i++){
				if(label.getJSONObject(i).getString("term").equals(term))
					result = i;//label.getJSONObject(i).getString("value");
			}
		} catch ( Exception e) {
			log.error("Error.CommonUtils.getPropertyPos(): "+e.getMessage());
		}
		
		return result;
	}
	
	
	
		
	
	public static boolean CheckPOIAlreadyInserted(Connection con, Integer sourceId, String originalRef, String projectedCategory, Logger log){
		
		if (log == null) log = CommonUtils.log;
		POISource poisource = POISource.getPOISourceClassBySourceIdAndOriginalRefAndPoiproxyAttribute(con, sourceId, originalRef, projectedCategory,log);
		
		if (poisource !=null) return true;
		else return false;			
	}
	
	
	//This method scans a POI given as a JSON object and returns its latitude
	public static String getLatitude(JSONObject poi, Logger log){
		
		String position;
		String lat = null;
		if (log == null) log = CommonUtils.log;
		
		try {
			position = poi.getJSONObject("location").getJSONArray("point").getJSONObject(0).getJSONObject("Point").getString("posList");
			String[] pos = position.split(" ");

			lat = pos[0];			
			
		} catch (JSONException e) {
			log.error("Error. CommonUtils.getLatitude(): "+e.getMessage());
		}
		
		return lat;
	}
	
	//This method scans a POI given as a JSON object and returns its longitude
	public static String getLongitude(JSONObject poi, Logger log){
		
		String position;
		String lon = null;
		if (log == null) log = CommonUtils.log;
		
		try {
			position = poi.getJSONObject("location").getJSONArray("point").getJSONObject(0).getJSONObject("Point").getString("posList");
			String[] pos = position.split(" ");

			
			 lon = pos[1];
			
		} catch (JSONException e) {
			log.error("Error. CommonUtils.getLongitude(): "+e.getMessage());
		}
		
		return lon;
	}
	
	//This method scans a POI given as a JSON object and returns its longitude
	public static String getAddress(JSONObject poi, Logger log){
		
		String address = null;
		if (log == null) log = CommonUtils.log;
		
		try {
			address = poi.getJSONObject("location").getJSONObject("address").getString("value");			 
			
		} catch (JSONException e) {
			log.error("Error. CommonUtils.getAddress(): "+e.getMessage());
		}
		
		return address;
	}
	
	//This method return current date as a java.sql.Date
	public static Date getFormatedDateAsDate(){
		
		Calendar cal = Calendar.getInstance();			
		java.sql.Date date2 = new java.sql.Date(cal.getTime().getTime());
		
		return date2;
	}
	
	//This method return current date as a formated string
	public static String getFormatedDateAsString(){
		
		Calendar cal = Calendar.getInstance();				
		SimpleDateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");				
		String date = outputformat.format(cal.getTime());	
		return date;
	}
		
	
	
	//This method scans a POI given as a JSON object and returns its longitude
	public static String getName(JSONObject poi, Logger log){
		
		
		String name = null;
		if (log == null) log = CommonUtils.log;
		
		try {
			//Get the default name of the POI
			int namePos = CommonUtils.getPropertyPos(poi,"name",log);
			name = poi.getJSONArray("label").getJSONObject(namePos).getString("value").toString();
			
		} catch (JSONException e) {
			log.error("Error CommonUtils.getName(): "+e.getMessage());
		}
		
		return name;
	}
	
	
	public static POISource getPOISource(Connection con,JSONObject poi, Logger log){
		POISource poisource;  
		JSONArray object;
	    JSONObject aux;
	    String source ="";
	    String originalRef="";	
	    
	    if (log == null) log = CommonUtils.log;
	    
		try{
	    
			//Get the 'originalRef' value from the id field						
		    object = poi.getJSONArray("label");
		   
			if (object !=null){	
				for(int i=0; i<object.length(); i++){
						
						aux =  object.getJSONObject(i);	
						
						//'term' should be 'name' or 'id'
						String term = (String)aux.get("term"); 
						
						if (term.equalsIgnoreCase("name")){						
							
							//'value' should always be there
							source = (String)aux.get("base");
						}
						
						if (term.equalsIgnoreCase("id")){						
						
							//'value' should always be there
							originalRef = (String)aux.get("value");
						}
				}
			}	
			
			
			
			//Fill in the POISource structure;
			Integer sourceId = (Source.getSourceClassByName(con, source,log)).getId();
			
			poisource = new POISource(null,sourceId,originalRef,null);
			return poisource;
				
			
			
		}catch(Exception ex){
			log.error("Error. CommonUtils.getPOISource(): "+ex.getMessage());
			System.out.println("Error. CommonUtils.getPOISource(): "+ex.getMessage());
			return null;
		}
		

		
		
	}

	
	private static void simpleLog(String str) {
		
		if (true) {
			System.out.println(str);
		}
		
		
	}

}
