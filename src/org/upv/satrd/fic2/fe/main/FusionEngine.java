package org.upv.satrd.fic2.fe.main;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.upv.satrd.fic2.fe.config.ConfigFEService;
import org.upv.satrd.fic2.fe.config.Configuration;
import org.upv.satrd.fic2.fe.connectors.citySDK.Fusion;
import org.upv.satrd.fic2.fe.connectors.citySDK.FusionResult;
import org.upv.satrd.fic2.fe.connectors.citySDK.Collector;
import org.upv.satrd.fic2.fe.connectors.citySDK.CommonUtils;
import org.upv.satrd.fic2.fe.db.APIType;
import org.upv.satrd.fic2.fe.db.OCD;
import org.upv.satrd.fic2.fe.db.POI;
import org.upv.satrd.fic2.fe.db.POILabel;
import org.upv.satrd.fic2.fe.db.POISource;
import org.upv.satrd.fic2.fe.db.Source;
import org.upv.satrd.fic2.fe.fusionrules.FusionRule;
import org.upv.satrd.fic2.fe.fusionrules.FusionRules;
import org.upv.satrd.fic2.fe.fusionrules.FusionRulesParser;

public class FusionEngine extends Thread{

	private static org.apache.log4j.Logger log;
	
	
	
	private ConfigFEService configFile;
	OCD ocd;
	
	
	
	
	public FusionEngine(ConfigFEService configFile) {		
		
		this.configFile = configFile;		
		
		//Create log file for this Thread
		log = Logger.getLogger(org.upv.satrd.fic2.fe.main.FusionEngine.class);	
		
		FileAppender fileAppender;
		try {
			PatternLayout layout = new PatternLayout();
			//String pattern = "%d{ABSOLUTE} - %m%n"; //"%m : %X{key1},%X{key2},%X{key3}%n";
			String pattern = "%d{ABSOLUTE} %5p - %m%n";
			layout.setConversionPattern(pattern);
			
			fileAppender = new FileAppender(layout, configFile.getLogFile());
			fileAppender.setThreshold(Level.DEBUG);
			log.removeAllAppenders();
			log.addAppender(fileAppender);
			
			log.info(" ");
			log.info("-----------------------------------------------------------------------------------------------------------------");
			log.info("--------------------------------------Starting FusionEngine Thread-----------------------------------------------");
			log.info("-----------------------------------------------------------------------------------------------------------------");
			log.info(" ");
			
			
			Connection con = OutputDB.connectDB(
					configFile.getConnectionString(),
					configFile.getUser(),
					configFile.getPwd(),
					configFile.getDriverName(),log);
			
			ocd = OCD.getOCDClassById(con, 1,log);
			
			ocd.setStatus(OCD.STATUS_RUNNING_START);
			
			OCD.updateOCDStatus(con, ocd,log);
			
			OutputDB.disconnectDB(con,log);
			
			log.info("Initializing OCD "+ocd.getName()+". Setting status to RUNNING_START");
			
			
			
		} catch (Exception e) {			
			System.out.println(e.getMessage());
		}	
		
	}
	
	
	
	

	
	
