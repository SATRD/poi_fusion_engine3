package org.upv.satrd.fic2.fe.test;


import java.io.File;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.upv.satrd.fic2.fe.config.Configuration;
import org.upv.satrd.fic2.fe.db.APIType;
import org.upv.satrd.fic2.fe.db.Category;
import org.upv.satrd.fic2.fe.db.City;
import org.upv.satrd.fic2.fe.db.Component;
import org.upv.satrd.fic2.fe.db.LabelType;
import org.upv.satrd.fic2.fe.db.License;
import org.upv.satrd.fic2.fe.db.OCD;
import org.upv.satrd.fic2.fe.db.POI;
import org.upv.satrd.fic2.fe.db.POICategory;
import org.upv.satrd.fic2.fe.db.POILabel;
import org.upv.satrd.fic2.fe.db.POISource;
import org.upv.satrd.fic2.fe.db.Source;
import org.upv.satrd.fic2.fe.main.OutputDB;



public class TestDBClasses {

	private static org.apache.log4j.Logger log;

	
	
	private static String configurationFile = "conf/config.xml";
	
	//@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		
		log = Logger.getLogger(org.upv.satrd.fic2.fe.test.TestDBClasses.class);	
		
		
		//Load configuration parameters of the OCD in order to access the PostGRESQL database
		Configuration config = new Configuration(configurationFile);
		Connection con = OutputDB.connectDB(
				config.getConnectionString(),
				config.getUser(),
				config.getPwd(),
				config.getDriverName(),null);	
		
		
		//Reset and Init database.
		// NOTE!!! It is supposed that psql is installed
		// and in the path, otherwise we cannot execute any script
		log.info("Reseting tables in database "+config.getDBName()
				+ " (user: " + config.getUser() + ")");
		System.out.println("Reseting tables in database "+config.getDBName()
				+ " (user: " + config.getUser() + ")");

		String sqlscript = config.getResetOCDScript();
		
		File script_file = new File(
				System.getProperty("user.dir") + File.separator + sqlscript);
		try {
			OutputDB.executeSql(
					script_file,
					config.getDriverName(),
					config.getUser(),
					config.getPwd(),
					config.getConnectionString(),
					config.getScriptSeparator());
		} catch (Exception e1) {
	        log.error(e1.getMessage()+".Exiting...");
	        System.exit(-1);
		}
		
	     System.out.println("Database tables have been reset");
	     log.info("Database tables have been reset");
		
	     
	     
		 // Init test
	     try{
	    	 
	    	 APIType.testAPIType(con);
	    	 Category.tesCategory(con);
	    	 City.testCity(con);
	    	 License.testLicense(con);
	    	 OCD.testOCD(con);
	    	 POI.testPOI(con);
	    	 POILabel.testPOILabel(con);
	    	 POISource.testPOISource(con);   	 	    	 
	    	 Source.testSource(con);
	    	 Component.testComponent(con);
	    	 LabelType.testLabelType(con);
	    	 POICategory.testPOICategory(con);
	    	 
	    	 
	    	 
	     }catch (Exception ex){
	    	 ex.printStackTrace();
	     }
	     
		
	    		
	    
	    
	}
	
}
