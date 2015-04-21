package org.upv.satrd.fic2.fe.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.upv.satrd.fic2.fe.config.ConfigFEService;
import org.upv.satrd.fic2.fe.config.Configuration;
import org.upv.satrd.fic2.fe.db.APIType;
import org.upv.satrd.fic2.fe.db.Category;
import org.upv.satrd.fic2.fe.db.City;
import org.upv.satrd.fic2.fe.db.License;
import org.upv.satrd.fic2.fe.db.LicenseSource;
import org.upv.satrd.fic2.fe.db.OCD;
import org.upv.satrd.fic2.fe.db.OCDCategory;
import org.upv.satrd.fic2.fe.db.OCDSource;
import org.upv.satrd.fic2.fe.db.Source;
import org.upv.satrd.fic2.fe.db.SourceCity;
import org.upv.satrd.fic2.fe.fusionrules.FusionRule;
import org.upv.satrd.fic2.fe.fusionrules.FusionRules;

public class OutputDB {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(OutputDB.class);
	private static Statement stmt = null;
	private static ResultSet rs = null;
	
	public static String idToString(Object id) {
		if (id instanceof Integer || id instanceof Long) {
			return id.toString();
		}
		
		if (id instanceof byte[]) {
			System.out.println("ID is byte[]...");
		}
		
		return id.toString();
	}
	
	
	private static String configurationFile;
	public static Configuration config;
	
	public static ConfigFEService configFEService;
	
	//These methods are required to reload the configuration from a given File
	public static void setConfiguration(String path){
		configurationFile = path;
		config = new Configuration(configurationFile);
	}
	
	public static void setConfiguration(Configuration config){
		config = config;
	}
	
	public static void setConfigFEService(ConfigFEService config2){
		configFEService = config2;
	}
	
	
	public static Connection connectDB(
			String connStr,
			String user, String pwd,
			String driverName, Logger log){
		
		
		if (log == null) log = OutputDB.log;
	
		Connection con = null;
		
		String fe_host = System.getenv("FE_HOST");
		if ( (fe_host!=null) && (!fe_host.isEmpty()) ) {
			//log .debug("FE_HOST: "+fe_host);
			//change connStr
			//jdbc:postgresql://127.0.0.1:5432/ocd_valencia
			String pre = connStr.substring(0,connStr.indexOf("//")+2);
			String post = connStr.substring(connStr.lastIndexOf(":"));
			connStr = pre+fe_host+post;
			//log.debug("connString: "+connStr);
		}; 
		
		String fe_port = System.getenv("FE_PORT");
		if ( (fe_port!=null) && (!fe_port.isEmpty()) ) {
			//log .debug("FE_PORT: "+fe_port);
			//change connStr
			//jdbc:postgresql://127.0.0.1:5432/ocd_valencia
			String pre = connStr.substring(0,connStr.lastIndexOf(":")+1);
			String post = connStr.substring(connStr.lastIndexOf("/"));
			connStr = pre+fe_port+post;
			//log.debug("connString: "+connStr);
		}; 
		
		
		
		
		
		String fe_user = System.getenv("FE_USER");
		if ( (fe_user!=null) && (!fe_user.isEmpty()) ) user = fe_user; 
		//log .debug("FE_USER: "+fe_user);
		
		
		String fe_pwd = System.getenv("FE_PWD");
		if ( (fe_pwd!=null) && (!fe_pwd.isEmpty()) ) pwd = fe_pwd; 
		//log .debug("FE_PWD: "+fe_pwd);
		
		try {
			Class.forName(driverName);
			// DriverManager.getConne
			con = DriverManager.getConnection(
					connStr,
					user,
					pwd);

			con.setAutoCommit(true);
		} catch ( Exception e ) {
			//System.out.println("Error: "+e.getMessage());
			log.error(e.getMessage());
		}
	
		return con;
	}
	
	
	public static void disconnectDB (Connection con,Logger log){
		if (log == null) log = OutputDB.log;
		try {
			con.close();
		} catch ( Exception e ) {
			log.error(e.getMessage());
		}
	}
	
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

	
	
	
	
		
		//Executes a SQL script given the path to that file
		public static int execScript (Connection con, String dbScriptFile, Logger log){	
			
			if (log == null) log = OutputDB.log;
			int ret = -1;			 
			
			try {
				  stmt = con.createStatement();	    	      
    	      
	    	      BufferedReader in = new BufferedReader(new FileReader(dbScriptFile));
	    	      String str;
	    	      StringBuffer sb = new StringBuffer();
	    	      while ((str = in.readLine()) != null) {
	    	    	  sb.append(str + "\n ");
	    	      }
	    	      in.close();
	    	      stmt.executeUpdate(sb.toString());
    	          stmt.close();
    	          ret =1;
				
			} catch ( Exception e ) {
				System.out.println("Error: "+e.getMessage());
				log.error(e.getMessage());
				//e.printStackTrace();
				return -1;
			}
			
			return ret;		
		}
		
