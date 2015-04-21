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

public class City {

	private Integer id;
	private String name;
	private String bbox; //bbox is given in  this format [Ymin,Xmin,Ymax,Xmax]
	
	
	private static org.apache.log4j.Logger log = Logger.getLogger(org.upv.satrd.fic2.fe.db.City.class);;
	
	
	//The City object has been inserted in the DDB, and the id is known
	//bbox is supposed to be [Ymin,Xmin,Ymax,Xmax]
	public City (Integer id, String name, String bbox){
		this.id = id;
		this.name = name;
		this.bbox = bbox;					
	}
	
	//The City object has not been inserted in the DDB, and the id is not known
	//bbox is supposed to be [Ymin,Xmin,Ymax,Xmax]
		public City (String name, String bbox){
			this.id = null;
			this.name = name;
			this.bbox = bbox;					
		}
	
	
	
	//GET METHODS
	public Integer getId(){return this.id;}
	public String getName(){return this.name;}
	public String getBbox(){return this.bbox;}
	
	
	
	
	//SET METHODS
	public void setId(Integer id){this.id = id;}
	public void setName(String name){this.name = name;}
	public void setBbox(String bbox){this.bbox= bbox;}
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static City getCityClassById(Connection con, Integer id, Logger log){
		
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		
		City city = null;
		if (log == null) log = City.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM city WHERE id="+id;
			rs = stmt.executeQuery(sql);
			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//name field mandatory
				String name = (list.get(0)).get("name").toString();
				
				//bbox field mandatory
				String bbox = (list.get(0)).get("bbox").toString();
				
										 
				city = new City(id, name,bbox);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error City.getCityClassById(): "+e.getMessage());
			log.error("Error City.getCityClassById(): "+e.getMessage());
		}
		
