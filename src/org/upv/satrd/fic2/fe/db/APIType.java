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


public class APIType {
	
	private Integer id;
	private String name;
	private String description;	
	private String apirules;
	
		
	private static org.apache.log4j.Logger log =Logger.getLogger(org.upv.satrd.fic2.fe.db.APIType.class);
	
	
	//The APIType object has been inserted in the DDB, and the id is known
	public APIType(Integer id, String name, String description, String apirules ) {    
		this.id = id;
		this.name = name;		
		this.description = description;
		this.apirules = apirules;		
	}
	
	//The APIType object has been inserted in the DDB, and the id is known
	public APIType(String name, String description, String apirules ) {    
		this.id = null;
		this.name = name;		
		this.description = description;
		this.apirules = apirules;		
	}
	
	
	//GET METHODS
	public void setId(Integer id){this.id = id;}
	public void setName(String name){this.name =name;}	
	public void setDescription(String description){this.description=description;}
	public void setAPIRules(String apirules){this.apirules=apirules;}
	
	
	
	
	//SET METHODS
	public Integer getId() {return  id;}		
	public String getName() {return name;}
	public String getDescription() {return description;}
	public String getAPIRules() {return apirules;}
	

	
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static APIType getAPITypeClassById(Connection con, Integer id, Logger log){
		APIType apitype = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		
		if (log == null) log = APIType.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM apitype WHERE id="+id;
			rs = stmt.executeQuery(sql);
			Object aux= null;
			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//name field mandatory
				String name = (list.get(0)).get("name").toString();
				
				//description field optional
				String description = null;
				aux = (list.get(0)).get("description");				
				if (aux!=null) description = aux.toString();
				
				//apirules field optional
				String apirules = null;
				aux = (list.get(0)).get("apirules");				
				if (aux!=null) apirules = aux.toString();
				
										 
				apitype = new APIType(id, name,description,apirules);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error APIType.getAPITypeClassById(): "+e.getMessage());
			log.error("Error APIType.getAPITypeClassById(): "+e.getMessage());
		}
		
