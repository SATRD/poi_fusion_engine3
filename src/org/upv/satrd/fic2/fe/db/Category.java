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

public class Category {

	private Integer id;
	private String name;
	private String description;
	private Integer level;
	private String icon;	
	
	private static org.apache.log4j.Logger log =Logger.getLogger(org.upv.satrd.fic2.fe.db.Category.class);
	
	
	
	
	//The Category object has been inserted in the DDB, and the id is known
	public Category(Integer id, String name, String description, Integer level, String icon){
		this.id = id;
		this.name = name;
		this.description = description;
		this.level = level;
		this.icon = icon;	
	}
	
	//The Category object has not been inserted in the DDB, and the id is not known
		public Category(String name, String description, Integer level, String icon){
			this.id = null;
			this.name = name;
			this.description = description;
			this.level = level;
			this.icon = icon;	
		}
	
	
	//GET METHODS
	public Integer getId(){return this.id;}
	public String getName(){return this.name;}
	public String getDescription(){return this.description;}
	public Integer getLevel(){return this.level;}
	public String getIcon(){return this.icon;}
	
	
	
	
	//SET METHODS
	public void setId(Integer id){this.id=id;}
	public void setName(String name){this.name = name;}
	public void setDescription(String description){this.description = description;}
	public void setLevel(Integer level){this.level = level;}
	public void setIcon(String icon){this.icon = icon;}
	
	
	
///////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////
////////////////////////////////DB methods/////////////////////////////////////////

