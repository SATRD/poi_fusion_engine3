package org.upv.satrd.fic2.fe.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Logger;


//This data structure is used to store all source information as they are being fusioned. As the POI has not been stored in the DDBB; we cannot know the POIId
//Is is therefore by default null
public class POISource {
	
	private Integer poiId;
	private Integer sourceId;
	private String originalRef;
	private String poiproxyattribute;
	
		
	private static org.apache.log4j.Logger log =Logger.getLogger(org.upv.satrd.fic2.fe.db.POISource.class);
	
	//The POISource object requires poiId and sourceId identifiers to be previously known
	public POISource(Integer poiId, Integer sourceId, String originalRef, String poiproxyattribute ){
		this.poiId = poiId;
		this.sourceId = sourceId;
		this.originalRef = originalRef;
		this.poiproxyattribute = poiproxyattribute;
	};
	
		
	
	//GET METHODS
	public Integer getPoiId(){return this.poiId;}
	public Integer getSourceId(){return this.sourceId;}
	public String getOriginalRef(){return this.originalRef;}
	public String getPoiproxyAttribute(){return this.poiproxyattribute;}
	
	//SET METHODS
	public void setPOiId(int id){ this.poiId = id;}
	public void setSourceId(int id){this.sourceId =id;}
	public void setOriginalRef(String ref){this.originalRef = ref;}
	public void setPoiproxyAttribute(String atr){this.poiproxyattribute = atr;}	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	//Here we have a PK for <poiid, sourceId and poiproxyattribute>
	public static POISource getPOISourceClassByPOIId(Connection con, Integer poiid, Logger log){
		POISource poisource = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		Object aux;
		if (log == null) log = POISource.log;
		
		try {
			stmt = con.createStatement();
			String sql;
			
				sql = "SELECT * FROM poisource WHERE poiid="+poiid;
			
			
			rs = stmt.executeQuery(sql);			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){						
				
				//sourceId field mandatory
				Integer sourceId = new Integer((list.get(0)).get("sourceid").toString());
				
				//originalRef field mandatory
				String originalref = (list.get(0)).get("originalref").toString();
				
				//poiproxyattribute mandatory, but might be null. As PK is cannot be NULL, but empty String. If so, we might convert it back to null
				String poiproxyattribute = null;
				aux = (list.get(0)).get("poiproxyattribute");				
				if (aux!=null){
					poiproxyattribute = aux.toString();
					if (poiproxyattribute.isEmpty()) poiproxyattribute = null;
				}
										 
				poisource = new POISource(poiid, sourceId,originalref,poiproxyattribute);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error POISource.getPOISourceClassById(): "+e.getMessage());
			log.error("Error POISource.getPOISourceClassById(): "+e.getMessage());
		}
		
		return poisource;
		
	}
	
	
	//Here we have a PK for <poiid, sourceId and poiproxyattribute>
	public static POISource getPOISourceClassByPOIIdAndSourceIdAndPoiproxyAttribute(Connection con, 
			Integer poiid, Integer sourceId, String poiproxyattribute, Logger log){
		POISource poisource = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POISource.log;
		
		try {
			stmt = con.createStatement();
			String sql;
			if ( (poiproxyattribute == null) || (poiproxyattribute.isEmpty())){
				sql = "SELECT * FROM poisource WHERE sourceid="+sourceId+" AND poiid="+poiid+";";
			}else{				
				sql = "SELECT * FROM poisource WHERE sourceid="+sourceId+" AND poiid="+poiid+" AND poiproxyattribute='"+poiproxyattribute+"'";
			}
			
			rs = stmt.executeQuery(sql);			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){						
								
				//originalRef field mandatory
				String originalref = (list.get(0)).get("originalref").toString();		
										 
				poisource = new POISource(poiid, sourceId,originalref,poiproxyattribute);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error POISource.getPOISourceClassByPOIIdAndSourceIdAndPoiproxyAttribute(): "+e.getMessage());
			log.error("Error POISource.getPOISourceClassByPOIIdAndSourceIdAndPoiproxyAttribute(): "+e.getMessage());
		}
		
		return poisource;
		
	}
	
	
	
	
	