		return apitype;
		
	}
	
	
	
	public static APIType getAPITypeClassByName(Connection con, String name, Logger log){
		APIType apitype = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		
		if (log == null) log = APIType.log;
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM apitype WHERE name='"+name+"'";
			rs = stmt.executeQuery(sql);
			Object aux=null;
			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//id field mandatory
				Integer id = new Integer((list.get(0)).get("id").toString());

				//description field optional
				String description = null;
				aux = (list.get(0)).get("description");				
				if (aux!=null) description = aux.toString();
				
				//apirules field optional
				String apirules = null;
				aux = (list.get(0)).get("apirules");				
				if (aux!=null) apirules = aux.toString();
				
										 
				apitype = new APIType(id, name,description,apirules);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error APIType.getAPITypeClassByName() : "+e.getMessage());
			log.error("Error APIType.getAPITypeClassByName() : "+e.getMessage());
		}
		
		return apitype;
		
	}
	
	
	public static Integer saveAPIType(Connection con, APIType apitype, Logger log){
	
	
		Integer id=null;		
		String sql;		
		ResultSet rs;		
		PreparedStatement ps;		
		
			if (log == null) log = APIType.log;
			try{   	
				
				sql = "INSERT INTO apitype (name,description,apirules) VALUES (?,?,?)";	
	
				String generatedColumns[] = { "id" };
				ps = con.prepareStatement(sql,
						generatedColumns);
				
				ps.setString(1,apitype.getName());				
				if (apitype.getDescription() == null) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2,apitype.getDescription());
				if (apitype.getAPIRules() == null) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3,apitype.getAPIRules());						
				
				ps.executeUpdate();
				
				rs = ps.getGeneratedKeys();
				if (rs.next()) { id = rs.getInt(1); }	
								
				rs.close();				
		        ps.close();
				
			} catch ( SQLException e) {
				System.out.println("Error APIType.saveAPIType(): "+e.getMessage());
				log.error("Error APIType.saveAPIType(): "+e.getMessage());
			}	
	
		return id;
	}
	
	
	
	public static boolean deleteAPITypeById(Connection con, Integer id, String ref, Logger log){			
			
		String sql;	
		Statement stmt;
		
		if (log == null) log = APIType.log;
		
		try {
			
			//First we need to delete all Sources that relate to that APIType
			if (Source.deleteSourceByAPITypeId(con, id,ref,log)){ 
			
				stmt = con.createStatement();
								
				sql = "DELETE FROM apitype WHERE id="+id+";";
				stmt.executeUpdate(sql);
				
				stmt.close();
			}else return false;
				
		} catch ( SQLException e) {
			System.out.println("Error APIType.deleteAPITypeById(): "+e.getMessage());
			log.error("Error APIType.deleteAPITypeById(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	
	
	public static boolean deleteAPITypeByName(Connection con, String name,String ref, Logger log){			
		
		if (log == null) log = APIType.log;
		APIType apitype = APIType.getAPITypeClassByName(con, name,log);
		if (apitype !=null)
			return APIType.deleteAPITypeById(con, apitype.getId(),ref,log);
		else
		return false;		
		
	}
	
	
	
	
	
	public static boolean updateAPIType(Connection con, APIType apitype, Logger log){
				
		String sql;		
		PreparedStatement ps;		
		
		if (log == null) log = APIType.log;
			try{   	
				
				sql = "UPDATE apitype SET name=?, description=?, apirules=? WHERE id="+apitype.getId();
	
				ps = con.prepareStatement(sql);	
				ps.setString(1,apitype.getName());	
				if (apitype.getDescription() == null) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2,apitype.getDescription());
				if (apitype.getAPIRules() == null) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3,apitype.getAPIRules());												
				
				ps.executeUpdate();
				
				ps.close();								
				
				
			} catch ( SQLException e) {
				System.out.println("Error APIType.updateAPIType(): "+e.getMessage());
				log.error("Error APIType.updateAPIType(): "+e.getMessage());
				return false;
			}	
	
		return true;
	}
	
	
	
	
	public static ArrayList<APIType> getAPITypeList (Connection con, Logger log){
		
		ArrayList<APIType> apitype_array = new ArrayList<APIType>();
		String sql;
		Statement stmt;
		ResultSet rs;
				
		ArrayList<HashMap<String, Object>> list;
		
		if (log == null) log = APIType.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM apitype ORDER BY name";
			rs = stmt.executeQuery(sql);
			Object aux=null;
			
			
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
					
					//apirules field optional
					String apirules = null;
					aux = (list.get(k)).get("apirules");				
					if (aux!=null) apirules = aux.toString();
					
					
											 
					APIType apitype = new APIType(id, name, description,apirules);
					apitype_array.add(apitype);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error APIType.getAPITypeList(): "+e.getMessage());
			log.error("Error APIType.getAPITypeList(): "+e.getMessage());
		}
		
		return apitype_array;
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
	
	public static void testAPIType(Connection con) {
		System.out.println("****************************************************************************************");
		System.out.println("APIType test. Creating, inserting,selecting and deleting an APIType object from DB");	
		log.info("****************************************************************************************");
		log.info("APIType test. Creating, inserting,selecting and deleting an APIType object from DB");	
		
		String name = "some_name";
		String description = "some_description";
		String apirules = "some_apirules";
		APIType apitype = new APIType(name,description,apirules);
		System.out.println("Creating APIType class...... OK");
		log.info("Creating APIType class......OK");
		
		
		Integer id = APIType.saveAPIType(con, apitype,null);
		if (id==null){
			System.out.println("Saving APIType class...... Error ");
			log.info("Saving APIType class...... Error ");
		}else{
			System.out.println("Saving APIType class...... OK ");
			log.info("Saving APIType class...... OK ");
			
			APIType apitype2 = APIType.getAPITypeClassById(con, id, null);
			if (apitype2==null){
				System.out.println("Getting APIType class by ID...... Error ");
				log.info("Getting APIType class by ID...... Error ");
			}else{
				System.out.println("Getting APIType class by ID...... OK ");
				log.info("Getting APIType class by ID...... OK ");
				
				apitype2 = APIType.getAPITypeClassByName(con, name, null);
				if (apitype2 == null){
					System.out.println("Getting APIType class by Name...... Error ");
					log.info("Getting APIType class by Name...... Error ");
				}else{
					System.out.println("Getting APIType class by Name...... OK ");
					log.info("Getting APIType class by Name...... OK ");
					
					if (!APIType.deleteAPITypeById(con, id,"base",null)){
						System.out.println("Deleting APIType class by ID ...... Error ");
						log.info("Deleting APIType class by ID ...... Error  ");
					}else{
						System.out.println("Deleting APIType class by ID ...... OK ");
						log.info("Deleting APIType class by ID ...... OK  ");
						
						id = APIType.saveAPIType(con, apitype,null);
						if (id!=null){
							if (!APIType.deleteAPITypeByName(con, name,"base",null)){
								System.out.println("Deleting APIType class by Name ...... Error ");
								log.info("Deleting APIType class by Name ...... Error  ");
							}else{
								System.out.println("Deleting APIType class by Name ...... OK ");
								log.info("Deleting APIType class by Name ...... OK  ");
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