		//Executes a SQL command given as a String
		public static int execSQL (Connection con, String sqlCommand, Logger log){	
			
			if (log == null) log = OutputDB.log;
			int ret = -1;			 
			
			try {
				  stmt = con.createStatement();		    	      
	    	      stmt.executeUpdate(sqlCommand);
    	          stmt.close();
    	          ret =1;
				
			} catch ( Exception e ) {
				log.error(e.getMessage());
				return -1;
			}
			
			return ret;		
		}
		
		
		
		//FIXME. Now this method makes nothing. It should take data from each table of 'orig' and copy it in the corresponding table in 'dest'
		//Copies BASE DP part from one database to another
		public static int initDBCorePart(Connection con_orig, Connection con_dest, Logger log){
			
			String sql;
			if (log == null) log = OutputDB.log;
			
			try {
				stmt = con_orig.createStatement();		
				
							
				sql="SELECT * FROM source";
				rs = stmt.executeQuery(sql);
				
				while (rs.next()){  }
				
				
				stmt.close();
				return 1;
				
			} catch ( Exception e ) {
				System.out.println("Error: "+e.getMessage());
				log.error(e.getMessage());
				return -1;
			}
			
					
		}
		
		//This method is no longer used
		//This method takes a FusionRules class and initializes the core part with the information: city, bbox, categories, ocd,sources		
		public static int initDBFromXML(Connection con, FusionRules fusionRules, Logger log){
			
			int ret=0;
			if (log == null) log = OutputDB.log;
			
			//insert the city
			City city = new City(fusionRules.getCity(),fusionRules.getBbox());
			Integer cityId = City.saveCity(con, city,null);			
			if (cityId ==null) return -1;
			
			
			//Insert the OCD (only one). The 'FusionRules' filename does not matter for now
			OCD ocd = new OCD(fusionRules.getCity(),cityId,null,"fusionrules.xml","ocd_tenerife",null,OCD.STATUS_NEW);
			Integer ocdId = OCD.saveOCD(con, ocd,null);			
			if (ocdId ==null) return -1;
			
			//Go though the FusionRules
			ArrayList<FusionRule> frules = fusionRules.GetFusionrules(); 
			for (int i=0;i<frules.size(); i++){
				FusionRule frule = frules.get(i);			
				
				//Insert the category. It is supposed that they are not duplicated in the XML. The icon file does not matter for now
				Category category = new Category(frule.getCategory(),null,1,"category_icon.png");
				Integer categoryId = Category.saveCategory(con, category,null);
				
				if (categoryId == null) return -1;			
				
				//TODO We may introduce here the sources, but it is much simpler to insert all of them outside this for-statement, without requiring to check
				//whether the source has already been inserted
			}
			
			//insert the APITypes. For the moment, only citySDK
			APIType apitype = new APIType("citySDK",null,null);
			Integer apitypeId = APIType.saveAPIType(con, apitype,null);			
			if (apitypeId ==null) return -1;
			
			//insert the sources. OSM
			Source source = new Source("osm",null,"http://app.prodevelop.es/ficontent/api/osm/","conf/osm.properties",apitypeId);
			Integer sourceId = Source.saveSource(con, source,null);			
			if (sourceId == null) return -1;
			
			//insert the sources. DBPedia
			source = new Source("dbpedia",null,"http://app.prodevelop.es/ficontent/api/dbpedia/","conf/dbpedia.properties",apitypeId);
			sourceId = Source.saveSource(con, source,null);	
			if (sourceId == null) return -1;
			
			//insert the sources. POIProxy
			source = new Source("poiproxy",null,"http://app.prodevelop.es/ficontent/api/poiproxy/","conf/poiproxy.properties",apitypeId);
			sourceId = Source.saveSource(con, source,null);	
			if (sourceId == null) return -1;			
			
			
			//Tables sourcecity, ocdsource,ocdcategory do not make sense for standalone ocds
			
			//Insert table license 
			License license =new License("ODBl",null,null);
			Integer licenseId = License.saveLicense(con, license,null);			
			if (licenseId ==null) return -1;
			
			license =new License("Commercial_issues",null,null);
			licenseId = License.saveLicense(con, license,null);			
			if (licenseId ==null) return -1;
			
			license =new License("CCAS",null,null);
			licenseId = License.saveLicense(con, license,null);			
			if (licenseId ==null) return -1;
			
			license =new License("GNU_Free_Doc",null,null);
			licenseId = License.saveLicense(con, license,null);			
			if (licenseId ==null) return -1;		
			
			
			return ret;
					
		}
		
		
		//This method takes initializes the categories in the OCD database (the other components have already been set)	
		public static boolean initDBCategoryFromXML(Connection con, FusionRules fusionRules, Logger log){			
			
			if (log == null) log = OutputDB.log;
			boolean bool = false;
			//Go though the FusionRules
			ArrayList<FusionRule> frules = fusionRules.GetFusionrules(); 
			for (int i=0;i<frules.size(); i++){
				FusionRule frule = frules.get(i);			
				
				//Insert the category. It is supposed that they are not duplicated in the XML. The icon file does not matter for now
				Category category = new Category(frule.getCategory(),null,1,"category_icon.png");
				Integer categoryId = Category.saveCategory(con, category, log);
				
				if (categoryId == null) return false;					
			}
			
				
			
			
			return bool;
					
		}
		
		
		
