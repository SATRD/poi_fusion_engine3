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

public class POICategory {

	
	private Integer poiId;
	private Integer categoryId;	
	
	
	private static org.apache.log4j.Logger log =Logger.getLogger(org.upv.satrd.fic2.fe.db.POICategory.class);
	
	
	
	//The POICategory object has been inserted in the DDB, and the id is known
	public POICategory(Integer poiId, Integer categoryId){
		this.poiId = poiId;
		this.categoryId = categoryId;
	}
	
	
	
	//GET METHODS
	public Integer getPOIid(){ return this.poiId;}
	public Integer getCategoryId(){ return this.categoryId;}
	
	
	//SET METHODS
	public void setPOIid(Integer id){ this.poiId = id;}
	public void setCategoryId(Integer id) {this.categoryId = id;}
	

	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static POICategory getPOICategoryClassByPOIIdAndCategoryId(Connection con, Integer poiId,Integer categoryId, Logger log){
		POICategory poicategory = null;
		Statement stmt ;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POICategory.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM poicategory WHERE poiid="+poiId+" AND categoryid="+categoryId;
			rs = stmt.executeQuery(sql);			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
						
										 
				poicategory = new POICategory(poiId, categoryId);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error POICategory.getPOICategoryClassByIdAndCategoryId(): "+e.getMessage());
			log.error("Error POICategory.getPOICategoryClassByIdAndCategoryId(): "+e.getMessage());
		}
		
		return poicategory;
		
	}
	
	public static ArrayList<POICategory> getPOICategoryListByPOIId(Connection con, Integer poiId,Logger log){
		ArrayList <POICategory> poicategory_array = new ArrayList<POICategory>();
		POICategory  poicategory = null;
		Statement stmt ;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POICategory.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM poicategory WHERE poiid="+poiId;
			rs = stmt.executeQuery(sql);			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
				for (int k=0;k<list.size();k++){
					
					poicategory = new POICategory(poiId, new Integer((list.get(k)).get("categoryid").toString()));
					poicategory_array.add(poicategory);
				}
				
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error POICategory.getPOICategoryClassByPOIId(): "+e.getMessage());
			log.error("Error POICategory.getPOICategoryClassByPOIId(): "+e.getMessage());
		}
		
		return poicategory_array;
		
	}
	
	
	
	
	
	
	public static boolean savePOICategory(Connection con, POICategory poicategory, Logger log){			
		String sql;
		PreparedStatement ps;	
		if (log == null) log = POICategory.log;
			try{   	
				
				sql = "INSERT INTO poicategory (poiid,categoryid) VALUES (?,?)";	
	
				ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);	
				ps.setInt(1,poicategory.getPOIid());
				ps.setInt(2, poicategory.getCategoryId());
				
				ps.executeUpdate();							
							
		        ps.close();
		        return true;
				
			} catch ( SQLException e) {
				System.out.println("Error POICategory.savePOICategory(): "+e.getMessage());
				log.error("Error POICategory.savePOICategory(): "+e.getMessage());
				return false;
			}			
	}
	
	
	
	public static boolean deletePOICategoryByPOIidAndCategoryId(Connection con, Integer poiId,Integer categoryId, Logger log){		
			
		String sql;	
		Statement stmt ;
		if (log == null) log = POICategory.log;
		
		try {
			
			
			
				stmt = con.createStatement();
								
				sql = "DELETE FROM poicategory WHERE poiid="+poiId+" AND categoryid="+categoryId+";";
				stmt.executeUpdate(sql);
				
				stmt.close();
			
				
		} catch ( SQLException e) {
			System.out.println("Error POICategory.deletePOICategoryByPOIidAndCategoryId(): "+e.getMessage());
			log.error("Error POICategory.deletePOICategoryByPOIidAndCategoryId(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	
	
	
	
	
	
	public static boolean updatePOICategory(Connection con, POICategory poicategory, Logger log){
				
		String sql;
		PreparedStatement ps;	
		if (log == null) log = POICategory.log;
		
			try{   	
				
				sql = "UPDATE poicategory SET poiid=?, categoryid=? ;";
				ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1,poicategory.getPOIid());
				ps.setInt(2, poicategory.getCategoryId());						
				ps.executeUpdate();				
				ps.close();								
				
				
			} catch ( SQLException e) {
				System.out.println("Error POICategory.updatePOICategory(): "+e.getMessage());
				log.error("Error POICategory.updatePOICategory(): "+e.getMessage());
				return false;
			}	
	
		return true;
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
	
	public static void testPOICategory(Connection con) throws ParseException{
		System.out.println("****************************************************************************************");
		System.out.println("POICategory test. Creating, inserting,selecting and deleting a POICategory object from DB");	
		log.info("****************************************************************************************");
		log.info("POICategory test. Creating, inserting,selecting and deleting a POICategory object from DB");	
		
		//We need to create previously a Category and a POI in order to have valid IDs
		String name_cat = "some_name";
		String description = "some_description";
		Integer level = 1;
		String icon = "some_icon_url";
		Category category = new Category(name_cat,description,level,icon);
		
		
		String name_poi = "some_name";
		Double latitude = -0.345547;
		Double longitude = 39.473408;
		//Set the current time
		Calendar cal = Calendar.getInstance();				
		SimpleDateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");				
		String date1 = outputformat.format(cal.getTime());		
		java.util.Date date2 = outputformat.parse(date1);						
		java.sql.Date sqlDate = new java.sql.Date(date2.getTime());		
		POI poi = new POI(name_poi,latitude,longitude,sqlDate);
		
		
		
		Integer poiId = POI.savePOI(con, poi,null);  	
		Integer categoryId= Category.saveCategory(con, category,null);
		
		POICategory poicategory = new POICategory(poiId,categoryId);
		System.out.println("Creating POICategory class...... OK");
		log.info("Creating POICategory class......OK");
		
		
		boolean bool = POICategory.savePOICategory(con, poicategory,null);
		if (!bool){
			System.out.println("Saving POICategory class...... Error ");
			log.info("Saving POICategory class...... Error ");
		}else{
			System.out.println("Saving POICategory class...... OK ");
			log.info("Saving POICategory class...... OK ");
			
			POICategory poicategory2 = POICategory.getPOICategoryClassByPOIIdAndCategoryId(con, poiId, categoryId,null);
			if (poicategory2==null){
				System.out.println("Getting POICategory class by POIiD and CategoryId ...... Error ");
				log.info("Getting POICategory class by POIiD and CategoryId...... Error ");
			}else{
				System.out.println("Getting POICategory class by POIiD and CategoryId..... OK ");
				log.info("Getting POICategory class by POIiD and CategoryId...... OK ");
				
				if (!POICategory.deletePOICategoryByPOIidAndCategoryId(con, poiId, categoryId,null)){
					System.out.println("Deleting POICategory class by POIiD and CategoryId...... Error ");
					log.info("Deleting POICategory class by POIiD and CategoryId ...... Error  ");
					
				}else{
					System.out.println("Deleting POICategory class by POIiD and CategoryId ...... OK ");
					log.info("Deleting POICategory class by POIiD and CategoryId ...... OK  ");
					System.out.println("All tests passed OK");
					log.info("All tests passed OK");			
					
				}				
			}
		}
		
		System.out.println("****************************************************************************************");
		log.info("****************************************************************************************");
		
		
	}
	
	


	
}