		return city;
		
	}
	
	
	
	public static City getCityClassByName(Connection con, String name, Logger log){
		City city = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = City.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM city WHERE name='"+name+"'";
			rs = stmt.executeQuery(sql);
			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//id field mandatory
				Integer id = new Integer((list.get(0)).get("id").toString());
				
				//bbox field mandatory
				String bbox = (list.get(0)).get("bbox").toString();
				
				
										 
				city = new City(id, name, bbox);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error City.getCityClassByName() : "+e.getMessage());
			log.error("Error City.getcityClassByName() : "+e.getMessage());
		}
		
		return city;
		
	}
	
	
	public static Integer saveCity(Connection con, City city, Logger log){
	
	
		Integer id=null;		
		String sql;
		PreparedStatement ps;		
		ResultSet rs;
		if (log == null) log = City.log;
		
		
			try{   	
				
				sql = "INSERT INTO city (name,bbox) VALUES (?,?)";	
	
				String generatedColumns[] = { "id" };
				
				ps = con.prepareStatement(sql, generatedColumns);
						// Statement.RETURN_GENERATED_KEYS);	
				ps.setString(1,city.getName());				
				ps.setString(2,city.getBbox());						
				
				ps.executeUpdate();
				
				rs = ps.getGeneratedKeys();
				if (rs.next()) { 
					id = rs.getInt(1);
					
				}	
								
				rs.close();				
		        ps.close();
				
			} catch ( SQLException e) {
				System.out.println("Error City.saveCity(): "+e.getMessage());
				log.error("Error City.saveCity(): "+e.getMessage());
			}	
	
		return id;
	}
	
	
	
	public static boolean deleteCityById(Connection con, Integer id, Logger log){			
			
		String sql;	
		Statement stmt;
		if (log == null) log = City.log;
		
		try {
			
			//First we need to delete all OCDs that relate to that city
			if (OCD.deleteOCDById(con, id,null)){
			
				stmt = con.createStatement();
				
				//Delete all dependencies with other tables (sourcecity )
				sql = "DELETE FROM sourcecity WHERE cityid="+id+";";
				stmt.executeUpdate(sql);
				
								
				//Finally delete the city
				sql = "DELETE FROM city WHERE id="+id+";";
				stmt.executeUpdate(sql);
				
				stmt.close();
			}else return false;
				
		} catch ( SQLException e) {
			System.out.println("Error City.deleteCityById(): "+e.getMessage());
			log.error("Error City.deleteCityById(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	
	
	public static boolean deleteCityByName(Connection con, String name, Logger log){			
		
		if (log == null) log = City.log;
		City city = City.getCityClassByName(con, name, log);
		if (city !=null)
			return City.deleteCityById(con, city.getId(),log);
		else
		return false;		
		
	}
	
	
	
	
	
	public static boolean updateCity(Connection con, City city, Logger log){
				
		String sql;
		PreparedStatement ps;		
		if (log == null) log = City.log;
		
			try{   	
				
				sql = "UPDATE city SET name=?, bbox=? WHERE id="+city.getId();
	
				ps = con.prepareStatement(sql);	
				ps.setString(1,city.getName());	
				ps.setString(2,city.getBbox());											
				
				ps.executeUpdate();
				
				ps.close();								
				
				
			} catch ( SQLException e) {
				System.out.println("Error City.updateCity(): "+e.getMessage());
				log.error("Error City.updateCity(): "+e.getMessage());
				return false;
			}	
	
		return true;
	}
	
	
	
	
	public static ArrayList<City> getCityList (Connection con, Logger log){
		
		ArrayList<City> city_array = new ArrayList<City>();
		String sql;
		Statement stmt;
		ResultSet rs;
		
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = City.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM city ORDER BY name";
			rs = stmt.executeQuery(sql);
			
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){	
				
				for (int k=0;k<list.size();k++){
				
					//id field mandatory
					Integer id = new Integer((list.get(k)).get("id").toString());
					
					//name field mandatory
					String name = list.get(k).get("name").toString();
					
					//bbox field mandatory
					String bbox = list.get(k).get("bbox").toString();
					
					
											 
					City city = new City(id, name, bbox);
					city_array.add(city);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error City.getCityList(): "+e.getMessage());
			log.error("Error City.getCityList(): "+e.getMessage());
		}
		
		return city_array;
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
	
	public static void testCity(Connection con) {
		System.out.println("****************************************************************************************");
		System.out.println("City test. Creating, inserting,selecting and deleting a City object from DB");	
		log.info("****************************************************************************************");
		log.info("City test. Creating, inserting,selecting and deleting a City object from DB");	
		
		String name = "some_name";
		String bbox = "some_bbox";
		City city = new City(name,bbox);
		System.out.println("Creating City class...... OK");
		log.info("Creating City class......OK");
		
		
		Integer id = City.saveCity(con, city,null);
		if (id==null){
			System.out.println("Saving City class...... Error ");
			log.info("Saving City class...... Error ");
		}else{
			System.out.println("Saving City class...... OK ");
			log.info("Saving City class...... OK ");
			
			City city2 = City.getCityClassById(con, id,null);
			if (city2==null){
				System.out.println("Getting City class by ID...... Error ");
				log.info("Getting City class by ID...... Error ");
			}else{
				System.out.println("Getting City class by ID...... OK ");
				log.info("Getting City class by ID...... OK ");
				
				city2 = City.getCityClassByName(con, name,null);
				if (city2 == null){
					System.out.println("Getting City class by Name...... Error ");
					log.info("Getting City class by Name...... Error ");
				}else{
					System.out.println("Getting City class by Name...... OK ");
					log.info("Getting City class by Name...... OK ");
					
					if (!City.deleteCityById(con, id,null)){
						System.out.println("Deleting City class by ID ...... Error ");
						log.info("Deleting City class by ID ...... Error  ");
					}else{
						System.out.println("Deleting City class by ID ...... OK ");
						log.info("Deleting City class by ID ...... OK  ");
						
						id = City.saveCity(con, city,null);
						if (id!=null){
							if (!City.deleteCityByName(con, name,null)){
								System.out.println("Deleting City class by Name ...... Error ");
								log.info("Deleting City class by Name ...... Error  ");
							}else{
								System.out.println("Deleting City class by Name ...... OK ");
								log.info("Deleting license City by Name ...... OK  ");
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
