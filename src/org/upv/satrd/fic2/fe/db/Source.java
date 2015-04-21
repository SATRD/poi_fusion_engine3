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



public class Source {
	
	private Integer id;
	private String name;	
	private String description;
	private String urlaccess;
	private String categorymapping;
	private Integer SourceId;	
	
	private static org.apache.log4j.Logger log =Logger.getLogger(org.upv.satrd.fic2.fe.db.Source.class);
	
	
	//The Source object has been inserted in the DDB, and the id is known
	public Source(Integer id, String name, String description, String urlaccess, String categorymapping, Integer SourceId ) {    
		this.id = id;
		this.name = name;
		this.description = description;
		this.urlaccess = urlaccess;
		this. categorymapping = categorymapping;
		this.SourceId = SourceId;			
		
	}
	
	
	//The Source object has not been inserted in the DDB, and the id is not known
	public Source(String name, String description, String urlaccess, String categorymapping, Integer SourceId ) {    
		this.id = null;
		this.name = name;
		this.description = description;
		this.urlaccess = urlaccess;
		this. categorymapping = categorymapping;
		this.SourceId = SourceId;			
		
	}
	
	
	
	//GET METHODS
	public Integer getId() {return  id;}		
	public String getName() {return name;}
	public String getDescription() {return description;}
	public String getUrlaccess() {return urlaccess;}
	public String getCategorymapping() {return categorymapping;}
	public Integer getAPITypeId() {return SourceId;}
	
	
	
	//SET METHODS
	public void setId(Integer id){this.id = id;}
	public void setName(String name){this.name =name;}
	public void setDescription(String description){this.description=description;}
	public void setUrlaccess(String urlaccess){this.urlaccess=urlaccess;}
	public void setCategorymapping(String categorymapping){this.categorymapping = categorymapping;}
	public void setAPITypeId(Integer SourceId){this.SourceId = SourceId;}
	
	
	
	

	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static Source getSourceClassById(Connection con, Integer id, Logger log){
		Source source = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = Source.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM source WHERE id="+id;
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
				
				//urlaccess field mandatory
				String urlaccess = (list.get(0)).get("urlaccess").toString();
				
				//categorymapping field mandatory
				String categorymapping = (list.get(0)).get("categorymapping").toString();
				
				//apitype field mandatory
				Integer apitypeId = new Integer((list.get(0)).get("apitypeid").toString());
				
				
				
										 
				source = new Source(id, name,description,urlaccess,categorymapping,apitypeId);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error Source.getSourceClassById(): "+e.getMessage());
			log.error("Error Source.getSourceClassById(): "+e.getMessage());
		}
		