		public static void executeSql(
				File sqlFile,
				String driver,
				String user,
				String pwd,
				String url,
				String scr_sep) throws Exception {
			
		    final class SqlExecuter extends SQLExec {
		        public SqlExecuter() {
		            Project project = new Project();
		            project.init();
		            setProject(project);
		            setTaskType("sql");
		            setTaskName("sql");
		        }
		    }
		    
		    final class OnErrorContinue extends SQLExec.OnError {
		    	
		    	public OnErrorContinue() {
		    		this.setValue("continue");
		    	}
		    	
		    	public String[] getValues() {
		    		return new String[]{"continue"};
		    	}
		    }

		    SqlExecuter executer = new SqlExecuter();
		    executer.setOnerror(new OnErrorContinue());
		    //executer.setOutput(new File("/home/bmolina/fusion/out_sql.txt"));
		    executer.setDelimiter(scr_sep);
		    executer.setSrc(sqlFile);
		    executer.setDriver(driver);
		    executer.setPassword(pwd);
		    executer.setUserid(user);
		    executer.setUrl(url);
		    executer.execute();
		    
		} 

		
		public static boolean deleteDB(Configuration configFile, String dbName){
			boolean bool = false;
			
						
			//Get Connection. For creating the database we need to connect to postgres (or Oracle)
			String dbName_old = configFile.getDBName();
			configFile.setDBName("postgres");
			Connection con = OutputDB.connectDB(
					configFile.getConnectionString(),
					configFile.getUser(),
					configFile.getPwd(),
					configFile.getDriverName(),null);
			
			
			String sql = "DROP DATABASE IF EXISTS "+dbName;
			log.debug("Deleting database: "+sql);
			int ret = OutputDB.execSQL(con, sql,null); 
			
			OutputDB.disconnectDB(con,null);
			
			configFile.setDBName(dbName_old); //restore the name
			
			if (ret == -1){				
				String state = "There was an error deleting the database. Please read the log file for more information";
				log.error(state);
				return false;
		        
			}else bool = true;
			
			return bool;
			
		}
		
		
		
		
		
