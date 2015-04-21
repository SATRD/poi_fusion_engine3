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


public class OCDSource {

	
	private Integer sourceid; 
	private Integer ocdid;
	
		
	private static org.apache.log4j.Logger log = Logger.getLogger(org.upv.satrd.fic2.fe.db.OCDSource.class);
	
	
	
	
	public OCDSource(Integer ocdid, Integer sourceid){
		this.sourceid = sourceid;
		this.ocdid = ocdid;					
	}
	
	
	
	
	//GET METHODS
	public Integer getSourceId(){return this.sourceid;}
	public Integer getOCDId(){return this.ocdid;}
	
	
	
	//SET METHODS
	public void setSourceId(Integer id){this.sourceid = id;}
	public void setOCDId(Integer id){this.ocdid = id;}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static ArrayList<OCDSource> getOCDSourceListBySourceId (Connection con, Integer sourceid, Logger log){
		
		ArrayList<OCDSource> ocdsource_array = new ArrayList<OCDSource>();
		String sql;
		Statement stmt;
		ResultSet rs;
		
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = OCDSource.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM ocdsource WHERE sourceid="+sourceid;
			rs = stmt.executeQuery(sql);
			//Object aux;
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){		
				
				for (int k=0;k<list.size();k++){
				
					//license4d field mandatory
					Integer ocdid = new Integer((list.get(k)).get("ocdid").toString());
					
					
											 
					OCDSource ocdsource = new OCDSource(ocdid,sourceid);
					ocdsource_array.add(ocdsource);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error OCDSource.getOCDSourceListBySourceId(): "+e.getMessage());
			log.error("Error OCDSource.getOCDSourceListBySourceId(): "+e.getMessage());
		}
		
		return ocdsource_array;
	}
		
	
 public static ArrayList<OCDSource> getOCDSourceListByOCDId (Connection con, Integer ocdid, Logger log){
		
		ArrayList<OCDSource> ocdsource_array = new ArrayList<OCDSource>();
		String sql;
		Statement stmt;
		ResultSet rs;
		
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = OCDSource.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM ocdsource WHERE ocdid="+ocdid;
			rs = stmt.executeQuery(sql);
			//Object aux;
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){		
				
				for (int k=0;k<list.size();k++){
				
					//license4d field mandatory
					Integer sourceid = new Integer((list.get(k)).get("sourceid").toString());
					
					
											 
					OCDSource ocdsource = new OCDSource(ocdid,sourceid);
					ocdsource_array.add(ocdsource);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error OCDSource.getOCDSourceListBySourceId(): "+e.getMessage());
			log.error("Error OCDSource.getOCDSourceListBySourceId(): "+e.getMessage());
		}
		
		return ocdsource_array;
	}
	
	
	public static boolean saveOCDSource(Connection con, OCDSource ocdsource, Logger log){
	
	
		boolean ok = false;		
		String sql;
		PreparedStatement ps;	
		if (log == null) log = OCDSource.log;
		
			try{   	
				
				sql = "INSERT INTO ocdsource (ocdid,sourceid) VALUES (?,?)";	
	
				
				ps = con.prepareStatement(sql);
				
				ps.setInt(1,ocdsource.getOCDId());	
				ps.setInt(2,ocdsource.getSourceId());								
				
				ps.executeUpdate();		
								
		        ps.close();
		        
		        ok = true;
				
			} catch ( SQLException e) {
				System.out.println("Error OCDSource.saveOCDSource(): "+e.getMessage());
				log.error("Error OCDSource.saveOCDSource(): "+e.getMessage());
			}	
	
		return ok;
	}
	
	
	
	public static boolean deleteOCDSourceBySourceId(Connection con, Integer sourceid, Logger log){			
			
		String sql;		
		Statement stmt;
		if (log == null) log = OCDSource.log;
		
		try {			
				stmt = con.createStatement();				
				sql = "DELETE FROM ocdsource WHERE sourceid="+sourceid+";";
				stmt.executeUpdate(sql);			
				stmt.close();				
		} catch ( SQLException e) {
			System.out.println("Error OCDSource.deleteOCDSourceBySourceId(): "+e.getMessage());
			log.error("Error OCDSource.deleteOCDSourceBySourceId(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	public static boolean deleteOCDSourceByOCDId(Connection con, Integer ocdid, Logger log){			
		
		String sql;		
		Statement stmt;
		if (log == null) log = OCDSource.log;
		
		try {			
				stmt = con.createStatement();				
				sql = "DELETE FROM ocdsource WHERE cityid="+ocdid+";";
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
