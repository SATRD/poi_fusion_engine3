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

public class Component {

	private Integer id;
	private String name;
	
	private static org.apache.log4j.Logger log =Logger.getLogger(org.upv.satrd.fic2.fe.db.Component.class);
	
	
	//The Component object has been inserted in the DDB, and the id is known
	public Component(Integer id, String name){
		this.id = id;
		this.name = name;
	}
	
	//The Component object has not been inserted in the DDB, and the id is not known
	public Component(String name){
		this.id = null;
		this.name = name;
	}
	
	//GET methods
	public Integer getId(){ return this.id;}
	public String getName(){ return this.name;}
	
	
	//SET methods
	public void setID(Integer id){ this.id = id;}
	public void setName(String name) {this.name = name;}
	
	

	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static Component getComponentClassById(Connection con, Integer id, Logger log){
		Component component = null;
		Statement stmt ;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = Component.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM component WHERE id="+id;
			rs = stmt.executeQuery(sql);			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//name field mandatory
				String name = (list.get(0)).get("name").toString();				
										 
				component = new Component(id, name);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error Component.getComponentClassById(): "+e.getMessage());
			log.error("Error Component.getComponentClassById(): "+e.getMessage());
		}
		
		return component;
		
	}
	
	
	
	public static Component getComponentClassByName(Connection con, String name, Logger log){
		Component component = null;
		Statement stmt ;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = Component.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM component WHERE name='"+name+"'";
			rs = stmt.executeQuery(sql);
			
			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//id field mandatory
				Integer id = new Integer((list.get(0)).get("id").toString());				
										 
				component = new Component(id, name);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error Component.getComponentClassByName() : "+e.getMessage());
			log.error("Error Component.getComponentClassByName() : "+e.getMessage());
		}
		
		return component;
		
	}
	
	
	public static Integer saveComponent(Connection con, Component component, Logger log){
	
	
		Integer id=null;		
		String sql;
		PreparedStatement ps;		
			if (log == null) log = Component.log;
			try{   	
				
				sql = "INSERT INTO component (name) VALUES (?)";	
	
				String generatedColumns[] = { "id" };
				ps = con.prepareStatement(sql,
						generatedColumns);	
				ps.setString(1,component.getName());							
				
				ps.executeUpdate();
				
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) { id = rs.getInt(1); }	
								
				rs.close();				
		        ps.close();
				
			} catch ( SQLException e) {
				System.out.println("Error Component.saveComponent(): "+e.getMessage());
				log.error("Error Component.saveComponent(): "+e.getMessage());
			}	
	
		return id;
	}
	
	
	
	public static boolean deleteComponentById(Connection con, Integer id, Logger log){			
			
		String sql;	
		Statement stmt ;
		
		if (log == null) log = Component.log;
		try {
						
			stmt = con.createStatement();
							
			sql = "DELETE FROM component WHERE id="+id+";";
			stmt.executeUpdate(sql);
			
			stmt.close();
			
				
		} catch ( SQLException e) {
			System.out.println("Error Component.deleteComponentById(): "+e.getMessage());
			log.error("Error Component.deleteComponentById(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	
	
	public static boolean deleteComponentByName(Connection con, String name, Logger log){			
		
		if (log == null) log = Component.log;
		Component component = Component.getComponentClassByName(con, name,log);
		if (component !=null)
			return Component.deleteComponentById(con, component.getId(),log);
		else
		return false;		
		
	}
	
	
	
	
	
	public static boolean updateComponent(Connection con, Component component, Logger log){
				
		String sql;
		PreparedStatement ps;		
		
		if (log == null) log = Component.log;
			try{   	
				
				sql = "UPDATE component SET name=? WHERE id="+component.getId();
	
				ps = con.prepareStatement(sql);	
				ps.setString(1,component.getName());	
							
				ps.executeUpdate();
				
				ps.close();								
				
				
			} catch ( SQLException e) {
				System.out.println("Error Component.updateComponent(): "+e.getMessage());
				log.error("Error Component.updateComponent(): "+e.getMessage());
				return false;
			}	
	
		return true;
	}
	
	
	
	
	public static ArrayList<Component> getComponentList (Connection con, Logger log){
		
		ArrayList<Component> Component_array = new ArrayList<Component>();
		String sql;
		Statement stmt ;
		ResultSet rs;
		
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = Component.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM component ORDER BY name";
			rs = stmt.executeQuery(sql);
			
			
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){	
				
				for (int k=0;k<list.size();k++){
				
					//id field mandatory
					Integer id = new Integer((list.get(k)).get("id").toString());
					
					//name field mandatory
					String name = list.get(k).get("name").toString();
											 
					Component Component = new Component(id, name);
					Component_array.add(Component);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error Component.getComponentList(): "+e.getMessage());
			log.error("Error Component.getComponentList(): "+e.getMessage());
		}
		
		return Component_array;
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
	
	public static void testComponent(Connection con) {
		System.out.println("****************************************************************************************");
		System.out.println("Component test. Creating, inserting,selecting and deleting a Component object from DB");	
		log.info("****************************************************************************************");
		log.info("Component test. Creating, inserting,selecting and deleting a Component object from DB");	
		
		String name = "some_name";		
		Component component = new Component(name);
		System.out.println("Creating Component class...... OK");
		log.info("Creating Component class......OK");
		
		
		Integer id = Component.saveComponent(con, component,null);
		if (id==null){
			System.out.println("Saving Component class...... Error ");
			log.info("Saving Component class...... Error ");
		}else{
			System.out.println("Saving Component class...... OK ");
			log.info("Saving Component class...... OK ");
			
			Component component2 = Component.getComponentClassById(con, id,null);
			if (component2==null){
				System.out.println("Getting Component class by ID...... Error ");
				log.info("Getting Component class by ID...... Error ");
			}else{
				System.out.println("Getting Component class by ID...... OK ");
				log.info("Getting Component class by ID...... OK ");
				
				component2 = Component.getComponentClassByName(con, name,null);
				if (component2 == null){
					System.out.println("Getting Component class by Name...... Error ");
					log.info("Getting Component class by Name...... Error ");
				}else{
					System.out.println("Getting Component class by Name...... OK ");
					log.info("Getting Component class by Name...... OK ");
					
					if (!Component.deleteComponentById(con, id,null)){
						System.out.println("Deleting Component class by ID ...... Error ");
						log.info("Deleting Component class by ID ...... Error  ");
					}else{
						System.out.println("Deleting Component class by ID ...... OK ");
						log.info("Deleting Component class by ID ...... OK  ");
						
						id = Component.saveComponent(con, component,null);
						if (id!=null){
							if (!Component.deleteComponentByName(con, name,null)){
								System.out.println("Deleting Component class by Name ...... Error ");
								log.info("Deleting Component class by Name ...... Error  ");
							}else{
								System.out.println("Deleting Component class by Name ...... OK ");
								log.info("Deleting Component class by Name ...... OK  ");
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