	public static Category getCategoryClassById(Connection con, Integer id, Logger log){
		
		Statement stmt; 
		ResultSet rs; 
		ArrayList<HashMap<String, Object>> list;
		
		Category category = null;
		if (log == null) log = Category.log;
	
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM category WHERE id="+id;
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
			
			//level field mandatory
			Integer level = new Integer ((list.get(0)).get("level").toString());				
			
			
			//icon field optional 
			String icon = null;
			aux = (list.get(0)).get("icon");
			if (aux!=null) icon = aux.toString();
			
			category = new Category(id, name, description,level,icon);
			
			}			
			rs.close();
			stmt.close();  
		
		} catch ( SQLException e ) {
			System.out.println("Error Category.getCategoryClassById(): "+e.getMessage());
			log.error("Error Category.getCategoryClassById(): "+e.getMessage());
		}
		
		return category;
	
	}



	public static Category getCategoryClassByName(Connection con, String name, Logger log){
		Statement stmt; 
		ResultSet rs; 
		ArrayList<HashMap<String, Object>> list;
		
		Category category = null;
		if (log == null) log = Category.log;
		
			try {
				stmt = con.createStatement();
				String sql = "SELECT * FROM category WHERE name='"+name+"'";
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
				
				//level field mandatory
				Integer level = new Integer ((list.get(0)).get("level").toString());				
				
				
				//icon field optional 
				String icon = null;
				aux = (list.get(0)).get("icon");
				if (aux!=null) icon = aux.toString();
				
				category = new Category(id, name, description,level,icon);
				
				}			
				rs.close();
				stmt.close();  
			
			} catch ( SQLException e ) {
				System.out.println("Error Category.getCategoryClassByName(): "+e.getMessage());
				log.error("Error Category.getCategoryClassByName(): "+e.getMessage());
			}
			
			return category;
		
		}



	public static Integer saveCategory(Connection con, Category category, Logger log){
	
		
		ResultSet rs; 		
		Integer id=null;		
		String sql;
		PreparedStatement ps;		
		if (log == null) log = Category.log;
		
		try{   	
		
			sql = "INSERT INTO category (name,description,level,icon) VALUES (?,?,?,?)";
			
			
			
			String generatedColumns[] = { "id" };
			ps = con.prepareStatement(sql,
					generatedColumns);
			
			
			ps.setString(1,category.getName());				
			if (category.getDescription() == null) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2,category.getDescription());
			ps.setInt(3, category.getLevel());
			if (category.getIcon() == null) ps.setNull(4, java.sql.Types.VARCHAR); else ps.setString(4,category.getIcon());							
			
			
			ps.executeUpdate();
			
			rs = ps.getGeneratedKeys();
			if (rs.next()) { id = rs.getInt(1); }	
			
			rs.close();				
			ps.close();
		
		} catch ( SQLException e) {
			System.out.println("Error Category.saveCategory(): "+e.getMessage());
			log.error("Error Category.saveCategory(): "+e.getMessage());
		}	
		
		return id;
	}
	
	
	
	//for ref='ocd' we refer to the OCD database. Otherwise for the base database
	public static boolean deleteCategoryById(Connection con, Integer id, String ref, Logger log){			
		
		Statement stmt; 		
		String sql;	
		if (log == null) log = Category.log;
		
		try {
			stmt = con.createStatement();
			
			if (ref.equalsIgnoreCase("ocd")){
				//Delete all dependencies with other tables (poicategory, categoryrelation, ocdcategory )
				sql = "DELETE FROM poicategory WHERE categoryid="+id+";";
				stmt.executeUpdate(sql);
			}
			
			
			sql = "DELETE FROM categoryrelation WHERE categoryid1="+id+" OR categoryid2="+id+";";
			stmt.executeUpdate(sql);
			
			sql = "DELETE FROM ocdcategory WHERE categoryid="+id+";";
			stmt.executeUpdate(sql);		
			
			//Finally delete the category
			sql = "DELETE FROM category WHERE id="+id+";";
			stmt.executeUpdate(sql);
			
	        stmt.close();
		
		} catch ( SQLException e) {
			System.out.println("Error Category.deleteCategoryById(): "+e.getMessage());
			log.error("Error Category.deleteCategoryById(): "+e.getMessage());
			return false;
		}	

		return true;
	}
	
	
	

	public static boolean deleteCategoryByName(Connection con, String name, String ref, Logger log){	
		if (log == null) log = Category.log;		
		Category category = Category.getCategoryClassByName(con, name,log);
		if (category !=null)
			return Category.deleteCategoryById(con, category.getId(),ref,log);
		else
		return false;
	}
	
	
	
	
	public static boolean updateCategory(Connection con, Category category, Logger log){
		
		String sql;				
		PreparedStatement ps;
		if (log == null) log = Category.log;
		
		
			try{   	
				
				sql = "UPDATE category SET name=?, description=?, level=?, icon=? WHERE id="+category.getId();
	
				ps = con.prepareStatement(sql);	
				ps.setString(1,category.getName());				
				if (category.getDescription() == null) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2,category.getDescription());
				ps.setInt(3,category.getLevel());
				if (category.getIcon() == null) ps.setNull(4, java.sql.Types.VARCHAR); else ps.setString(4,category.getIcon());							
				
				
				log.debug("sql: "+ps.toString());
				ps.executeUpdate();
				
				ps.close();								
				
				
			} catch ( SQLException e) {
				System.out.println("Error Category.updateCategory(): "+e.getMessage());
				log.error("Error Category.updateCategory(): "+e.getMessage());
				return false;
			}	
	
		return true;
	}
	
	
	
	
	
	
	public static ArrayList<Category> getCategoryList (Connection con, Logger log){
		
		Statement stmt; 
		ResultSet rs; 
		
		
		ArrayList<Category> category_array = new ArrayList<Category>();
		String sql;
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = Category.log;
		
		try {
			stmt = con.createStatement();
			sql = "SELECT * FROM category ORDER BY name";
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
									
					//Level field mandatory 
					Integer level = new Integer((list.get(k)).get("level").toString());
					
					//icon field optional
					String icon = null;
					aux = (list.get(k)).get("icon");				
					if (aux!=null) icon = aux.toString();
											 
					Category category = new Category(id, name, description,level,icon);
					category_array.add(category);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error Category.getCategoryList(): "+e.getMessage());
			log.error("Error Category.getCategoryList(): "+e.getMessage());
		}
		
		return category_array;
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
	
	public static void tesCategory(Connection con) {
		System.out.println("****************************************************************************************");
		System.out.println("Category test. Creating, inserting,selecting and deleting a Category object from DB");	
		log.info("****************************************************************************************");
		log.info("Category test. Creating, inserting,selecting and deleting a Category object from DB");	
		
		String name = "some_name";
		String description = "some_description";
		Integer level = 1;
		String icon = "some_icon_url";
		Category category = new Category(name,description,level,icon);
		System.out.println("Creating Category class...... OK");
		log.info("Creating Category class......OK");
		
		
		Integer id = Category.saveCategory(con, category,null);
		
		if (id==null){
			System.out.println("Saving Category class...... Error ");
			log.info("Saving Category class...... Error ");
		}else{
			System.out.println("Saving Category class...... OK ");
			log.info("Saving Category class...... OK ");
			
			Category category2 = Category.getCategoryClassById(con, id,null);
			
			if (category2==null){
				System.out.println("Getting Category class by ID...... Error ");
				log.info("Getting Category class by ID...... Error ");
			}else{
				System.out.println("Getting Category class by ID...... OK ");
				log.info("Getting Category class by ID...... OK ");
				
				category2 = Category.getCategoryClassByName(con, name,null);
				if (category2 == null){
					System.out.println("Getting Category class by Name...... Error ");
					log.info("Getting Category class by Name...... Error ");
				}else{
					System.out.println("Getting Category class by Name...... OK ");
					log.info("Getting Category class by Name...... OK ");
					
					if (!Category.deleteCategoryById(con, id,"base",null)){
						System.out.println("Deleting Category class by ID ...... Error ");
						log.info("Deleting Category class by ID ...... Error  ");
					}else{
						System.out.println("Deleting Category class by ID ...... OK ");
						log.info("Deleting Category class by ID ...... OK  ");
						
						id = Category.saveCategory(con, category,null);
						if (id!=null){
							if (!Category.deleteCategoryByName(con, name,"base",null)){
								System.out.println("Deleting Category class by Name ...... Error ");
								log.info("Deleting Category class by Name ...... Error  ");
							}else{
								System.out.println("Deleting Category class by Name ...... OK ");
								log.info("Deleting Category class by Name ...... OK  ");
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

	
	
	
