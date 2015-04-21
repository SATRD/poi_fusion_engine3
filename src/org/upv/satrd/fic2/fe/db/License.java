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


public class License {

	
	private Integer id; //id in the database. This might be unknown if it has not yet been inserted in the DB
	private String name;
	private String description;
	private String info;
		
	private static org.apache.log4j.Logger log = Logger.getLogger(org.upv.satrd.fic2.fe.db.License.class);
	
	
	
	//The License object has been inserted in the DDB, and the id is known
	public License(Integer id, String name, String description,String info){
		this.id = id;
		this.name = name;
		this.description = description;
		this.info = info;			
	}
	
	
	//The License object has not been inserted in the DDB, and the id is not known
	public License(String name, String description,String info){
		this.id = null;
		this.name = name;
		this.description = description;
		this.info = info;		
	}
	
	
	//GET METHODS
	public Integer getId(){return this.id;}
	public String getName(){return this.name;}
	public String getDescription(){return this.description;}
	public String getInfo(){return this.info;}
	
	
	//SET METHODS
	public void setId(Integer id){this.id = id;}
	public void setName(String name){this.name = name;}
	public void setDescription(String description){this.description = description;}
	public void setInfo(String info){this.info = info;}
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static License getLicenseClassById(Connection con, Integer id, Logger log){
		License license = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = License.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM license WHERE id="+id;
			rs = stmt.executeQuery(sql);
			Object aux = null;
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//name field mandatory
				String name = (list.get(0)).get("name").toString();
				
				//description field optional
				String description = null;
				aux = (list.get(0)).get("description");				
				if (aux!=null) description = aux.toString();
								
				//info field optional 
				String info = null;
				aux = (list.get(0)).get("info");
				if (aux!=null) info = aux.toString();
										 
				license = new License(id, name, description,info);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error License.getLicenseClassById(): "+e.getMessage());
			log.error("Error License.getLicenseClassById(): "+e.getMessage());
		}
		