	//This method let us know if a POI has already been inserted in the database. Poiproxyattribute=null for datasources different as POIProxy
	public static POISource getPOISourceClassBySourceIdAndOriginalRefAndPoiproxyAttribute(Connection con, 
			Integer sourceId, String originalRef,String poiproxyattribute, Logger log){
		
		POISource poisource = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POISource.log;
		
		try {
			stmt = con.createStatement();
			String sql;
			if ( (poiproxyattribute == null) || (poiproxyattribute.isEmpty())  ){
				sql = "SELECT * FROM poisource WHERE sourceid="+sourceId+" AND originalref='"+originalRef+"';";
			}else{
				sql = "SELECT * FROM poisource WHERE sourceid="+sourceId+" AND originalref='"+originalRef+"' AND poiproxyattribute='"+poiproxyattribute+"'";
			}			
			
			rs = stmt.executeQuery(sql);			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//poiId field mandatory
				Integer poiId = new Integer((list.get(0)).get("poiid").toString());			
										 
				poisource = new POISource(poiId, sourceId,originalRef,poiproxyattribute);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error POISource.getPOISourceClassBySourceIdAndOriginalRefAndPoiproxyAttribute() : "+e.getMessage());
			log.error("Error POISource.getPOISourceClassBySourceIdAndOriginalRefAndPoiproxyAttribute() : "+e.getMessage());
		}
		
		return poisource;
		
	}
	
	
	public static boolean savePOISource(Connection con, POISource poisource, Logger log){
	
		
		String sql;
		PreparedStatement ps;		
		if (log == null) log = POISource.log;
		
			try{   	
				
				sql = "INSERT INTO poisource (poiid,sourceid,originalref,poiproxyattribute) VALUES (?,?,?,?)";	
	
				ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);	
				ps.setInt(1,poisource.getPoiId());
				ps.setInt(2,poisource.getSourceId());
				ps.setString(3,poisource.getOriginalRef());	
				//Though we may insert NULL in poiproxyattribute, as it is PK it cannot be null, so we insert an empty string
				if (poisource.getPoiproxyAttribute() == null){
					//ps.setNull(4, java.sql.Types.VARCHAR);
					ps.setString(4, "");
				}else ps.setString(4,poisource.getPoiproxyAttribute());	
				
				ps.executeUpdate();							
							
		        ps.close();
				
			} catch ( SQLException e) {
				System.out.println("Error POISource.savePOISource(): "+e.getMessage());
				log.error("Error POISource.savePOISource(): "+e.getMessage());
				return false;
			}	
	
