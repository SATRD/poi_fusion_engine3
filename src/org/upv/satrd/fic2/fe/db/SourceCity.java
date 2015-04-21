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


public class SourceCity {

	
	private Integer sourceid; 
	private Integer cityid;
	
		
	private static org.apache.log4j.Logger log = Logger.getLogger(org.upv.satrd.fic2.fe.db.SourceCity.class);
	
	
	
	
	public SourceCity(Integer sourceid, Integer cityid){
		this.sourceid = sourceid;
		this.cityid = cityid;					
	}
	
	
	
	
	//GET METHODS
	public Integer getSourceId(){return this.sourceid;}
	public Integer getCityId(){return this.cityid;}
	
	
	
	//SET METHODS
	public void setSourceId(Integer id){this.sourceid = id;}
	public void setCityId(Integer id){this.cityid = id;}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static ArrayList<SourceCity> getSourceCityListBySourceId (Connection con, Integer idsource, Logger log){
		
		ArrayList<SourceCity> sourcecity_array = new ArrayList<SourceCity>();
		String sql;
		Statement stmt;
		ResultSet rs;
		
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = SourceCity.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM sourcecity WHERE sourceid="+idsource;
			rs = stmt.executeQuery(sql);
			//Object aux;
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){		
				
				for (int k=0;k<list.size();k++){
				
					//license4d field mandatory
					Integer cityid = new Integer((list.get(k)).get("cityid").toString());
					
					
											 
					SourceCity sourcecity = new SourceCity(idsource, cityid);
					sourcecity_array.add(sourcecity);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error SourceCity.getSourceCityListBySourceId(): "+e.getMessage());
			log.error("Error SourceCity.getSourceCityListBySourceId(): "+e.getMessage());
		}
		
		return sourcecity_array;
	}
		
	
	
	public static boolean saveSourceCity(Connection con, SourceCity sourcecity, Logger log){
	
	
		boolean ok = false;		
		String sql;
		PreparedStatement ps;		
		if (log == null) log = SourceCity.log;
		
			try{   	
				
				sql = "INSERT INTO sourcecity (sourceid,cityid) VALUES (?,?)";	
	
				
				ps = con.prepareStatement(sql);
				
				ps.setInt(1,sourcecity.getSourceId());	
				ps.setInt(2,sourcecity.getCityId());								
				
				ps.executeUpdate();		
								
		        ps.close();
		        
		        ok = true;
				
			} catch ( SQLException e) {
				System.out.println("Error SourceCity.saveSourceCity(): "+e.getMessage());
				log.error("Error SourceCity.saveSourceCity(): "+e.getMessage());
			}	
	
		return ok;
	}
	
	
	
	public static boolean deleteSourceCityBySourceId(Connection con, Integer idsource, Logger log){			
			
		String sql;		
		Statement stmt;
		if (log == null) log = SourceCity.log;
		
		try {			
				stmt = con.createStatement();				
				sql = "DELETE FROM sourcecity WHERE sourceid="+idsource+";";
				stmt.executeUpdate(sql);			
				stmt.close();				
		} catch ( SQLException e) {
			System.out.println("Error Sourcecity.deleteSourceCityBySourceId(): "+e.getMessage());
			log.error("ErrorSourcecity.deleteSourceCityBySourceId(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	public static boolean deleteSourceCityByCityId(Connection con, Integer idcity, Logger log){			
		
		String sql;		
		Statement stmt;
		if (log == null) log = SourceCity.log;
		
		try {			
				stmt = con.createStatement();				
				sql = "DELETE FROM sourcecity WHERE cityid="+idcity+";";
				stmt.executeUpdate(sql);			
				stmt.close();				
		} catch ( SQLException e) {
			System.out.println("Error Sourcecity.deleteSourceCityByCityId(): "+e.getMessage());
			log.error("Error Sourcecity.deleteSourceCityByCityId(): "+e.getMessage());
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
