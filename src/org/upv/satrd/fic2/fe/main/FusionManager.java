package org.upv.satrd.fic2.fe.main;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.upv.satrd.fic2.fe.config.ConfigFEService;
import org.upv.satrd.fic2.fe.db.OCD;

public class FusionManager {
	

	private static org.apache.log4j.Logger log =Logger.getLogger(org.upv.satrd.fic2.fe.main.FusionManager.class);
	
	public static void main(String[] args) {		
		
		//check that we have a valid input (one string with a valid (readable) directory). Otherwise take the current dir
		//String fusionDir = checkInput(args);	 
			
			String fusionDir = System.getProperty("user.dir");
			String configPath = System.getProperty("user.dir")+File.separator+"config.xml";			
			ConfigFEService configFile = new ConfigFEService(configPath);
			
			// 0. Change the log file
			FileAppender fileAppender;
			try {
				PatternLayout layout = new PatternLayout();
				String pattern = "%d{ABSOLUTE} - %m%n"; //"%m : %X{key1},%X{key2},%X{key3}%n";
				layout.setConversionPattern(pattern);
				
				
				if ( (configFile.getLogFile()==null) || (configFile.getLogFile().isEmpty())  ){
					configFile.setLogFile(fusionDir+File.separator+"fic2_fe_v3_service.log");
				}else{
					//update fusionDir
					String str = configFile.getLogFile();
					fusionDir = str.substring(0,str.lastIndexOf("/"));
				}
				
				System.out.println("Starting FusionManager. Setting fusionDir in "+fusionDir);
				
				/*
				//Create file if not exist
				File file_aux = new File(configFile.getLogFile());
				if(!file_aux.exists()) {
				    file_aux.createNewFile();
				} 
				*/
				
				fileAppender = new FileAppender(layout, configFile.getLogFile());
				fileAppender.setThreshold(Level.DEBUG);
				
				log.removeAllAppenders();
				log.addAppender(fileAppender);
			}catch (Exception ex){
				System.out.println(ex.getMessage());
			}
			log.info(" ");
			log.info("-----------------------------------------------------------------------------------------------------------------");
			log.info("--------------------------------------Starting FusionManager-----------------------------------------------------");
			log.info("-----------------------------------------------------------------------------------------------------------------");
			log.info(" ");
			
			
			
			
			// 1. Connect to OCD_BASE			
			Connection con = OutputDB.connectDB(
					configFile.getConnectionString(),
					configFile.getUser(),
					configFile.getPwd(),
					configFile.getDriverName(),null);
			
			if (con==null){
				log.error("Unable to get a connection to "+configFile.getConnectionString()+". Exiting...");
				System.out.println("Unable to get a connection to "+configFile.getConnectionString()+". Exiting...");
				System.exit(0);
			}
			
			// 2. Iterate through an endless loop and check for new OCDs to fusion or terminate
			
			while (true){					
				
				
				
				
				ArrayList<OCD> ocd_array = OCD.getOCDList(con,log);
				log.info("Available cities in the OCD_BASE database: "+ocd_array.size());
				for (int j=0;j<ocd_array.size();j++){
					
					OCD ocd = ocd_array.get(j);					
					String ocd_status = ocd.getStatus(); 
					
					
					
					// 2.1. Check for OCDs to be started
					//The check for starting fusion is signalled in the OCD_BASE database
					if (ocd_status.equalsIgnoreCase(OCD.STATUS_RUNNING_START)){
						
						//Get also the link to each ocd database
						String configPath2 = System.getProperty("user.dir")+File.separator+"config.xml";					
						ConfigFEService configFile2 = new ConfigFEService(configPath2);
						configFile2.setDBName("ocd_"+ocd.getName());
						//TODO. We might also change <usr,pass> in order not to use the 'root' user
						configFile2.setLogFile(fusionDir+File.separator+ocd.getId().toString()+".log");	
						System.out.println(fusionDir+File.separator+ocd.getId().toString()+".log");
						
						//start a new Thread and set the status to RUNNING
						log.info("Starting OCD "+ocd.getName()+" with ID "+ocd.getId()+".Check "+ocd.getId()+".log for details.");
						FusionEngine fe = new FusionEngine(configFile2);
						fe.start();
						ocd.setStatus(OCD.STATUS_RUNNING);
						OCD.updateOCDStatus(con, ocd,log);
						
						
						
					}else{			
						
								
						// 2.2 Check for OCDs to be terminated. We should check this on each ocd, but for this we need that the status
						// 	   in ocd_base in not NEW or INITIALIZED
						
						
						if  (  (!ocd_status.equalsIgnoreCase(OCD.STATUS_NEW)) &&  (!ocd_status.equalsIgnoreCase(OCD.STATUS_INITIALIZED)) ){
							//Get also the link to each ocd database
							String configPath2 = System.getProperty("user.dir")+File.separator+"config.xml";					
							ConfigFEService configFile2 = new ConfigFEService(configPath2);
							configFile2.setDBName("ocd_"+ocd.getName());
							//TODO. We might also change <usr,pass> in order not to use the 'root' user
							configFile2.setLogFile(fusionDir+File.separator+ocd.getId().toString()+".log");
							Connection con2 = OutputDB.connectDB(
									configFile2.getConnectionString(),
									configFile2.getUser(),
									configFile2.getPwd(),
									configFile2.getDriverName(),null);
							OCD ocd2= OCD.getOCDClassById(con2, 1,log);  //there should be only one OCD here
							
							
							String ocd_status2 = ocd2.getStatus();
							
							if (ocd_status2.equalsIgnoreCase(OCD.STATUS_RUNNING_END_OK)){
								//update the status in OCD_BASE and set the status to STATUS_FINISHED_OK
								ocd.setStatus(OCD.STATUS_FINISHED_OK);
								OCD.updateOCDStatus(con, ocd,log);
								ocd2.setStatus(OCD.STATUS_FINISHED_OK);
								OCD.updateOCDStatus(con2, ocd2,log);
							}
							
							if (ocd_status2.equalsIgnoreCase(OCD.STATUS_RUNNING_END_ERR)){
								//update the status in OCD_BASE and set the status to STATUS_FINISHED_ERR
								ocd.setStatus(OCD.STATUS_FINISHED_ERR);
								OCD.updateOCDStatus(con, ocd,log);
								ocd2.setStatus(OCD.STATUS_FINISHED_ERR);
								OCD.updateOCDStatus(con2, ocd2,log);
							}
							OutputDB.disconnectDB(con2,null);
							
							
						}
						
						
						
						
					}	
					
					
				} // for
				
				
				try {
					//Wait for 15 seconds; enough time for the new FusionEngine thread to start and change status to running
					//System.out.println("--------------------------------Sleeping for 15 s -----------------------------------------");
					log.info(" ");
					log.info("Sleeping for 15 s..........");
					
					
					Thread.sleep(15000);
					//System.out.println("--------------------------------Checking for changes -----------------------------------------");
					
					log.info("Waking up. Checking for changes........");
					log.info(" ");
				} catch (InterruptedException e) {
					e.printStackTrace();
					//close connection
					OutputDB.disconnectDB(con,null);
				}
				
			}// del while	
				
			
				
		
	}
	
	
	//This method is no longer used
	private static String checkInput(String[] args){
		
		
		if ( (args!=null) && (args.length>=1)  ){
			
			String dir = args[0]; //we don't care for any remaining parameter (there should not be any);
			File file = new File(dir);
			if (file.isDirectory()){
				System.out.println("Found a valid directory on "+dir);
				//log.info("Found a valid directory on "+dir);
				return dir;
			}
			else{
				System.out.println("Argument passed ("+dir+") is not a directory. Taking current Directory as default.");	
				//log.error("Argument passed ("+dir+") is not a directory. Taking current Directory as default.");				
				return System.getProperty("user.dir");
			}			
			
		}else{
			System.out.println("No arguments passed.Taking current Directory as default.");
			
			
			return System.getProperty("user.dir");
		}
		
		
		
	}

}