		return license;
		
	}
	
	
	
	public static License getLicenseClassByName(Connection con, String name, Logger log){
		License license = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = License.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM license WHERE name='"+name+"'";
			rs = stmt.executeQuery(sql);
			Object aux = null;
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//id field mandatory
				Integer id = new Integer((list.get(0)).get("id").toString());
				
				//description field optional
				String description = null;
				aux = (list.get(0)).get("description");				
				if (aux!=null) description = aux.toString();
								
				//info field optional 
				String info = null;
				aux = (list.get(0)).get("info");
				if (aux!=null) info = aux.toString();
										 
				license = new License(id, name, description,info);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error License.getLicenseClassByName() : "+e.getMessage());
			log.error("Error License.getLicenseClassByName() : "+e.getMessage());
		}
		
		return license;
		
	}
	
	
	public static Integer saveLicense(Connection con, License license, Logger log){
	
	
		Integer id=null;		
		String sql;
		PreparedStatement ps;	
			if (log == null) log = License.log;
		
			try{   	
				
				sql = "INSERT INTO license (name,description,info) VALUES (?,?,?)";	
	
				String generatedColumns[] = { "id" };
				ps = con.prepareStatement(sql,
						generatedColumns);
				
				ps.setString(1,license.getName());				
				if (license.getDescription() == null) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2,license.getDescription());	
				if (license.getInfo() == null) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3,license.getInfo());							
				
				//log.debug(ps.toString());
				ps.executeUpdate();
				
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) { id = rs.getInt(1); }	
								
				rs.close();				
		        ps.close();
				
			} catch ( SQLException e) {
				System.out.println("Error License.saveLicense(): "+e.getMessage());
				log.error("Error License.saveLicense(): "+e.getMessage());
			}	
	
		return id;
	}
	
	
	
	public static boolean deleteLicenseById(Connection con, Integer id, Logger log){			
			
		String sql;		
		Statement stmt;
		if (log == null) log = License.log;
		
		try {
			
			//FIXME: We need to delete all POILabels that relate to that License
			if (POILabel.deletePOILabelByLicenseId(con, id,log)){
				stmt = con.createStatement();
				
				//Delete all dependencies with other tables (licensesource )
				sql = "DELETE FROM licensesource WHERE licenseid="+id+";";
				stmt.executeUpdate(sql);
				
				sql = "DELETE FROM license WHERE id="+id+";";
				stmt.executeUpdate(sql);
				stmt.close();
			}else return false;
				
		} catch ( SQLException e) {
			System.out.println("Error License.deleteLicenseById(): "+e.getMessage());
			log.error("Error License.deleteLicenseById(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	
	
	public static boolean deleteLicenseByName(Connection con, String name, Logger log){			
		
		String sql;		
		Statement stmt;
		if (log == null) log = License.log;
		
		try {
			stmt = con.createStatement();
			sql = "DELETE FROM license WHERE name='"+name+"';";
			stmt.executeUpdate(sql);
			stmt.close();
				
		} catch ( SQLException e) {
			System.out.println("Error License.deleteLicenseByName(): "+e.getMessage());
			log.error("Error License.deleteLicenseByName(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	
	
	
	public static boolean updateLicense(Connection con, License license, Logger log){
				
		String sql;
		PreparedStatement ps;
		if (log == null) log = License.log;
		
			try{   	
				
				sql = "UPDATE license SET name=?, description=?, info=? WHERE id="+license.getId();
	
				ps = con.prepareStatement(sql);	
				ps.setString(1,license.getName());				
				if (license.getDescription() == null) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2,license.getDescription());	
				if (license.getInfo() == null) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3,license.getInfo());							
				
				ps.executeUpdate();
				
				ps.close();								
				
				
			} catch ( SQLException e) {
				System.out.println("Error License.updateLicense(): "+e.getMessage());
				log.error("Error License.updateLicense(): "+e.getMessage());
				return false;
			}	
	
		return true;
	}
	
	
	
	
	public static ArrayList<License> getLicenseList (Connection con, Logger log){
		
		ArrayList<License> license_array = new ArrayList<License>();
		String sql;
		Statement stmt;
		ResultSet rs;
		
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = License.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM license ORDER BY name";
			rs = stmt.executeQuery(sql);
			Object aux;
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){		
				
				for (int k=0;k<list.size();k++){
				
					//id field mandatory
					Integer id = new Integer((list.get(k)).get("id").toString());
					
					//name field mandatory
					String name = list.get(k).get("name").toString();
					
					//description field optional
					String description = null;
					aux = (list.get(k)).get("description");				
					if (aux!=null) description = aux.toString();
									
					//info field optional 
					String info = null;
					aux = (list.get(k)).get("info");
					if (aux!=null) info = aux.toString();
											 
					License license = new License(id, name, description,info);
					license_array.add(license);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error License.getLicenseList(): "+e.getMessage());
			log.error("Error License.getLicenseList(): "+e.getMessage());
		}
		
		return license_array;
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
	
	public static void testLicense(Connection con) {
		System.out.println("****************************************************************************************");
		System.out.println("License test. Creating, inserting,selecting and deleting a License object from DB");	
		log.info("****************************************************************************************");
		log.info("License test. Creating, inserting,selecting and deleting a License object from DB");	
		
		String name = "some_name";
		String description = "some_description";
		String information = "some_information";
		License license = new License(name,description,information);
		System.out.println("Creating license class...... OK");
		log.info("Creating license class......OK");
		
		
		Integer id = License.saveLicense(con, license,null);
		if (id==null){
			System.out.println("Saving license class...... Error ");
			log.info("Saving license class...... Error ");
		}else{
			System.out.println("Saving license class...... OK ");
			log.info("Saving license class...... OK ");
			
			License license2 = License.getLicenseClassById(con, id,null);
			if (license2==null){
				System.out.println("Getting license class by ID...... Error ");
				log.info("Getting license class by ID...... Error ");
			}else{
				System.out.println("Getting license class by ID...... OK ");
				log.info("Getting license class by ID...... OK ");
				
				license2 = License.getLicenseClassByName(con, name,null);
				if (license2 == null){
					System.out.println("Getting license class by Name...... Error ");
					log.info("Getting license class by Name...... Error ");
				}else{
					System.out.println("Getting license class by Name...... OK ");
					log.info("Getting license class by Name...... OK ");
					
					if (!License.deleteLicenseById(con, id,null)){
						System.out.println("Deleting license class by ID ...... Error ");
						log.info("Deleting license class by ID ...... Error  ");
					}else{
						System.out.println("Deleting license class by ID ...... OK ");
						log.info("Deleting license class by ID ...... OK  ");
						
						id = License.saveLicense(con, license,null);
						if (id!=null){
							if (!License.deleteLicenseByName(con, name,null)){
								System.out.println("Deleting license class by Name ...... Error ");
								log.info("Deleting license class by Name ...... Error  ");
							}else{
								System.out.println("Deleting license class by Name ...... OK ");
								log.info("Deleting license class by Name ...... OK  ");
								System.out.println("All tests passed OK");
								log.info("All tests passed OK");
							}
						}
					}
					
				}
				
			}
		}
		
		
		System.out.println("****************************************************************************************");
		log.info("****************************************************************************************");
		
		
	}
	
	
}
