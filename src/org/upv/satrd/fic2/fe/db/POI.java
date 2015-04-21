package org.upv.satrd.fic2.fe.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.upv.satrd.fic2.fe.main.OutputDB;


public class POI {
	
	private Integer id;
	private String name;
	private Double latitude;
	private Double longitude;
	private Date updated;
	
	
	
	private static org.apache.log4j.Logger log =Logger.getLogger(org.upv.satrd.fic2.fe.db.POI.class);
	
	
	//The POI object has been inserted in the DDB, and the id is known
	public POI (Integer id, String name, Double latitude, Double longitude, Date updated){
		this.id = id;
		this.name=name;
		this.latitude = latitude;
		this.longitude =longitude;
		this.updated = updated;	
	}
	
	//The POI object has not been inserted in the DDB, and the id is not known
	public POI (String name, Double latitude, Double longitude, Date updated){
		this.id = null;
		this.name=name;
		this.latitude = latitude;
		this.longitude =longitude;
		this.updated = updated;	
	}	
	
	
	
	//GET METHODS
	public Integer getId(){return this.id;}
	public String getName(){return this.name;}
	public Double getLatitude(){return this.latitude;}
	public Double getLongitude(){return this.longitude;}
	public Date getUpdated(){return this.updated;}
	
		
	
	
	//SET METHODS
	public void setId(Integer id){this.id = id;}
	public void setName(String name){this.name = name;}
	public void setLatitude (Double latitude){this.latitude = latitude;}
	public void setLongitude(Double longitude){this.longitude = longitude;}
	public void setUpdated(Date updated){this.updated=updated;}

	
	

	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static POI getPOIClassById(Connection con, Integer id, Logger log){
		POI poi = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POI.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM poi WHERE id="+id;
			rs = stmt.executeQuery(sql);
			Object aux= null;
			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//name field mandatory
				String name = (list.get(0)).get("name").toString();
				
				//date field mandatory. It shouldn't be null, but check for it
				Date updated = null;
				aux = (list.get(0)).get("updated");
				if (aux!=null){
					if (aux instanceof Date) {
						updated = (Date) aux;
					} else {
						if (aux instanceof Timestamp) {
							updated = new Date(((Timestamp) aux).getTime());
						} else {
							String date = aux.toString(); 					
							//convert to Date
							updated = Date.valueOf(date);
						}
					}
				}	
				
				//Get latitude and longitude
				String lon_from_geom;
				if (OutputDB.configFEService ==null){
					lon_from_geom = OutputDB.config.getLonFromPoint();
				}else{
					lon_from_geom = OutputDB.configFEService.getLonFromPoint();
				}
				
				//String lon_from_geom = OutputDB.configFEService.getLonFromPoint();
				
				lon_from_geom = lon_from_geom.replace("{0}", "p.position");
				
				sql = "SELECT " + lon_from_geom + " st_x from poi p WHERE p.id="+id;
				
				rs = stmt.executeQuery(sql);
				
				list = resultSetToArrayList(rs);
				
				
				
				if (!list.isEmpty()){					
					Double longitude = new Double((list.get(0)).get("st_x").toString());
					
					String lat_from_geom;					
					if (OutputDB.configFEService ==null){
						lat_from_geom = OutputDB.config.getLatFromPoint();
					}else{
						lat_from_geom = OutputDB.configFEService.getLatFromPoint();
					}
					
					
					lat_from_geom = lat_from_geom.replace("{0}", "p.position");

					sql = "SELECT " + lat_from_geom + " st_y from poi p WHERE p.id="+id;
					rs = stmt.executeQuery(sql);
					
					list = resultSetToArrayList(rs);
					
					if (!list.isEmpty()){					
						Double latitude = new Double((list.get(0)).get("st_y").toString());						
					
					
						poi = new POI(id, name,latitude,longitude,updated);
					}
				}
				
				
			}	
			
			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error POI.getPOIClassById(): "+e.getMessage());
			log.error("Error POI.getPOIClassById(): "+e.getMessage());
		}
		
		return poi;
		
	}
	
	
	
	public static POI getPOIClassByName(Connection con, String name, Logger log){
		POI poi = null;
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POI.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM poi WHERE name='"+name+"'";
			rs = stmt.executeQuery(sql);
			Object aux=null;
			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//id field mandatory
				Integer id = new Integer((list.get(0)).get("id").toString());
				
				//date field mandatory. It shouldn't be null, but check for it
				Date updated = null;
				aux = (list.get(0)).get("updated");
				if (aux!=null){
					String date = aux.toString(); 					
					//convert to Date
					updated = Date.valueOf(date);
				}	
				
				//Get latitude and longitude
				
				String lon_from_geom;
				if (OutputDB.configFEService ==null){
					lon_from_geom = OutputDB.config.getLonFromPoint();
				}else{
					lon_from_geom = OutputDB.configFEService.getLonFromPoint();
				}
				
				lon_from_geom = lon_from_geom.replace("{0}", "p.position");
				
				sql = "SELECT " + lon_from_geom + " st_x from p.poi WHERE p.id="+id;
				rs = stmt.executeQuery(sql);
				
				list = resultSetToArrayList(rs);
				
				if (!list.isEmpty()){					
					Double longitude = new Double((list.get(0)).get("st_x").toString());
					
					String lat_from_geom;					
					if (OutputDB.configFEService ==null){
						lat_from_geom = OutputDB.config.getLatFromPoint();
					}else{
						lat_from_geom = OutputDB.configFEService.getLatFromPoint();
					}
					
					lat_from_geom = lat_from_geom.replace("{0}", "p.position");
					
					sql = "SELECT " + lat_from_geom + " st_y from poi p WHERE p.id="+id;
					rs = stmt.executeQuery(sql);
					
					list = resultSetToArrayList(rs);
					
					if (!list.isEmpty()){					
						Double latitude = new Double((list.get(0)).get("st_y").toString());						
					
					
						poi = new POI(id, name,latitude,longitude,updated);
					}
				}
				
			}	
			
			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error POI.getPOIClassByName() : "+e.getMessage());
			log.error("Error POI.getPOIClassByName() : "+e.getMessage());
		}
		
		return poi;
		
	}
	
	
	public static Integer savePOI(Connection con, POI poi, Logger log){	
	
		Integer id=null;		
		String sql;
		PreparedStatement ps;	
		if (log == null) log = POI.log;
		
			try{   					
				
				
				String geom_xysrid =
						OutputDB.configFEService.getGeometryFromLonLatSrid();
				geom_xysrid = geom_xysrid.replace("{0}",
						formatter().format(poi.getLongitude()));
				geom_xysrid = geom_xysrid.replace("{1}",
						formatter().format(poi.getLatitude()));
				geom_xysrid = geom_xysrid.replace("{2}", "4326");
			
				
				
				/*
				sql = "INSERT INTO poi (name,position,updated) VALUES "
						+ "(?,ST_SetSRID(ST_MakePoint(?,?), 4326),?)";
				
				*/
							
				
				sql = "INSERT INTO poi (name,position,updated) VALUES "
						+ "(?," + geom_xysrid + ",?)";	
					
	
				String generatedColumns[] = { "id" };
				ps = con.prepareStatement(sql,
						generatedColumns);
				
				ps.setString(1,poi.getName());	
				//ps.setDouble(2,poi.getLongitude());
				//ps.setDouble(3,poi.getLatitude());
				ps.setDate(2,poi.getUpdated());	
				
								
				ps.executeUpdate();
				
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) { id = rs.getInt(1); }	
								
				rs.close();				
		        ps.close();
				
			} catch ( SQLException e) {
				System.out.println("Error POI.savePOI(): "+e.getMessage());
				log.error("Error POI.savePOI(): "+e.getMessage());
			}	
	
		return id;
	}
	
	
	
	public static boolean deletePOIById(Connection con, Integer id, Logger log){			
			
		String sql;	
		Statement stmt;
		if (log == null) log = POI.log;
		
		try {
			
			//First we need to delete all OCDs that relate to that POI
			if (POILabel.deletePOILabelByPOIId(con, id,log)){  
			
				stmt = con.createStatement();
				
				//First delete other dependencies with other tables
				sql = "DELETE FROM poicategory WHERE poiid="+id+";";
				stmt.executeUpdate(sql);
				
				sql = "DELETE FROM poisource WHERE poiid="+id+";";
				stmt.executeUpdate(sql);
								
				sql = "DELETE FROM poificontent WHERE poiid="+id+";";
				stmt.executeUpdate(sql);
				
								
				sql = "DELETE FROM poi WHERE id="+id+";";
				stmt.executeUpdate(sql);
				
				stmt.close();
			}else return false;
				
		} catch ( SQLException e) {
			System.out.println("Error POI.deletePOIById(): "+e.getMessage());
			log.error("Error POI.deletePOIById(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	
		
	public static boolean updatePOI(Connection con, POI poi, Logger log){
				
		String sql;
		PreparedStatement ps;	
		if (log == null) log = POI.log;
		
			try{   	
				String geom_xysrid;					
				if (OutputDB.configFEService ==null){
					geom_xysrid = OutputDB.config.getGeometryFromLonLatSrid();
				}else{
					geom_xysrid = OutputDB.configFEService.getGeometryFromLonLatSrid();
				}
				
				geom_xysrid = OutputDB.configFEService.getGeometryFromLonLatSrid();
				
				geom_xysrid = geom_xysrid.replace("{0}",
						formatter().format(poi.getLongitude()));
				geom_xysrid = geom_xysrid.replace("{1}",
						formatter().format(poi.getLatitude()));
				geom_xysrid = geom_xysrid.replace("{2}", "4326");

				sql = "UPDATE POI SET name=?, position="
						+ geom_xysrid
						+ ", updated=? WHERE id="+poi.getId();
	
				ps = con.prepareStatement(sql);	
				ps.setString(1,poi.getName());
				ps.setDate(2,poi.getUpdated());
				
				ps.executeUpdate();
				
				ps.close();								
				
				
			} catch ( SQLException e) {
				System.out.println("Error POI.updatePOI(): "+e.getMessage());
				log.error("Error POI.updatePOI(): "+e.getMessage());
				return false;
			}	
	
		return true;
	}
	
	
	
	//This method should be used just for testing purposes
			public static ArrayList<POI> getPOIList(Connection con, Integer limit, Logger log){
				
				ArrayList<POI> poi_array = new ArrayList<POI>();
				String sql;
				Statement stmt;
				ResultSet rs;
				if (log == null) log = POI.log;
				
				ArrayList<HashMap<String, Object>> list;
				ArrayList<HashMap<String, Object>> list2;
				Double latitude=0.0;
				Double longitude=0.0;
				
				try {				
						
					stmt = con.createStatement();
					sql = "select * from poi"; 
					if (limit!=null) sql = sql+" limit "+limit;
					rs = stmt.executeQuery(sql);
					Object aux = null;
					
					
					list = resultSetToArrayList(rs);
					
					if (!list.isEmpty()){	
						
						for (int k=0;k<list.size();k++){
						
							//id field mandatory
							Integer id = new Integer((list.get(k)).get("id").toString());
							
							//name field mandatory
							String name = list.get(k).get("name").toString();
							
							
										
							
							//Get longitude and latitude
							String lon_from_geom;					
							if (OutputDB.config ==null){
								lon_from_geom = OutputDB.configFEService.getLonFromPoint();
							}else{
								lon_from_geom = OutputDB.config.getLonFromPoint();
							}
							
							
							
							lon_from_geom = lon_from_geom.replace("{0}", "p.position");
							
							sql = "SELECT " + lon_from_geom + " st_x from poi p WHERE p.id="+id;
							
							rs = stmt.executeQuery(sql);
							
							list2 = resultSetToArrayList(rs);
							
							
							
							if (!list2.isEmpty()){					
								longitude = new Double((list2.get(0)).get("st_x").toString());
								
								
								String lat_from_geom;					
								if (OutputDB.config ==null){
									lat_from_geom = OutputDB.configFEService.getLatFromPoint();
								}else{
									lat_from_geom = OutputDB.config.getLatFromPoint();
								}
								
								
								
								lat_from_geom = lat_from_geom.replace("{0}", "p.position");

								sql = "SELECT " + lat_from_geom + " st_y from poi p WHERE p.id="+id;
								rs = stmt.executeQuery(sql);
								
								list2 = resultSetToArrayList(rs);
								
								if (!list2.isEmpty()){					
									latitude = new Double((list2.get(0)).get("st_y").toString());											
									
								}
							}
										
							
							//updated field might null
							Date updated = null;
							aux = (list.get(k)).get("updated");
							if (aux!=null){
								String date = aux.toString(); 					
								//convert to Date
								updated = Date.valueOf(date);
							}				
													 
							POI poi = new POI(id,name,latitude,longitude,updated);
							poi_array.add(poi);
						}
					}
					rs.close();
			        stmt.close();					
					
				
					
					
				} catch ( Exception e ) {
					System.out.println("Error POI.getPOIList(): "+e.getMessage());
					log.error("Error POI.getPOIList(): "+e.getMessage());
				}
				
				return poi_array;
			}
			
			public static ArrayList<POI> getPOIListByCategoryName(Connection con, String categoryName, Integer limit, Logger log){
				
				
				ArrayList<POI> poi_array = new ArrayList<POI>();
				String sql;
				Statement stmt;
				ResultSet rs;
				
				
				ArrayList<HashMap<String, Object>> list;
				ArrayList<HashMap<String, Object>> list2;
				if (log == null) log = POI.log;
				
				try {
					
					Category category = Category.getCategoryClassByName(con, categoryName,log);
					
					if (category!=null){
						
						Integer categoryId = category.getId();
						
						if (categoryId !=null){
						
							stmt = con.createStatement();
							sql = "select * from poi inner join poicategory on poi.id = poicategory.poiid where poicategory.categoryid="+categoryId; 
							
							if (limit!=null) sql = sql+" limit "+limit;
							
							//log.info(sql);
							rs = stmt.executeQuery(sql);
							Object aux = null;
							
							Double latitude = 0.0;
							Double longitude = 0.0;
							
							
							list = resultSetToArrayList(rs);
							
							if (!list.isEmpty()){	
								
								for (int k=0;k<list.size();k++){
									
									
									//id field mandatory
									Integer id = new Integer((list.get(k)).get("id").toString());
									
									
									//name field mandatory
									String name = list.get(k).get("name").toString();
									
									
									
									
									
									//Get longitude and latitude
									String lon_from_geom;					
									if (OutputDB.config ==null){
										lon_from_geom = OutputDB.configFEService.getLonFromPoint();
									}else{
										lon_from_geom = OutputDB.config.getLonFromPoint();
									}
									
									
									
									
									lon_from_geom = lon_from_geom.replace("{0}", "p.position");
									
									sql = "SELECT " + lon_from_geom + " st_x from poi p WHERE p.id="+id;
									
									rs = stmt.executeQuery(sql);
									
									list2 = resultSetToArrayList(rs);
									
									
									
									if (!list2.isEmpty()){					
										longitude = new Double((list2.get(0)).get("st_x").toString());
										
										
										String lat_from_geom;					
										if (OutputDB.config ==null){
											lat_from_geom = OutputDB.configFEService.getLatFromPoint();
										}else{
											lat_from_geom = OutputDB.config.getLatFromPoint();
										}
										
										
										
										lat_from_geom = lat_from_geom.replace("{0}", "p.position");

										sql = "SELECT " + lat_from_geom + " st_y from poi p WHERE p.id="+id;
										rs = stmt.executeQuery(sql);
										
										list2 = resultSetToArrayList(rs);
										
										if (!list2.isEmpty()){					
											latitude = new Double((list2.get(0)).get("st_y").toString());											
											
										}
									}											
									
									
									
												
									
									//updated field might null
									Date updated = null;
									aux = (list.get(k)).get("updated");
									if (aux!=null){
										String date = aux.toString(); 					
										//convert to Date
										updated = Date.valueOf(date);
									}				
															 
									POI poi = new POI(id,name,latitude,longitude,updated);
									poi_array.add(poi);
								}
							}
							rs.close();
					        stmt.close();
							
						}	
					}
				
					
					
				} catch ( Exception e ) {
					System.out.println("Error POI.getPOIListByCategoryName(): "+e.getMessage());
					log.error("Error POI.getPOIListByCategoryName(): "+e.getMessage());
					e.printStackTrace();
				}
				
				return poi_array;
			}
			
			
			public static ArrayList<Category> getCategoryListByPOIId(Connection con, Integer poiid, Integer limit, Logger log){
				
				
				ArrayList<Category> category_array = new ArrayList<Category>();
				
				if (log == null) log = POI.log;
				Category category;
				
				try {
					ArrayList<POICategory> poicategory_array = POICategory.getPOICategoryListByPOIId(con, poiid, log);
					
					for (int k=0;k<poicategory_array.size();k++){
						Integer id = poicategory_array.get(k).getCategoryId();
						category = Category.getCategoryClassById(con, id, log);
						category_array.add(category);						
					}
					
				
					
					
				} catch ( Exception e ) {
					System.out.println("Error POI.getCategoryListByPOIId(): "+e.getMessage());
					log.error("Error POI.getCategoryListByPOIId(): "+e.getMessage());
					e.printStackTrace();
				}
				
				return category_array;
			}
			
			
			public static ArrayList<POI> getPOIListByCategoryNameAndRadius(Connection con, String categoryName, Integer limit,Double lat,Double lon, Integer radius, Logger log){
				
				ArrayList<POI> poi_array = new ArrayList<POI>();
				String sql;
				Statement stmt;
				ResultSet rs;
				
				
				ArrayList<HashMap<String, Object>> list;
				ArrayList<HashMap<String, Object>> list2;
				
				Double latitude = 0.0;
				Double longitude = 0.0;
				
				if (log == null) log = POI.log;
				
				try {
					Category category = Category.getCategoryClassByName(con, categoryName,log);
					if (category!=null){
						
						Integer categoryId = category.getId();
						
						if (categoryId !=null){
						
							stmt = con.createStatement();
							sql = "select * from poi inner join poicategory on poi.id = poicategory.poiid where poicategory.categoryid="+categoryId; 
							if ( (lat!=null) && (lon!=null) && (radius!=null) )
								sql = sql+ " and ST_DWithin(Geography(position),Geography(ST_MakePoint("+lon+","+lat+")),"+radius+")";	  
							if (limit!=null) 
								sql = sql+" limit "+limit;
							rs = stmt.executeQuery(sql);
							Object aux = null;
							
							
							list = resultSetToArrayList(rs);
							
							if (!list.isEmpty()){	
								
								for (int k=0;k<list.size();k++){
								
									//id field mandatory
									Integer id = new Integer((list.get(k)).get("id").toString());
									
									//name field mandatory
									String name = list.get(k).get("name").toString();
									

									//Get longitude and latitude
									String lon_from_geom;					
									if (OutputDB.config ==null){
										lon_from_geom = OutputDB.configFEService.getLonFromPoint();
									}else{
										lon_from_geom = OutputDB.config.getLonFromPoint();
									}
									
									
									
									lon_from_geom = lon_from_geom.replace("{0}", "p.position");
									
									sql = "SELECT " + lon_from_geom + " st_x from poi p WHERE p.id="+id;
									
									rs = stmt.executeQuery(sql);
									
									list2 = resultSetToArrayList(rs);
									
									
									
									if (!list2.isEmpty()){					
										longitude = new Double((list2.get(0)).get("st_x").toString());
										
										String lat_from_geom;					
										if (OutputDB.config ==null){
											lat_from_geom = OutputDB.configFEService.getLatFromPoint();
										}else{
											lat_from_geom = OutputDB.config.getLatFromPoint();
										}
										
										
										lat_from_geom = lat_from_geom.replace("{0}", "p.position");

										sql = "SELECT " + lat_from_geom + " st_y from poi p WHERE p.id="+id;
										rs = stmt.executeQuery(sql);
										
										list2 = resultSetToArrayList(rs);
										
										if (!list2.isEmpty()){					
											latitude = new Double((list2.get(0)).get("st_y").toString());											
											
										}
									}
												
									
									//updated field might null
									Date updated = null;
									aux = (list.get(k)).get("updated");
									if (aux!=null){
										String date = aux.toString(); 					
										//convert to Date
										updated = Date.valueOf(date);
									}				
															 
									POI poi = new POI(id,name,latitude,longitude,updated);
									poi_array.add(poi);
								}
							}
							rs.close();
					        stmt.close();
							
						}	
					}
				
					
					
				} catch ( Exception e ) {
					System.out.println("Error POI.getPOIListByCategoryNameAndRadius(): "+e.getMessage());
					log.error("Error POI.getPOIListByCategoryNameAndRadius(): "+e.getMessage());
				}
				
				return poi_array;
			}
			
			
			public static ArrayList<POI> getPOIListByCategoryNameAndBbox(Connection con, String categoryName, Integer limit,
					Double lat_min,Double lon_min, Double lat_max, Double lon_max, Logger log){
				
				ArrayList<POI> poi_array = new ArrayList<POI>();
				String sql;
				Statement stmt;
				ResultSet rs;
				
				
				ArrayList<HashMap<String, Object>> list;
				ArrayList<HashMap<String, Object>> list2;
				
				Double latitude = 0.0;
				Double longitude = 0.0;
				
				if (log == null) log = POI.log;
				
				try {
					Category category = Category.getCategoryClassByName(con, categoryName,log);
					if (category!=null){
						
						Integer categoryId = category.getId();
						
						if (categoryId !=null){
						
							stmt = con.createStatement();
							sql = "select * from poi inner join poicategory on poi.id = poicategory.poiid where poicategory.categoryid="+categoryId; 
							if ( (lat_min!=null) && (lon_min!=null) && (lat_max!=null) && (lon_max !=null) ){
								//sql = sql+ " and ST_DWithin(Geography(position),Geography(ST_MakePoint("+lon+","+lat+")),"+radius+")";	 
							
								sql = sql+ "and ST_Contains(ST_MakeEnvelope ("+lon_min+","+ lat_min+","+lon_max+","+lat_max+",4326), poi.position)";
							}
							if (limit!=null) 
								sql = sql+" limit "+limit;
							rs = stmt.executeQuery(sql);
							Object aux = null;
							
							
							list = resultSetToArrayList(rs);
							
							if (!list.isEmpty()){	
								
								for (int k=0;k<list.size();k++){
								
									//id field mandatory
									Integer id = new Integer((list.get(k)).get("id").toString());
									
									//name field mandatory
									String name = list.get(k).get("name").toString();
									
									//Get longitude and latitude
									String lon_from_geom;					
									if (OutputDB.config ==null){
										lon_from_geom = OutputDB.configFEService.getLonFromPoint();
									}else{
										lon_from_geom = OutputDB.config.getLonFromPoint();
									}
									
									lon_from_geom = lon_from_geom.replace("{0}", "p.position");
									
									sql = "SELECT " + lon_from_geom + " st_x from poi p WHERE p.id="+id;
									
									rs = stmt.executeQuery(sql);
									
									list2 = resultSetToArrayList(rs);
									
									
									
									if (!list2.isEmpty()){					
										longitude = new Double((list2.get(0)).get("st_x").toString());
										
										String lat_from_geom;					
										if (OutputDB.config ==null){
											lat_from_geom = OutputDB.configFEService.getLatFromPoint();
										}else{
											lat_from_geom = OutputDB.config.getLatFromPoint();
										}
										
										lat_from_geom = lat_from_geom.replace("{0}", "p.position");

										sql = "SELECT " + lat_from_geom + " st_y from poi p WHERE p.id="+id;
										rs = stmt.executeQuery(sql);
										
										list2 = resultSetToArrayList(rs);
										
										if (!list2.isEmpty()){					
											latitude = new Double((list2.get(0)).get("st_y").toString());											
											
										}
									}
												
									
									//updated field might null
									Date updated = null;
									aux = (list.get(k)).get("updated");
									if (aux!=null){
										String date = aux.toString(); 					
										//convert to Date
										updated = Date.valueOf(date);
									}				
															 
									POI poi = new POI(id,name,latitude,longitude,updated);
									poi_array.add(poi);
								}
							}
							rs.close();
					        stmt.close();
							
						}	
					}
				
					
					
				} catch ( Exception e ) {
					System.out.println("Error POI.getPOIListByCategoryNameAndBbox(): "+e.getMessage());
					log.error("Error POI.getPOIListByCategoryNameAndBbox(): "+e.getMessage());
				}
				
				return poi_array;
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
	
	public static void testPOI(Connection con) throws ParseException {
		System.out.println("****************************************************************************************");
		System.out.println("POI test. Creating, inserting,selecting and deleting a POI object from DB");	
		log.info("****************************************************************************************");
		log.info("POI test. Creating, inserting,selecting and deleting a POI object from DB");	
		
		String name = "some_name";
		Double latitude = -0.345547;
		Double longitude = 39.473408;
		//Set the current time
		Calendar cal = Calendar.getInstance();				
		SimpleDateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");				
		String date1 = outputformat.format(cal.getTime());		
		java.util.Date date2 = outputformat.parse(date1);						
		java.sql.Date sqlDate = new java.sql.Date(date2.getTime());
		
		POI poi = new POI(name,latitude,longitude,sqlDate);
		
		System.out.println("Creating POI class...... OK");
		log.info("Creating POI class......OK");
		
		
		Integer id = POI.savePOI(con, poi, null);
		if (id==null){
			System.out.println("Saving POI class...... Error ");
			log.info("Saving POI class...... Error ");
		}else{
			System.out.println("Saving POI class...... OK ");
			log.info("Saving POI class...... OK ");
			
			POI poi2 = POI.getPOIClassById(con, id,null);
			if (poi2==null){
				System.out.println("Getting POI class by ID...... Error ");
				log.info("Getting POI class by ID...... Error ");
			}else{
				System.out.println("Getting POI class by ID...... OK ");
				log.info("Getting POI class by ID...... OK ");
				
				if ( Double.compare(poi2.getLatitude(),latitude) == 0){
					System.out.println("Latitude matches...OK ");
					log.info("Latitude matches..OK ");
				}else{
					System.out.println("Error: Latitude does not match");
					log.info("Error: Latitude does not match ");
				}
				
				if ( Double.compare(poi2.getLongitude(),longitude) == 0){
					System.out.println("Longitude matches...OK ");
					log.info("Longitude matches..OK ");
				}else{
					System.out.println("Error: Longitude does not match");
					log.info("Error: Longitude does not match ");
				}
				
				poi2 = POI.getPOIClassByName(con, name,null);
				if (poi2 == null){
					System.out.println("Getting POI class by Name...... Error ");
					log.info("Getting POI class by Name...... Error ");
				}else{
					System.out.println("Getting POI class by Name...... OK ");
					log.info("Getting POI class by Name...... OK ");
					
					if (!POI.deletePOIById(con, id,null)){
						System.out.println("Deleting POI class by ID ...... Error ");
						log.info("Deleting POI class by ID ...... Error  ");
					}else{
						System.out.println("Deleting POI class by ID ...... OK ");
						log.info("Deleting POI class by ID ...... OK  ");
						System.out.println("All tests passed OK");
						log.info("All tests passed OK");						
					}
					
				}
				
			}
		}
		
		System.out.println("****************************************************************************************");
		log.info("****************************************************************************************");
		
		
	}
	
	private static DecimalFormat fmt = null;
	private static DecimalFormat formatter() {
		if (fmt == null) {
			fmt = new DecimalFormat("#.#######");
			DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
			fmt.setDecimalFormatSymbols(dfs);
		}
		return fmt;
	}
	

	
	

}