		return source;
		
	}
	
	
	
	public static Source getSourceClassByName(Connection con, String name, Logger log){
		Source source = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = Source.log;
		
		//For poiproxy.local we search only 'poiproxy';			
		//if (name.toLowerCase().contains("poiproxy"))	name = "poiproxy";
		
		try {	
			
			stmt = con.createStatement();
			String sql = "SELECT * FROM source WHERE name='"+name+"'";
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
				
				//urlaccess field mandatory
				String urlaccess = (list.get(0)).get("urlaccess").toString();
				
				//categorymapping field mandatory
				String categorymapping = (list.get(0)).get("categorymapping").toString();
				
				//apitype field mandatory
				Integer apitypeId = new Integer((list.get(0)).get("apitypeid").toString());
				
				source = new Source(id, name,description,urlaccess,categorymapping,apitypeId);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error Source.getSourceClassByName() : "+e.getMessage());
			log.error("Error Source.getSourceClassByName() : "+e.getMessage());
		}
		
		return source;
		
	}
	
	
	public static Integer saveSource(Connection con, Source source, Logger log){
	
	
		Integer id=null;		
		String sql;
		PreparedStatement ps;		
		if (log == null) log = Source.log;
		
			try{   	
				
				sql = "INSERT INTO source (name,description,urlaccess,categorymapping,apitypeid) VALUES (?,?,?,?,?)";	
	
				String generatedColumns[] = { "id" };
				ps = con.prepareStatement(sql,
						generatedColumns);
				
				
				ps.setString(1,source.getName());				
				if (source.getDescription() == null) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2,source.getDescription());
				ps.setString(3,source.getUrlaccess());
				ps.setString(4,source.getCategorymapping());
				ps.setInt(5,source.getAPITypeId());	
				
				ps.executeUpdate();
				
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) { id = rs.getInt(1); }	
								
				rs.close();				
		        ps.close();
				
			} catch ( SQLException e) {
				System.out.println("Error Source.saveSource(): "+e.getMessage());
				log.error("Error Source.saveSource(): "+e.getMessage());
			}	
	
		return id;
	}
	
	
	
	//for ref='ocd' we refer to the OCD database. Otherwise for the base database
	public static boolean deleteSourceById(Connection con, Integer id, String ref, Logger log){			
			
		String sql;	
		Statement stmt;
		if (log == null) log = Source.log;
		
		try {
			
			//First we need to delete all POILabel that relate to that Source
			if (POILabel.deletePOILabelBySourceId(con, id,log)){ 
			
				stmt = con.createStatement();
				
				//delete from other tables (ocdsource,sourcecity
				sql = "DELETE FROM ocdsource WHERE sourceid="+id+";";
				stmt.executeUpdate(sql);
				
				sql = "DELETE FROM sourcecity WHERE sourceid="+id+";";
				stmt.executeUpdate(sql);
				
				sql = "DELETE FROM licensesource WHERE sourceid="+id+";";
				stmt.executeUpdate(sql);
				
				if (ref.equalsIgnoreCase("ocd")){
					sql = "DELETE FROM poisource WHERE sourceid="+id+";";
					stmt.executeUpdate(sql);
				}
				
				//Finally remove the table entry												
				sql = "DELETE FROM source WHERE id="+id+";";
				stmt.executeUpdate(sql);
				
				stmt.close();
			}else return false;
				
		} catch ( SQLException e) {
			System.out.println("Error Source.deleteSourceById(): "+e.getMessage());
			log.error("Error Source.deleteSourceById(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	
	
	public static boolean deleteSourceByName(Connection con, String name, String ref, Logger log){			
		
		if (log == null) log = Source.log;
		Source source = Source.getSourceClassByName(con, name,log);
		if (source !=null)
			return Source.deleteSourceById(con, source.getId(),ref,log);
		else
		return false;		
		
	}
	
	public static boolean deleteSourceByAPITypeId(Connection con, Integer id, String ref, Logger log){			
		
		if (log == null) log = Source.log;
		try {
			
			//delete dependencies with other tables
			//Get all Sources, retrieve their id and delete them 
			ArrayList<Source> source_array = getSourceListByAPITypeId(con,id,log);
			for (int k=0;k<source_array.size();k++){
				Source source = source_array.get(k);
				deleteSourceById(con,source.getId(),ref,log);				
			}					
				
		} catch ( Exception e) {
			System.out.println("Error Source.deleteSourceById(): "+e.getMessage());
			log.error("Error Source.deleteSourceById(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	
	
	public static boolean updateSource(Connection con, Source source, Logger log){
				
		String sql;
		PreparedStatement ps;	
		if (log == null) log = Source.log;
		
			try{   	
				
				sql = "UPDATE source SET name=?, description=?, urlaccess=?, categorymapping=?, apitypeid=? WHERE id="+source.getId();
	
				ps = con.prepareStatement(sql);	
				ps.setString(1,source.getName());	
				if (source.getDescription() == null) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2,source.getDescription());
				ps.setString(3,source.getUrlaccess());
				ps.setString(4,source.getCategorymapping());
				ps.setInt(5,source.getAPITypeId());
				
				ps.executeUpdate();
				
				ps.close();								
				
				
			} catch ( SQLException e) {
				System.out.println("Error Source.updateSource(): "+e.getMessage());
				log.error("Error Source.updateSource(): "+e.getMessage());
				return false;
			}	
	
		return true;
	}
	
	
	
	
	public static ArrayList<Source> getSourceList (Connection con, Logger log){
		
		ArrayList<Source> source_array = new ArrayList<Source>();
		String sql;
		Statement stmt;
		ResultSet rs;
				
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = Source.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM source ORDER BY name";
			rs = stmt.executeQuery(sql);
			Object aux=null;
			
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){	
				
				//log.debug("size:" +list.size());
				
				for (int k=0;k<list.size();k++){
				
					//id field mandatory
					Integer id = new Integer((list.get(k)).get("id").toString());
					
					//name field mandatory
					String name = (list.get(k)).get("name").toString();					
					
					//description field optional
					String description = null;
					aux = (list.get(k)).get("description");				
					if (aux!=null) description = aux.toString();
					
					//urlaccess field mandatory
					String urlaccess = (list.get(k)).get("urlaccess").toString();
					
					//categorymapping field mandatory
					String categorymapping = (list.get(k)).get("categorymapping").toString();
					
					//apitype field mandatory
					Integer apitypeId = new Integer((list.get(k)).get("apitypeid").toString());
					
					Source source = new Source(id, name,description,urlaccess,categorymapping,apitypeId);
					
					source_array.add(source);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error Source.getSourceList(): "+e.getMessage());
			log.error("Error Source.getSourceList(): "+e.getMessage());
		}
		
		return source_array;
	}
	
	
	public static ArrayList<Source> getSourceListByAPITypeId (Connection con, Integer apitypeId, Logger log){
		
		ArrayList<Source> source_array = new ArrayList<Source>();
		String sql;
		
		Statement stmt;
		ResultSet rs;
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = Source.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM source WHERE apitypeid="+apitypeId;
			rs = stmt.executeQuery(sql);
			Object aux=null;
			
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){	
				
				for (int k=0;k<list.size();k++){
				
					//id field mandatory
					Integer id = new Integer((list.get(k)).get("id").toString());
					
					//name field mandatory
					String name = (list.get(k)).get("name").toString();					
					
					//description field optional
					String description = null;
					aux = (list.get(k)).get("description");				
					if (aux!=null) description = aux.toString();
					
					//urlaccess field mandatory
					String urlaccess = (list.get(k)).get("urlaccess").toString();
					
					//categorymapping field mandatory
					String categorymapping = (list.get(k)).get("categorymapping").toString();					
					
					Source source = new Source(id, name,description,urlaccess,categorymapping,apitypeId);
					
					source_array.add(source);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error Source.getSourceList(): "+e.getMessage());
			log.error("Error Source.getSourceList(): "+e.getMessage());
		}
		
		return source_array;
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
	          row.put(md.getColumnName(i).toLowerCase().toLowerCase(),rs.getObject(i));
	        }
	        
	        results.add(row);
	    }
	    
	    return results;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////testing method///////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	
	public static void testSource(Connection con) {
		System.out.println("****************************************************************************************");
		System.out.println("Source test. Creating, inserting,selecting and deleting an Source object from DB");	
		log.info("****************************************************************************************");
		log.info("Source test. Creating, inserting,selecting and deleting an Source object from DB");	
		
		String name = "some_name";
		String description = "some_description";
		String urlaccess = "some_urlaccess";
		String categorymapping = "some_categorymapping";
		
		//We need to create first an APIType
		String api_name = "some_apiname";
		String api_description = "some_apidescription";
		String apirules = "some_apirules";
		APIType apitype  = new APIType(api_name,api_description,apirules);
		Integer apitypeId = APIType.saveAPIType(con, apitype,null);
		
		Source source = new Source(name,description,urlaccess,categorymapping,apitypeId);
		System.out.println("Creating Source class...... OK");
		log.info("Creating Source class......OK");
		
		
		Integer id = Source.saveSource(con, source,null);
		if (id==null){
			System.out.println("Saving Source class...... Error ");
			log.info("Saving Source class...... Error ");
		}else{
			System.out.println("Saving Source class...... OK ");
			log.info("Saving Source class...... OK ");
			
			Source Source2 = Source.getSourceClassById(con, id,null);
			if (Source2==null){
				System.out.println("Getting Source class by ID...... Error ");
				log.info("Getting Source class by ID...... Error ");
			}else{
				System.out.println("Getting Source class by ID...... OK ");
				log.info("Getting Source class by ID...... OK ");
				
				Source2 = Source.getSourceClassByName(con, name,null);
				if (Source2 == null){
					System.out.println("Getting Source class by Name...... Error ");
					log.info("Getting Source class by Name...... Error ");
				}else{
					System.out.println("Getting Source class by Name...... OK ");
					log.info("Getting Source class by Name...... OK ");
					
					if (!Source.deleteSourceById(con, id,"base",null)){
						System.out.println("Deleting Source class by ID ...... Error ");
						log.info("Deleting Source class by ID ...... Error  ");
					}else{
						System.out.println("Deleting Source class by ID ...... OK ");
						log.info("Deleting Source class by ID ...... OK  ");
						
						id = Source.saveSource(con, source,null);
						if (id!=null){
							if (!Source.deleteSourceByName(con, name,"base",null)){
								System.out.println("Deleting Source class by Name ...... Error ");
								log.info("Deleting Source class by Name ...... Error  ");
							}else{
								System.out.println("Deleting Source class by Name ...... OK ");
								log.info("Deleting Source class by Name ...... OK  ");
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