		public static boolean createDBAndEnable2D(Configuration configFile, String dbName,String owner,String scriptFile2D){
			boolean bool = false;
			
						
			// 1. Create database
			
			//Get Connection. For creating the database we need to connect to postgres (or Oracle)
			String dbName_old = configFile.getDBName();
			configFile.setDBName("postgres");
			Connection con = OutputDB.connectDB(
					configFile.getConnectionString(),
					configFile.getUser(),
					configFile.getPwd(),
					configFile.getDriverName(),null);
			
			
			String sql = "CREATE DATABASE "+dbName+" WITH ENCODING='UTF8' template template0 OWNER="+owner+" CONNECTION LIMIT=-1; ";
			log.debug("Creating database: "+sql);
			int ret = OutputDB.execSQL(con, sql,null); 
			
			OutputDB.disconnectDB(con,null);
			
			configFile.setDBName(dbName); //restore the name
			
			if (ret == -1){				
				String state = "There was an error generating the database. Please read the log file for more information";
				log.error(state);
				return false;
		        
			}else{
				
					// 2. Create the extension in POSTGIS
					con = OutputDB.connectDB(
						configFile.getConnectionString(),
						configFile.getUser(),
						configFile.getPwd(),
						configFile.getDriverName(),null);
					
					log.debug(scriptFile2D);
					log.debug(configFile.getConnectionString());
					
					File script_file = new File(scriptFile2D);
					//log.debug("Enabling postgis extension with file at "+getServletContext().getRealPath(sqlscript));
							//System.getProperty("user.dir") + File.separator + sqlscript);
					try {
						OutputDB.executeSql(
								script_file,
								configFile.getDriverName(),
								configFile.getUser(),
								configFile.getPwd(),
								configFile.getConnectionString(),
								configFile .getScriptSeparator());
						bool = true;
						
					} catch (Exception e1) {
						
						String state = "There was an error creating extension postgis";
						log.error(state);
					}
					OutputDB.disconnectDB(con,null);
					
				}
			    configFile.setDBName(dbName_old); //restore the name
			
			
			
			return bool;
			
		}
		
