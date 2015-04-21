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


public class LicenseSource {

	
	private Integer sourceid; 
	private Integer licenseid;
	
		
	private static org.apache.log4j.Logger log = Logger.getLogger(org.upv.satrd.fic2.fe.db.LicenseSource.class);
	
	
	
	
	public LicenseSource(Integer sourceid, Integer licenseid){
		this.sourceid = sourceid;
		this.licenseid = licenseid;					
	}
	
	
	
	
	//GET METHODS
	public Integer getSourceId(){return this.sourceid;}
	public Integer getLicenseId(){return this.licenseid;}
	
	
	
	//SET METHODS
	public void setSourceId(Integer id){this.sourceid = id;}
	public void setLicenseId(Integer id){this.licenseid = id;}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static ArrayList<LicenseSource> getLicenseSourceListBySourceId (Connection con, Integer idsource, Logger log){
		
		ArrayList<LicenseSource> licensesource_array = new ArrayList<LicenseSource>();
		String sql;
		Statement stmt;
		ResultSet rs;
		
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = LicenseSource.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM licensesource WHERE sourceid="+idsource;
			rs = stmt.executeQuery(sql);
			//Object aux;
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){		
				
				for (int k=0;k<list.size();k++){
				
					//license4d field mandatory
					Integer licenseid = new Integer((list.get(k)).get("licenseid").toString());
					
					
											 
					LicenseSource licensesource = new LicenseSource(idsource, licenseid);
					licensesource_array.add(licensesource);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error License.getLicensesourceListBySourceId(): "+e.getMessage());
			log.error("Error License.getLicensesourceListBySourceId(): "+e.getMessage());
		}
		
		return licensesource_array;
	}
		
	
	
	public static boolean saveLicenseSource(Connection con, LicenseSource licensesource, Logger log){
	
	
		boolean ok = false;		
		String sql;
		PreparedStatement ps;		
		if (log == null) log = LicenseSource.log;
			try{   	
				
				sql = "INSERT INTO licensesource (sourceid,licenseid) VALUES (?,?)";	
	
				
				ps = con.prepareStatement(sql);
				
				ps.setInt(1,licensesource.getSourceId());	
				ps.setInt(2,licensesource.getLicenseId());								
				
				ps.executeUpdate();		
								
		        ps.close();
		        ok = true;
				
			} catch ( SQLException e) {
				System.out.println("Error LicenseSource.saveLicense(): "+e.getMessage());
				log.error("Error LicenseSource.saveLicense(): "+e.getMessage());
			}	
	
		return ok;
	}
	
	
	
	public static boolean deleteLicenseSourceBySourceId(Connection con, Integer idsource, Logger log){			
			
		String sql;		
		Statement stmt;
		if (log == null) log = LicenseSource.log;
		
		try {			
				stmt = con.createStatement();				
				sql = "DELETE FROM licensesource WHERE sourceid="+idsource+";";
				stmt.executeUpdate(sql);			
				stmt.close();				
		} catch ( SQLException e) {
			System.out.println("Error LicenseSource.deleteLicenseSourceBySourceId(): "+e.getMessage());
			log.error("Error LicenseSource.deleteLicensesourceBySourceId(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	public static boolean deleteLicenseSourceByLicenseId(Connection con, Integer idlicense, Logger log){			
		
		String sql;		
		Statement stmt;
		if (log == null) log = LicenseSource.log;
		
		try {			
				stmt = con.createStatement();					
				sql = "DELETE FROM licensesource WHERE licenseid="+idlicense+";";
				stmt.executeUpdate(sql);				
				stmt.close();				
		} catch ( SQLException e) {
			System.out.println("Error LicenseSource.deleteLicenseSourceByLicenseId(): "+e.getMessage());
			log.error("Error LicenseSource.deleteLicensesourceByLicenseId(): "+e.getMessage());
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
