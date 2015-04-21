package org.upv.satrd.fic2.fe.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;


public class OCDCategory {

	
	
	private Integer ocdid;
	private Integer categoryid; 
	
		
	private static org.apache.log4j.Logger log = Logger.getLogger(org.upv.satrd.fic2.fe.db.OCDCategory.class);
	
	
	
	
	public OCDCategory(Integer ocdid, Integer categoryid){		
		this.ocdid = ocdid;		
		this.categoryid = categoryid;
	}
	
	
	
	
	//GET METHODS	
	public Integer getOCDId(){return this.ocdid;}
	public Integer getCategoryId(){return this.categoryid;}
	
	
	
	//SET METHODS	
	public void setOCDId(Integer id){this.ocdid = id;}
	public void setCategoryId(Integer id){this.categoryid = id;}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static ArrayList<OCDCategory> getOCDCategoryListByCategoryId (Connection con, Integer categoryid, Logger log){
		
		ArrayList<OCDCategory> ocdcategory_array = new ArrayList<OCDCategory>();
		String sql;
		Statement stmt;
		ResultSet rs;
		
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = OCDCategory.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM ocdcategory WHERE categoryid="+categoryid;
			rs = stmt.executeQuery(sql);
			//Object aux;
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){		
				
				for (int k=0;k<list.size();k++){
				
					//license4d field mandatory
					Integer ocdid = new Integer((list.get(k)).get("ocdid").toString());
					
					
											 
					OCDCategory ocdcategory = new OCDCategory(ocdid,categoryid);
					ocdcategory_array.add(ocdcategory);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error OCDCategory.getOCDCategoryListByCategoryId(): "+e.getMessage());
			log.error("Error OCDCategory.getOCDCategoryListByCategoryId(): "+e.getMessage());
		}
		
		return ocdcategory_array;
	}
		
	
 public static ArrayList<OCDCategory> getOCDCategoryListByOCDId (Connection con, Integer ocdid, Logger log){
		
		ArrayList<OCDCategory> ocdcategory_array = new ArrayList<OCDCategory>();
		String sql;
		Statement stmt;
		ResultSet rs;
		
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = OCDCategory.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM ocdcategory WHERE ocdid="+ocdid;
			rs = stmt.executeQuery(sql);
			//Object aux;
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){		
				
				for (int k=0;k<list.size();k++){
				
					//license4d field mandatory
					Integer categoryid = new Integer((list.get(k)).get("categoryid").toString());
					
					
											 
					OCDCategory ocdcategory = new OCDCategory(ocdid,categoryid);
					ocdcategory_array.add(ocdcategory);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error OCDCategory.getOCDCategoryListByOCDId(): "+e.getMessage());
			log.error("Error OCDSource.getOCDSourceListByOCDId(): "+e.getMessage());
		}
		
		return ocdcategory_array;
	}
	
	
	public static boolean saveOCDCategory(Connection con, OCDCategory ocdcategory, Logger log){
	
	
		boolean ok = false;		
		String sql;
		PreparedStatement ps;	
		if (log == null) log = OCDCategory.log;
		
			try{   	
				
				sql = "INSERT INTO ocdcategory (ocdid,categoryid) VALUES (?,?)";	
	
				
				ps = con.prepareStatement(sql);
				
				ps.setInt(1,ocdcategory.getOCDId());	
				ps.setInt(2,ocdcategory.getCategoryId());								
				
				ps.executeUpdate();		
								
		        ps.close();
		        
		        ok = true;
				
			} catch ( SQLException e) {
				System.out.println("Error OCDSource.saveOCDCategory(): "+e.getMessage());
				log.error("Error OCDSource.saveOCDCategory(): "+e.getMessage());
			}	
	
		return ok;
	}
	
	
	
	public static boolean deleteOCDCategoryByCategoryId(Connection con, Integer categoryid, Logger log){			
			
		String sql;		
		Statement stmt;
		if (log == null) log = OCDCategory.log;
		
		try {			
				stmt = con.createStatement();				
				sql = "DELETE FROM ocdcategory WHERE categoryid="+categoryid+";";
				stmt.executeUpdate(sql);			
				stmt.close();				
		} catch ( SQLException e) {
			System.out.println("Error OCDSource.deleteOCDCategoryByCategoryId(): "+e.getMessage());
			log.error("Error OCDSource.deleteOCDCategoryByCategoryId(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	public static boolean deleteOCDSourceByOCDId(Connection con, Integer ocdid, Logger log){			
		
		String sql;		
		Statement stmt;
		if (log == null) log = OCDCategory.log;
		
		try {			
				stmt = con.createStatement();				
				sql = "DELETE FROM ocdcategory WHERE ocdid="+ocdid+";";
				stmt.executeUpdate(sql);			
				stmt.close();				
		} catch ( SQLException e) {
			System.out.println("Error OCDSource.deleteOCDSourceByOCDId(): "+e.getMessage());
			log.error("Error OCDSource.deleteOCDSourceByOCDId(): "+e.getMessage());
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
	
	
	
	
}