		public static boolean resetDB(Configuration configFile, String dbName,String resetScriptFile){
			
			
			boolean bool = false;
						
			String dbName_old = configFile.getDBName();
			configFile.setDBName(dbName);
			
					
			try {
				OutputDB.executeSql(
						new File(resetScriptFile),
						configFile.getDriverName(),
						configFile.getUser(),
						configFile.getPwd(),
						configFile.getConnectionString(),
						configFile .getScriptSeparator());
						
				bool = true;
				
				log.debug("DB "+dbName+" has been reset with the connString: "+configFile.getConnectionString());
				configFile.setDBName(dbName_old); //restore the name
				
			} catch (Exception e1) {		
					
				log.error(e1.getMessage());
				String state = "There was an error creating tables in the database "+configFile.getDBName();	
				log.error(state);
				return false;
			}	
			
			
			return bool;
			
		}
		
		
		public static boolean insertOCDBaseSources(Configuration configFile, String categoryMappingDir){
			boolean bool = false;
			String sql;
			int ret;
			
			Connection con = OutputDB.connectDB(
					configFile.getConnectionString(),
					configFile.getUser(),
					configFile.getPwd(),
					configFile.getDriverName(),null);
			
			String osm_mappingFile = categoryMappingDir+"/osm.properties";
			sql = "INSERT INTO source (name, description, urlaccess, categorymapping, apitypeid) VALUES "+
			"('osm', NULL, 'http://app.prodevelop.es/ficontent/api/osm/', '"+osm_mappingFile+"', 1)";
			ret = OutputDB.execSQL (con, sql,null); 
			
			String dbpedia_mappingFile = categoryMappingDir+"/dbpedia.properties";
			sql = "INSERT INTO source (name, description, urlaccess, categorymapping, apitypeid) VALUES "+
			"('dbpedia', NULL, 'http://app.prodevelop.es/ficontent/api/dbpedia/', '"+dbpedia_mappingFile+"', 1)";
			ret = OutputDB.execSQL (con, sql,null); 
			

			String poiproxy_mappingFile = categoryMappingDir+"/poiproxy.properties";
			sql = "INSERT INTO source (name, description, urlaccess, categorymapping, apitypeid) VALUES "+
			"('poiproxy', NULL, 'http://app.prodevelop.es/ficontent/api/poiproxy/', '"+poiproxy_mappingFile+"', 1)";
			ret = OutputDB.execSQL (con, sql,null); 
			
			
			poiproxy_mappingFile = categoryMappingDir+"/poiproxy_local_valencia.properties";
			sql = "INSERT INTO source (name, description, urlaccess, categorymapping, apitypeid) VALUES "+
			"('poiproxy_local_valencia', NULL, 'http://app.prodevelop.es/ficontent/api/poiproxy/', '"+poiproxy_mappingFile+"', 1)";
			ret = OutputDB.execSQL (con, sql,null); 
			
			poiproxy_mappingFile = categoryMappingDir+"/poiproxy_local_tenerife.properties";
			sql = "INSERT INTO source (name, description, urlaccess, categorymapping, apitypeid) VALUES "+
			"('poiproxy_local_tenerife', NULL, 'http://app.prodevelop.es/ficontent/api/poiproxy/', '"+poiproxy_mappingFile+"', 1)";
			ret = OutputDB.execSQL (con, sql,null); 
			
			
			//4. Insert in the Sourcecity table the correspondence with the previous sources and the sample cities (all, valencia, tenerife)
			sql = "INSERT INTO sourcecity (sourceid,cityid) VALUES (1,1)";  //OSM maps to all (global datasource)
			ret = OutputDB.execSQL (con, sql,null);
			
			sql = "INSERT INTO sourcecity (sourceid,cityid) VALUES (2,1)";  //DBPedia maps to all (global datasource)
			ret = OutputDB.execSQL (con, sql,null);
			
			sql = "INSERT INTO sourcecity (sourceid,cityid) VALUES (3,1)";  //Poiproxy maps to all (global datasource)
			ret = OutputDB.execSQL (con, sql,null);
			
			sql = "INSERT INTO sourcecity (sourceid,cityid) VALUES (4,3)";  //Poiproxy_local_valencia maps to valencia (local datasource)
			ret = OutputDB.execSQL (con, sql,null);
			
			sql = "INSERT INTO sourcecity (sourceid,cityid) VALUES (5,2)";  //Poiproxy_local_tenerife maps to tenerife (local datasource)
			ret = OutputDB.execSQL (con, sql,null);
			
			
			//6. Insert in the licensesource table the corresponde with the previous sources and the available licenses
			sql = "INSERT INTO licensesource (sourceid,licenseid) VALUES (1,1)";  //OSM maps to ODbl (1)
			ret = OutputDB.execSQL (con, sql,null);
			
			sql = "INSERT INTO licensesource (sourceid,licenseid) VALUES (2,3)";  //DBPedia maps to GPL (3)
			ret = OutputDB.execSQL (con, sql,null);
			
			sql = "INSERT INTO licensesource (sourceid,licenseid) VALUES (3,2)";  //Poiproxy maps to CC(2) ,Apache2(5) and CI(4)
			ret = OutputDB.execSQL (con, sql,null);
			sql = "INSERT INTO licensesource (sourceid,licenseid) VALUES (3,5)";  //Poiproxy maps to CC(2) ,Apache2(5) and CI(4)
			ret = OutputDB.execSQL (con, sql,null);
			sql = "INSERT INTO licensesource (sourceid,licenseid) VALUES (3,4)";  //Poiproxy maps to CC(2) ,Apache2(5) and IC(4)
			ret = OutputDB.execSQL (con, sql,null);
			
			sql = "INSERT INTO licensesource (sourceid,licenseid) VALUES (4,3)";  //Poiproxy_local_valencia maps to Apache2 (5)
			ret = OutputDB.execSQL (con, sql,null);
			
			sql = "INSERT INTO licensesource (sourceid,licenseid) VALUES (5,3)";  //Poiproxy_local_tenerife maps to Apache2 (5)
			ret = OutputDB.execSQL (con, sql,null);
			
					
			if (ret == -1){
					String state = "There was an error inserting sources in the database";	
					log.error(state);
			}else{
				bool = true;
			}
			OutputDB.disconnectDB(con,null);
			
			
			
			return bool;
		}
		
		
		public static boolean insertOCDBaseValenciaDemoCity(Configuration configFile,String fusionDir){
			boolean bool = false;
					
			
			Connection con = OutputDB.connectDB(
					configFile.getConnectionString(),
					configFile.getUser(),
					configFile.getPwd(),
					configFile.getDriverName(),null);
			
			
			String name = "valencia_demo";
			Integer city_id = City.getCityClassByName(con,"valencia",null).getId();
			String description =  "ocd sample for valencia";							
			String fusion_path = fusionDir+"/FusionRules_Valencia.xml";									
			String accesskey = "valencia_demo";							
			java.sql.Date fusionDate = null;							
			String status = OCD.STATUS_NEW;
			OCD valencia = new OCD(name, city_id, description, fusion_path, accesskey, fusionDate, status );
			int id_selec = OCD.saveOCD (con, valencia,null);	
			
			
			if (id_selec > 0){
				
				//Update the ocdsource table. FusionRules_Valencia.xml uses all other base sources except for Tenerife	    			
    			boolean check = true;
    			boolean bool2;
    			
				ArrayList<Source> sources = Source.getSourceList(con,null);		
    			for (int j=0;j<sources.size();j++){
    				Source source = sources.get(j);
    				if (!source.getName().equalsIgnoreCase("poiproxy_local_tenerife")){
    					Integer in = new Integer(source.getId());
		        		OCDSource ocdsource = new OCDSource(new Integer(id_selec),in);
		        		bool2 = OCDSource.saveOCDSource(con,ocdsource,null);
		        		if (!bool2) check = false;
    				}
    							        		
    			}    			
    			if (check){  
    				
    					//Update the ocdcategory table. FusionRules_Valencia.xml uses fallas, museum,monument,accommodation
    					
	    				ArrayList<Category> categories = Category.getCategoryList(con,null);		
	        			for (int j=0;j<categories.size();j++){
	        				Category category = categories.get(j);
	        				if (   (category.getName().equalsIgnoreCase("fallas"))   ||  (category.getName().equalsIgnoreCase("museum")) 
	        						|| (category.getName().equalsIgnoreCase("monument")) || (category.getName().equalsIgnoreCase("accommodation"))    ){
	        					Integer in = new Integer(category.getId());
	    		        		OCDCategory ocdcategory = new OCDCategory(new Integer(id_selec),in);
	    		        		bool2 = OCDCategory.saveOCDCategory(con,ocdcategory,null);
	    		        		if (!bool2) check = false;
	        				}
	        							        		
	        			} 
	        			if (check){
    				
			    			log.info("OCD correctly saved");
			    			bool = true;	
	        			}else{
	        				String state = "There was a problem storing the OCD while updating OCDCategory table";
	    	    			return false;
	        			}
						
    			}else{    				
	    			String state = "There was a problem storing the OCD while updating OCDSource table";
	    			return false;
    			} 		
				
				
				
			}else{				
				String state = "There was a problem saving the OCD in the database";
    			return false;
				
			}
			
			
			return bool;
			
		}
		
		
		public static boolean insertOCDBaseTenerifeDemoCity(Configuration configFile,String fusionDir){
			boolean bool = false;
					
			
			Connection con = OutputDB.connectDB(
					configFile.getConnectionString(),
					configFile.getUser(),
					configFile.getPwd(),
					configFile.getDriverName(),null);
			
			
			String name = "tenerife_demo";
			Integer city_id = City.getCityClassByName(con,"tenerife",null).getId();
			String description =  "ocd sample for tenerife";							
			String fusion_path = fusionDir+"/FusionRules_Tenerife.xml";									
			String accesskey = "tenerife_demo";							
			java.sql.Date fusionDate = null;							
			String status = OCD.STATUS_NEW;
			OCD valencia = new OCD(name, city_id, description, fusion_path, accesskey, fusionDate, status );
			int id_selec = OCD.saveOCD (con, valencia,null);	
			
			
			if (id_selec > 0){
				
				//Update the ocdsource table. FusionRules_Tenerife.xml uses all other base sources except for Valencia	    			
    			boolean check = true;
    			boolean bool2;
    			
				ArrayList<Source> sources = Source.getSourceList(con,null);		
    			for (int j=0;j<sources.size();j++){
    				Source source = sources.get(j);
    				if (!source.getName().equalsIgnoreCase("poiproxy_local_valencia")){
    					Integer in = new Integer(source.getId());
		        		OCDSource ocdsource = new OCDSource(new Integer(id_selec),in);
		        		bool2 = OCDSource.saveOCDSource(con,ocdsource,null);
		        		if (!bool2) check = false;
    				}
    							        		
    			}    			
    			if (check){  
    				
    					//Update the ocdcategory table. FusionRules_Tenerife.xml uses museum,monument,accommodation,tourist_info,shop
    					
	    				ArrayList<Category> categories = Category.getCategoryList(con,null);		
	        			for (int j=0;j<categories.size();j++){
	        				Category category = categories.get(j);
	        				if (   (category.getName().equalsIgnoreCase("museum"))   ||  (category.getName().equalsIgnoreCase("tourist_info")) 
	        						|| (category.getName().equalsIgnoreCase("monument")) || (category.getName().equalsIgnoreCase("accommodation"))
	        						|| (category.getName().equalsIgnoreCase("shop"))   ){
	        					Integer in = new Integer(category.getId());
	    		        		OCDCategory ocdcategory = new OCDCategory(new Integer(id_selec),in);
	    		        		bool2 = OCDCategory.saveOCDCategory(con,ocdcategory,null);
	    		        		if (!bool2) check = false;
	        				}
	        							        		
	        			} 
	        			if (check){
    				
			    			log.info("OCD correctly saved");
			    			bool = true;	
	        			}else{
	        				String state = "There was a problem storing the OCD while updating OCDCategory table";
	    	    			return false;
	        			}
						
    			}else{    				
	    			String state = "There was a problem storing the OCD while updating OCDSource table";
	    			return false;
    			} 		
				
				
				
			}else{				
				String state = "There was a problem saving the OCD in the database";
    			return false;
				
			}
			
			
			return bool;
			
		}
		
		
		
		
		public static boolean initializeOCD(Configuration configFile, OCD ocd_base){
			boolean bool=false;
			
			//We need to copy the contents of ocd from 'ocd_base' to 'ocd_<ocd_name>'. So we need two connections 
						
			Connection con_base = OutputDB.connectDB(
					configFile.getConnectionString(),
					configFile.getUser(),
					configFile.getPwd(),
					configFile.getDriverName(),null);
			
			configFile.setDBName("ocd_"+ocd_base.getName());;
			
			Connection con_ocd = OutputDB.connectDB(
					configFile.getConnectionString(),
					configFile.getUser(),
					configFile.getPwd(),
					configFile.getDriverName(),null);
			
			
			//1. city table
			City city = City.getCityClassById(con_base, ocd_base.getCityId(),null);
			Integer id_city = City.saveCity(con_ocd, city,null);
			if (id_city == null){
				log.error("Error initializeOCD(). Saving city");
				return false;
			}
			
			
			//2. OCD table and OCDCategory
			OCD ocd_ocd = ocd_base;
			ocd_ocd.setCityId(id_city);
			Integer id_ocd = OCD.saveOCD(con_ocd, ocd_ocd,null);
			if (id_ocd == null){
				log.error("Error initializeOCD(). Saving ocd");
				return false;
			}
			
			ArrayList<OCDCategory> ocdcategory_array = OCDCategory.getOCDCategoryListByOCDId(con_base, ocd_base.getId(),null);
			for (int j=0;j<ocdcategory_array.size();j++){
				
				OCDCategory ocdcategory = ocdcategory_array.get(j);
				Category category = Category.getCategoryClassById(con_base, ocdcategory.getCategoryId(),null);
				
				Integer id_cat = Category.saveCategory(con_ocd, category,null);
				if (id_cat !=null){
					OCDCategory ocdcategory2 = new OCDCategory(id_ocd,id_cat);
					bool = OCDCategory.saveOCDCategory(con_ocd, ocdcategory2,null);
					if (!bool) log.error("Error initializeOCD(). Saving OCDCategory");
				}else{
					log.error("Error initializeOCD(). Saving category");
				}
				
			}
			
			
			//3. OCDSource and Source
			ArrayList<OCDSource> ocdsource_array = OCDSource.getOCDSourceListByOCDId(con_base, ocd_base.getId(),null);
			for (int j=0;j<ocdsource_array.size();j++){
				
				OCDSource ocdsource = ocdsource_array.get(j);
				Source source = Source.getSourceClassById(con_base, ocdsource.getSourceId(),null);
				
				//First APIType
				APIType apitype = APIType.getAPITypeClassById(con_base, source.getAPITypeId(),null);
				Integer id_apitype = APIType.saveAPIType(con_ocd, apitype,null);
				if (id_apitype == null){
					log.error("Error initializeOCD(). Saving aPIType");
					return false;
				}
				
				
				source.setAPITypeId(id_apitype);
				Integer id_source = Source.saveSource(con_ocd, source,null);
				if (id_source == null){
					log.error("Error initializeOCD(). Saving Source");
					return false;
				}
				
				if (id_source != null){
					//No need to do that, but we should also insert the OCDSource table
					OCDSource ocdsource2 = new OCDSource(id_ocd,id_source);
					bool = OCDSource.saveOCDSource(con_ocd, ocdsource2,null);
					if (!bool){
						log.error("Error initializeOCD. Saving OCDSource");
						return false;
					}
				}
				
				if (id_source != null){
					//As there is only one city, we can do this here. Sourcecity table
					SourceCity sourcecity = new SourceCity(id_source,id_city);
					bool= SourceCity.saveSourceCity(con_ocd, sourcecity,null);
					if (!bool){
						log.error("Error initializeOCD(). Saving SourceCity");
						return false;
					}
				}
				
				if (id_source != null){
					//LicenseSource and License table
					ArrayList<LicenseSource> licensesource_array = LicenseSource.getLicenseSourceListBySourceId(con_base, id_source,null);
					for (int i=0;i<licensesource_array.size();i++){
						LicenseSource licensesource = licensesource_array.get(i);
						License license = License.getLicenseClassById(con_base, licensesource.getLicenseId(),null);
						
						//maybe the license is already in the table
						License license2 = License.getLicenseClassByName(con_ocd, license.getName(),null);
						if (license2==null){
							Integer id_license = License.saveLicense(con_ocd, license,null);
							if (id_license == null){
								log.error("Error initializeOCD(). Saving License");
								return false;
							}
							licensesource.setLicenseId(id_license);
							
						}else{
							licensesource.setLicenseId(license2.getId());
						}
						bool= LicenseSource.saveLicenseSource(con_ocd, licensesource,null);			
						if (!bool){
							log.error("Error initializeOCD(). Saving LicenseSource");
							return false;
						}
					}
					
				}			
				
				
			}
			
			//Finally change the status of the OCD
			ocd_base.setStatus(OCD.STATUS_INITIALIZED);			
			bool = OCD.updateOCDStatus(con_base,ocd_base,null);
			if (!bool){
				log.error("Error initializeOCD. Changing status to OCD_BASE");
				return false;
			}
			
			ocd_ocd.setStatus(OCD.STATUS_INITIALIZED);			
			bool = OCD.updateOCDStatus(con_ocd,ocd_ocd,null);
			if (!bool){
				log.error("Error initializeOCD. Changing status to OCD_OCD");
				return false;
			}
			
			bool = true;
			
			
			
			
			
			OutputDB.disconnectDB(con_base,null);
			OutputDB.disconnectDB(con_ocd,null);
			
			
			return bool;
		}
		
		
		