	//@SuppressWarnings("rawtypes")
	public void run() {		
		
		
		boolean end = false;
		
		
		
		Connection con = OutputDB.connectDB(
				configFile.getConnectionString(),
				configFile.getUser(),
				configFile.getPwd(),
				configFile.getDriverName(),log);
		
		
		OutputDB.setConfigFEService(configFile);
		
			
		// Get OCD. There should be only one, so lets go faster than getting the OCD ArrayList
		ocd = OCD.getOCDClassById(con, 1,log);
		
		ocd.setStatus(OCD.STATUS_RUNNING);		
		OCD.updateOCDStatus(con, ocd,log);
		
		//We should reset the POI part of the OCD (just in case we want to relaunch the fusion)
		// This includes tables poi, poicategory, poisource, poilabel, labeltype
		
		String sqlFile = System.getProperty("user.dir")+File.separator+configFile.getResetOCDPOIScript();
		System.out.println("sqlFile: "+sqlFile);
		boolean reset = OutputDB.resetPOIData2(configFile,sqlFile,log);
		
		if (!reset){
			log.error("The target OCD could not been reset. Exiting...");
	    	System.out.println("The target OCD could not been reset. Exiting...");
	    	
	    	ocd.setStatus(OCD.STATUS_RUNNING_END_ERR);
			OCD.updateOCDStatus(con, ocd,log);
			
			end =true;
		}
		
	    //Load fusion rules		
		String fusion_path = ocd.getFusionRulesPath();
		System.out.println("Loading fusion rules located at: "+fusion_path);
		log.debug("Loading fusion rules located at: "+fusion_path);
	    FusionRules fusionRules = new FusionRulesParser().getFusionRules(fusion_path);
	    
	    if (fusionRules.GetFusionrules().isEmpty()){
	    	log.error(" No fusion rules have been loaded. Exiting...");
	    	System.out.println("No fusion rules have been loaded. Exiting...");
	    		
			ocd.setStatus(OCD.STATUS_RUNNING_END_ERR);
			OCD.updateOCDStatus(con, ocd,log);
			
			end =true;
			
	    	    	
	    }else{
	    	 //log.info("Starting fusion of OCD "+fusionRules.getCity());		    
	 	     log.debug("Detecting "+fusionRules.GetFusionrules().size()+" fusion_rules for city "+fusionRules.getCity());
	    }
	    log.debug("Fusion rules loaded");
	    System.out.println("Fusion rules loaded");
	    
		
	    
	    
	    
	    if (!end){
	    
	    
	    
		    for (int i=0;i<fusionRules.GetFusionrules().size();i++){
		    	FusionRule fusionRule = fusionRules.GetFusionrules().get(i);
		    	
		    	log.info("Fusion rule "+new Integer(i+1).toString()+" with category "+fusionRule.getCategory());
		    	
		    	
		    	
		    	//Get the location element and iterate through all its sources
		    	ArrayList<String> location = fusionRule.getLocation();
		    	log.debug("Fusion rule "+fusionRule.getCategory()+" has "+location.size()+" location elements");
		    	
		    	
		    	for (int j=0;j<location.size();j++){
		    		
		    		String sourceName = location.get(j).toString();	    		
		    		log.debug("location element "+new Integer(j+1).toString()+" has as sourcename: "+sourceName);
		    		
				    
		    		Source source = Source.getSourceClassByName (con,sourceName,log);			    
				    
				    if (source !=null){			    	
				    	
				    	//Get APIType 
				    	APIType apitype = APIType.getAPITypeClassById(con, source.getAPITypeId(),log);
				    	log.debug(sourceName+" has APIType "+apitype.getName());
				    	if (apitype != null){
				    		
				    		///////////////////////////////
				    		//CitytSDK
				    		//////////////////////////////
				    		if (apitype.getName().equalsIgnoreCase("CitySDK")){
				    			
				    			//Instantiate CitySDK Collector class to start retrieving objects
				    			Collector collector = new Collector(source);
				    			String bbox = fusionRules.getBbox(); 
				    			
				    			
				    			//This method already checks that the returned JSONArray does not include any POI that has been already inserted in the database
				    			JSONArray poiArray = collector.getPOIArrayByCategory(
				    					con, fusionRule.getCategory(),
				    					fusionRules.getCity(),
				    					bbox,fusionRule.getLimit(),log);
				    			
				    			//TODO include this info as statistics
				    			log.info("Getting "+poiArray.length()+" POIs of '"+fusionRule.getCategory()+"' category from "+source.getName());
				        		
				    			
				    			
				        		for(int k = 0; k < poiArray.length(); k++) { 			        			
				        			
				        			FusionResult fusionresult = new FusionResult();
				        			ArrayList<POISource> poisourceArray = new ArrayList<POISource>();
				        			JSONObject poi1;
									try {
											//Get the POIs one by one
											poi1 = poiArray.getJSONObject(k);											
											
											
											//Get latitude and longitude of this POI. 
						        			// FIXME: We assume here that all returned POIS  are georeferenced. Otherwise it will throw an Exception and the loop will end
						    				String lat  = CommonUtils.getLatitude(poi1,log);
						        			String lon  = CommonUtils.getLongitude(poi1,log);				        			
						        			String name = CommonUtils.getName(poi1,log);
						        								        			
						        			
						        			// POI has a name
						        			if(name != null){
						        				
						        				log.debug("POI "+k+" has longitude "+lon+" , latitude "+lat+" and name "+name);
						        				
						        				//The POISource structure needs to be initialized with this poi (poi_id, poiproxyattribute initially are null)				        				
						        				POISource poisource = CommonUtils.getPOISource(con,poi1,log);
						        				
						        				if (source.getName().contains("poiproxy")){
						        					//set the poiproxyattribute as the category
						        					String projectedCategory = getProjectedCategory(source,fusionRules.getCity() ,fusionRule.getCategory());
						        					poisource.setPoiproxyAttribute(projectedCategory); 
						        				}
						        				poisourceArray.add(poisource);
						        				
						        				
						        				//Find in the remaining sources if there are matching POIs 
						        				log.debug("Checking in the remaining sources if we can find matching POIs");
						        				
						        				//j indexes the current source. The remaining sources start from j+1  
						        				for (int p=j+1;p<location.size();p++){
						        		    		
						        		    		String remainingSourceName = location.get(p).toString();
						        		    		log.debug("Checking for matches in "+remainingSourceName);
						        		    		
						        		    		Source source2 = Source.getSourceClassByName(con, remainingSourceName,log);					        		    		
						        		    		Collector collector2 = new Collector(source2);
						        		    		ArrayList<JSONObject> matchingPOIArray = collector2.getZonePOIArrayByCriteria(con, fusionRule.getCategory(), fusionRules.getCity(), lat, lon, 
						        		    				fusionRule.getMaxDistance(), name, fusionRule.getSimilarityPercentage(),log); 
						        		    		
						        		    		
						        		    		if(matchingPOIArray.size() == 0){ 
						        		    			log.info("The POI '"+name+"' has no matches in '"+source2.getName()); 
						        		    			
						        		    		}else if(matchingPOIArray.size() > 1){ 
						        		    			log.info("The POI '"+name+"' has "+matchingPOIArray.size()+" matches in '"+source2.getName()); 
						        		    		
						        		    		}else{
								        				
								        				//One match only. Take the first element
						        		    			JSONObject poi2 = matchingPOIArray.get(0);
								        				
						        		    			log.info("The POI '"+name+"' has 1 match in '"+source2.getName()+"'. Applying fusion...");
						        		    			
						        		    			//Add this poi in the sources list
						        		    			poisource = CommonUtils.getPOISource(con,poi2,log);
						        		    			Source source_aux = Source.getSourceClassById(con, poisource.getSourceId(),log);
								        				if (source_aux.getName().contains("poiproxy")){
								        					//set the poiproxyattribute as the category
								        					String projectedCategory = getProjectedCategory(source2,fusionRules.getCity() ,fusionRule.getCategory() );
								        					poisource.setPoiproxyAttribute(projectedCategory); 
								        				}
								        				poisourceArray.add(poisource);
								        				
						        		    			
						        		    			
						        		    			//We store the fusioned object in poi1.The fusionresult structure allows us to obtain not only the POI, 
						        		    			// but also the name of the source we have used for the 'name' field in the POI table. We'll use also 
						        		    			// the source of the position, but we can take it from the for-statements in the code just
						        		    			//using source.getName()
						        		    			
						        		    			fusionresult = Fusion.fusion(poi1,poi2,fusionRule,log);   
						        		    			poi1 = fusionresult.getFusionedPOI(); 
								        				
								        			}
						        		    		
						        				}
						        				
						        				//After all iterations through the remaining sources, save result POI in DB
						        				//We need to check the source of the 'name' field. This is done during fusion. However, if there is no fusion (no matching)
						        				//then we need to set up the source of the 'name' from the source
						        				String sourceNameOfname = source.getName();
						        				if (fusionresult.getSourceNameFromName() != null) sourceNameOfname = fusionresult.getSourceNameFromName();
						        							        		    			
						        				
				        		    			int poi_id = Fusion.save(con, poi1,fusionRule.getCategory(),sourceNameOfname,source.getName(),poisourceArray,log);
				        		    			
				        		    			if (poi_id!=-1){
				        		    				log.debug("POI inserted with id: "+poi_id+" Inserting additional images from POIProxy...(if configured so)");			        		    				
				        		    				if (fusionRule.getImgActive().equalsIgnoreCase("true")){
				        		    					log.debug("FusionRule indicates to search for "+fusionRule.getImgLimit()+ " images");
				        		    					ArrayList<String> imgSource = fusionRule.getImgSource();
				        		    					if (imgSource.size()== 0) log.error("No sources present to search for images");
				        		    					int img_index = 0;
				        		    					for (int p=0;p<imgSource.size();p++){
				        		    						log.debug("Looking for images in "+imgSource.get(p));
				        		    						//This method already checks that the returned JSONArray does not include any POI that has been already inserted in the database
				        		    						Source poiproxySource = Source.getSourceClassByName(con, "poiproxy",log);	
				        		    						Collector collector_img = new Collector(poiproxySource);
				        		    						//Here the bbox refers to the location of the point and the radius
				        		    						Double lon_d = POI.getPOIClassById(con, poi_id,log).getLongitude();
				        		    						Double lat_d = POI.getPOIClassById(con, poi_id,log).getLatitude();
				        		    						
				        		    						String longitude = lon_d.toString();			        		    						
				        		    						String latitude = lat_d.toString();
				        		    						String bbox_img=latitude+","+longitude+","+fusionRule.getMaxDistance();
				        		    						
															JSONArray poiArrayImages = collector_img.getImgPOIArrayByCategory(con, imgSource.get(p),fusionRules.getCity(),bbox_img,new Integer(fusionRule.getImgLimit()),log);
															log.debug("Found "+poiArrayImages.length()+" images in "+imgSource.get(p));
															
															for (int q=0;q<poiArrayImages.length();q++){
																//Get poi
																POILabel poilabel = collector_img.getImgPOILabel(con, poi_id,poiArrayImages,q,log);
																
																//save poi
																if (img_index ==new Integer(fusionRule.getImgLimit()) ) break;
																if (poilabel !=null){
																	POILabel.savePOILabel(con, poilabel,log); 
																	img_index++;
																}
																
															}
															
															if (img_index ==new Integer(fusionRule.getImgLimit()) ) break;
				        		    					}
				        		    					
				        		    				}else{
				        		    					log.debug("FusionRule indicates not to include any images");
				        		    				}
				        		    				
				        		    				
				        		    				
				        		    			}else{
				        		    				log.error("There was a problem saving the POI");
				        		    			}
						        				
						        				
						        			
				        		    			
				        		    			
						        				
						        			}
									} catch (JSONException e) {
											log.error(e.getMessage());
									}
				        			
				        		}	  		
				    			
				    		}
				    		if (apitype.getName().equalsIgnoreCase("FI-PP POI API")){
				    			//TODO
				    		}
				    	}
				    	
				    }else{
				    	log.error("Could not find a source in the database corresponding with "+sourceName);
				    }		    
				   
		    	}	
		    	
		    }
		    ocd.setStatus(OCD.STATUS_RUNNING_END_OK);
		    OCD.updateOCDStatus(con, ocd, log);
		    OutputDB.disconnectDB(con,log);
		    
		}  
	    
	    
	    
			
		
	}
	