		return true;
	}
	
	
	
	public static boolean deletePOISourceByPOIIdAndSourceIdAndPoiproxyAttribute(Connection con, 
			Integer poiid, Integer sourceId, String poiproxyattribute, Logger log){			
			
		String sql;		
		Statement stmt;
		if (log == null) log = POISource.log;
		
		
		try {			
			
			stmt = con.createStatement();			
			
			if ( (poiproxyattribute == null) || (poiproxyattribute.isEmpty()) ){
				sql = "DELETE FROM poisource WHERE sourceid="+sourceId+" AND poiid="+poiid+";";
			}else{				
				sql = "DELETE FROM poisource WHERE sourceid="+sourceId+" AND poiid="+poiid+" AND poiproxyattribute='"+poiproxyattribute+"';";
			}			
			
			stmt.executeUpdate(sql);
			
			stmt.close();			
				
		} catch ( SQLException e) {
			System.out.println("Error POISource.deletePOISourceById(): "+e.getMessage());
			log.error("Error POISource.deletePOISourceById(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
		
	
	
	
	
	
	
	public static ArrayList<POISource> getPOISourceList (Connection con, Logger log){
		
		ArrayList<POISource> poisource_array = new ArrayList<POISource>();
		String sql;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POISource.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM poisource ORDER BY name";
			rs = stmt.executeQuery(sql);
			Object aux=null;
			
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){	
				
				for (int k=0;k<list.size();k++){
				
					//id field mandatory
					Integer poiid = new Integer((list.get(k)).get("poiid").toString());
					
					//id field mandatory
					Integer sourceid = new Integer((list.get(k)).get("sourceid").toString());
					
					//originalref field mandatory
					String originalref = list.get(k).get("originalref").toString();	
					
					//poiproxyattribute mandatory, but might be null. As PK is cannot be NULL, but empty String. If so, we might convert it back to null
					String poiproxyattribute = null;
					aux = (list.get(k)).get("poiproxyattribute");				
					if (aux!=null){
						poiproxyattribute = aux.toString();
						if (poiproxyattribute.isEmpty()) poiproxyattribute = null;
					}				
											 
					POISource poisource = new POISource(poiid, sourceid, originalref,poiproxyattribute);
					poisource_array.add(poisource);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error POISource.getPOISourceList(): "+e.getMessage());
			log.error("Error POISource.getPOISourceList(): "+e.getMessage());
		}
		
		return poisource_array;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////Utility method///////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	
	public static ArrayList<HashMap<String, Object>> resultSetToArrayList(ResultSet rs) throws SQLException{
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    ArrayList<HashMap<String, Object>> results = new ArrayList<HashMap<String, Object>>();

	    while (rs.next()) {
	        HashMap<String, Object> row = new HashMap<String, Object>();
	        
	        for(int i=1; i<=columns; i++){
	          row.put(md.getColumnName(i).toLowerCase(),rs.getObject(i));
	        }
	        
	        results.add(row);
	    }
	    
	    return results;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////testing method///////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	
	public static void testPOISource(Connection con) throws ParseException {
		
		System.out.println("****************************************************************************************");
		System.out.println("POISource test. Creating, inserting,selecting and deleting an POISource object from DB");	
		log.info("****************************************************************************************");
		log.info("POISource test. Creating, inserting,selecting and deleting an POISource object from DB");	
		
		
		//Before creating a POISource, we need to previously create objects: Source,POI
		
		//Before creating source, we need previously a APIType object
		APIType apitype = new APIType("some_name","some_description","some_apirules");
		Integer apitypeId = APIType.saveAPIType(con, apitype,null);
		
			
		Source source = new Source("some_name","some_description","some_url_access","some_categorymapping",apitypeId);
		Integer sourceId = Source.saveSource(con, source,null);
		
		
		
		//Set the current time
		Calendar cal = Calendar.getInstance();				
		SimpleDateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");				
		String date1 = outputformat.format(cal.getTime());		
		java.util.Date date2 = outputformat.parse(date1);						
		java.sql.Date sqlDate = new java.sql.Date(date2.getTime());
		
		Double latitude = -0.345547;
		Double longitude = 39.473408;
		POI poi = new POI("some_name",latitude,longitude,sqlDate);
		Integer poiId = POI.savePOI(con, poi,null);
		
		
		
		String originalRef = "some_originalref";
		String poiproxyattribute = "some_poiproxyattribute";
		POISource poisource = new POISource(poiId,sourceId,originalRef,poiproxyattribute);
		System.out.println("Creating POISource class...... OK");
		log.info("Creating POISource class......OK");
		
		
		
		if (!POISource.savePOISource(con, poisource,null)){
			System.out.println("Saving POISource class...... Error ");
			log.info("Saving POISource class...... Error ");
		}else{
			System.out.println("Saving POISource class...... OK ");
			log.info("Saving POISource class...... OK ");
			
			POISource poisource2 = POISource.getPOISourceClassBySourceIdAndOriginalRefAndPoiproxyAttribute(con, sourceId, originalRef, poiproxyattribute,null);
			if (poisource2==null){
				System.out.println("Getting POISource class by SourceIdAndOriginalRefAndPoiproxyAttribute...... Error ");
				log.info("Getting POISource class by SourceIdAndOriginalRefAndPoiproxyAttribute...... Error ");
			}else{
				System.out.println("Getting POISource class by SourceIdAndOriginalRefAndPoiproxyAttribute...... OK ");
				log.info("Getting POISource class by SourceIdAndOriginalRefAndPoiproxyAttribute...... OK ");
				
				poisource2 = POISource.getPOISourceClassByPOIIdAndSourceIdAndPoiproxyAttribute(con, poiId, sourceId, poiproxyattribute,null);
				if (poisource2 == null){
					System.out.println("Getting POISource class by POIIdAndSourceIdAndPoiproxyAttribute...... Error ");
					log.info("Getting POISource class by POIIdAndSourceIdAndPoiproxyAttribute...... Error ");
				}else{
					System.out.println("Getting POISource class by POIIdAndSourceIdAndPoiproxyAttribute...... OK ");
					log.info("Getting POISource class by POIIdAndSourceIdAndPoiproxyAttribute...... OK ");
					
					if (!POISource.deletePOISourceByPOIIdAndSourceIdAndPoiproxyAttribute(con, poiId, sourceId, poiproxyattribute,null)){
						System.out.println("Deleting POISource class by ID ...... Error ");
						log.info("Deleting POISource class by ID ...... Error  ");
					}else{
						System.out.println("Deleting POISource class by ID ...... OK ");
						log.info("Deleting POISource class by ID ...... OK  ");
						
						System.out.println("All tests passed OK");
						log.info("All tests passed OK");
						
					}
					
				}
				
			}
		}
		
		System.out.println("****************************************************************************************");
		log.info("****************************************************************************************");
		
		
	}
	
	

	

	

}