		//This method should work, but doesn't
		public static boolean resetPOIData(ConfigFEService configFile, String resetScriptFile, Logger log){
			
			
			boolean bool = false;	
			if (log == null) log = OutputDB.log;
			
					
			try {
				OutputDB.executeSql(
						new File(resetScriptFile),
						configFile.getDriverName(),
						configFile.getUser(),
						configFile.getPwd(),
						configFile.getConnectionString(),
						configFile .getScriptSeparator());
						
				bool = true;
				System.out.println("scriptfile: "+resetScriptFile);
				System.out.println("connString: "+configFile.getConnectionString());
				
				log.debug("POI data has been reset with the connString: "+configFile.getConnectionString());				
				
			} catch (Exception e1) {		
					
				log.error("OutputDB. resetPOIData: "+e1.getMessage());
				System.out.println("OutputDB. resetPOIData: "+e1.getMessage());
				
				return false;
			}	
			
			
			return bool;
			
		}
		

	public static boolean resetPOIData2(ConfigFEService configFile, String resetScriptFile, Logger log){
	
		
		boolean bool = false;	
		if (log == null) log = OutputDB.log;
		
		
		try {
	        Runtime rt = Runtime.getRuntime();
	        String user = configFile.getUser();
	        String host = configFile.getHost();
	        String dbName = configFile.getDBName();
	        
	        String executeSqlCommand = "psql -U "+user+" -h "+host+" -f "+resetScriptFile+" "+dbName;
	        //System.out.println(executeSqlCommand);
	        Process pr = rt.exec(executeSqlCommand);
	        int exitVal = pr.waitFor();
	        if (exitVal==0) bool=true;
	     } catch (Exception e1) {
	    	 log.error("OutputDB. resetPOIData: "+e1.getMessage());
	 		System.out.println("OutputDB. resetPOIData: "+e1.getMessage());
	     }
				
		
		
		
		return bool;
	
	}
	
	
	public static boolean dumpData(String host, String dbName, String user, String dumpFile, Logger log){
	
		
		boolean bool = false;	
		if (log == null) log = OutputDB.log;
		
		
		try {
	        Runtime rt = Runtime.getRuntime();
	        
	        	        
	        
	        String executeSqlCommand = "psql -U "+user+" -h "+host+" -f "+dumpFile+" "+dbName;
	        //System.out.println(executeSqlCommand);
	        Process pr = rt.exec(executeSqlCommand);
	        int exitVal = pr.waitFor();
	        if (exitVal==0) bool=true;
	     } catch (Exception e1) {
	    	 log.error("OutputDB. dumpData: "+e1.getMessage());
	 		System.out.println("OutputDB. dumpData: "+e1.getMessage());
	     }
				
		
		
		
		return bool;
	
	}

		
		
		
	
	
}