	private static String getProjectedCategory(Source source, String city, String category){
	
		String projectedCategory = null;
		String mappingfile = source.getCategorymapping();
		
		/*
		//In the case of POIProxy, as special source, we should map to local content depending on the city
		String sourceName = source.getName();
		if (sourceName.toLowerCase().contains("poiproxy")){
			//we need to change the properties file name to match the local data
			mappingfile = mappingfile.substring(0,mappingfile.lastIndexOf("/"));
			mappingfile = mappingfile+"/poiproxy_local_"+city+".properties";
			
		}
		*/
		try{
			FileInputStream input = new FileInputStream(mappingfile);
		
			Properties prop =new Properties();
			// load properties file
			prop.load(input);
			
			// get the corresponding category in the source
			projectedCategory = prop.getProperty(category);
		}catch(Exception ex){
			log.error("Error in getProjectedCategory: "+ex.getMessage());
			System.out.println("Error in getProjectedCategory: "+ex.getMessage());
		}
		
		return projectedCategory;
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
	    executer.setOutput(new File("/home/bmolina/fusion/out_sql.txt"));
	    executer.setDelimiter(scr_sep);
	    executer.setSrc(sqlFile);
	    executer.setDriver(driver);
	    executer.setPassword(pwd);
	    executer.setUserid(user);
	    executer.setUrl(url);
	    executer.execute();
	    
	}

	
}
