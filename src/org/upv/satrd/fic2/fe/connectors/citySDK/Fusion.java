package org.upv.satrd.fic2.fe.connectors.citySDK;


import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.upv.satrd.fic2.fe.db.Category;
import org.upv.satrd.fic2.fe.db.LabelType;
import org.upv.satrd.fic2.fe.db.License;
import org.upv.satrd.fic2.fe.db.POI;
import org.upv.satrd.fic2.fe.db.POICategory;
import org.upv.satrd.fic2.fe.db.POILabel;
import org.upv.satrd.fic2.fe.db.Source;
import org.upv.satrd.fic2.fe.db.POISource;
import org.upv.satrd.fic2.fe.fusionrules.FusionRule;

public class Fusion {
	
	private static org.apache.log4j.Logger log =Logger.getLogger(org.upv.satrd.fic2.fe.connectors.citySDK.Fusion.class);
	
	
	
	public Fusion(){ }
	
	
	public static FusionResult fusion(JSONObject poi1, JSONObject poi2, FusionRule fr, Logger log){
				
		FusionResult fusionresult = null;
		String name_source = "";
		if (log == null) log = Fusion.log;
		
		
		try{
			
			
			int namePos1 = CommonUtils.getPropertyPos(poi1,"name", log);
			String source1 = poi1.getJSONArray("label").getJSONObject(namePos1).getString("base");
			int namePos2 = CommonUtils.getPropertyPos(poi2,"name", log);
			String source2 =  poi2.getJSONArray("label").getJSONObject(namePos2).getString("base");
			
			//Fusion label tag
			JSONArray object1 = poi1.getJSONArray("label");
			JSONArray object2 = poi2.getJSONArray("label");
			
			JSONArray object3 = new JSONArray();
			int i = 0;
			
			
			//TODO we are not combining the location.address element of both POIs. Now we just take the first one (the one that conditions position)   
			
					
			//////////////////////
			//Add name. We take the one that first appears in the name ArrayList
			//////////////////////
			for(int j=0; j<fr.getName().size(); j++){
				if(fr.getName().get(j).equals(source1)){
					int namePos = CommonUtils.getPropertyPos(poi1,"name",log);
					object3.put(i,poi1.getJSONArray("label").getJSONObject(namePos));
					i++;
					log.info("Fusion.fusion(). Fusioning. Inserting name from source: "+source1);
					name_source = source1;
					break;				
				}
				if(fr.getName().get(j).equals(source2)){
					int namePos = CommonUtils.getPropertyPos(poi2,"name",log);
					object3.put(i,poi2.getJSONArray("label").getJSONObject(namePos));
					log.info("Fusion.fusion(). Fusioning. Inserting name from source: "+source2);
					name_source = source2;
					i++;
					break;					
				}
			}
			
			
			////////////////////
			//Add other labels
			////////////////////
			ArrayList<String> allObjects = new ArrayList<String>();
			
			for(int j=0; j<object1.length(); j++){							
				//If is not name
				if(!poi1.getJSONArray("label").getJSONObject(j).get("term").equals("name")){
					String value =  (String)poi1.getJSONArray("label").getJSONObject(j).get("value");
					//If is not repeated
					if(!allObjects.contains(value)){
						object3.put(i,poi1.getJSONArray("label").getJSONObject(j));
						allObjects.add(value);
						i++;
					}
				}	
					
			}			
			for(int j=0; j<object2.length(); j++){
				//If is not name
				if(!poi2.getJSONArray("label").getJSONObject(j).get("term").equals("name")){
					String value =  (String)poi2.getJSONArray("label").getJSONObject(j).get("value");
					//If is not repeated
					if(!allObjects.contains(value)){
						object3.put(i,poi2.getJSONArray("label").getJSONObject(j));
						allObjects.add(value);
						i++;
					}
				}
			}
			
			//We'll store the final POI in poi1. We preserve the structure and override step by step. Up to now only label
			poi1.put("label", object3);
			
			
			
			///////////////////////////////////
			//Fusion description tag. It is possible that this tag does not appear in some POIs, so we must be careful 
			//////////////////////////////////
			try{
				object1 = poi1.getJSONArray("description");
			}catch (JSONException e){
				object1 = null;
				log.warn("Fusion.fusion(). Fusioning. POI object from source "+source1+" does not have description. We'll take the description from the other POI independently of the FusionRule" );
			}
			try{
				object2 = poi2.getJSONArray("description");
			}catch (JSONException e){
				object2 = null;
				log.warn("Fusion.fusion(). Fusioning. POI object from source "+source2+" does not have description. We'll take the description from the other POI independently of the FusionRule" );
			}
			
			
			object3 = new JSONArray();
			i = 0;
			
			if (object1 == null){
				object3.put(i,poi2.getJSONArray("description").getJSONObject(i));
			}else{
				if (object2 == null){
					object3.put(i,poi1.getJSONArray("description").getJSONObject(i));
				}else{
					
					
					for(int j=0; j<fr.getDescription().size(); j++){
						if(fr.getDescription().get(j).equals(source1)){
							if(poi1.getJSONArray("description").length() > 0){
								object3.put(i,poi1.getJSONArray("description").getJSONObject(i));
								log.info("Fusion.fusion(). Fusioning. Inserting description from source: "+source1);
								break;
							}
						}
						if(fr.getDescription().get(j).equals(source2)){
							if(poi2.getJSONArray("description").length() > 0){
								object3.put(i,poi2.getJSONArray("description").getJSONObject(i));
								log.info("Fusion.fusion().Fusioning. Inserting description from source: "+source2);
								break;
							}
						}
					}
				}
			}
			
			//Override description field
			poi1.put("description", object3);
			
			
			
			//////////////////////////
			//Fusion category tag
			/////////////////////////
			try{
				object1 = poi1.getJSONArray("category");
			}catch (JSONException e){
				object1 = null;
				log.warn("Fusion.fusion(). Fusioning. POI object from source "+source1+" does not have category." );
			}
			try{ 
				object2 = poi2.getJSONArray("category");
			}catch (JSONException e){
				object1 = null;
				log.warn("Fusion.fusion(). Fusioning. POI object from source "+source2+" does not have category." );
			}
			
			allObjects = new ArrayList<String>();		//We don't need it as we will add all categories, we'll not check if they are repeated	
			object3 = new JSONArray();
			i = 0;
			
			if (object1 == null){
				object3.put(i,poi2.getJSONArray("category").getJSONObject(i));
			}else{
				if (object2 == null){
					object3.put(i,poi1.getJSONArray("category").getJSONObject(i));
				}else{
			
					for(int j=0; j<object1.length(); j++){							
						String value =  (String)object1.getJSONObject(j).get("value");
			
						object3.put(i,object1.getJSONObject(j));
						allObjects.add(value);
						i++;								
					}
					for(int j=0; j<object2.length(); j++){							
						String value =  (String)object2.getJSONObject(j).get("value");
						
						object3.put(i,object2.getJSONObject(j));
						allObjects.add(value);
						i++;							
					}
					
					
				}
			}
			
			//Override category
			poi1.put("category", object3);
		
			
			///////////////////////////
			//Fusion link tag
			///////////////////////////
			try{
				object1 = poi1.getJSONArray("link");
			}catch (JSONException e){
				object1 = null;
				log.warn("Fusion.fusion().Fusioning. POI object from source "+source1+" does not have link." );
			}
			try{ 
				object2 = poi2.getJSONArray("link");
				
			}catch (JSONException e){
				object2 = null;
				log.warn("Fusion.fusion().Fusioning. POI object from source "+source2+" does not have link." );
			}
			
			allObjects = new ArrayList<String>();
			
			object3 = new JSONArray();
			i = 0;

			if (object1 !=null){
				for(int j=0; j<object1.length(); j++){							
					String value =  (String)object1.getJSONObject(j).get("value");
					//If is not repeated
					if(!allObjects.contains(value)){	
						object3.put(i,object1.getJSONObject(j));
						allObjects.add(value);
						i++;
					}								
				}
			}
			
			if (object2 != null){
				for(int j=0; j<object2.length(); j++){							
					String value =  (String)object2.getJSONObject(j).get("value");
					//If is not repeated
					if(!allObjects.contains(value)){	
						object3.put(i,object2.getJSONObject(j));
						allObjects.add(value);
						i++;
					}								
				}
			}
			
			//Override link
			poi1.put("link", object3);

			
			//Finally we should override the publisher part to say 'Fusion Engine'. Anyway we can set it up during the storage of the POI in the database
			
			//Create the FusionResult return object
			fusionresult = new FusionResult(poi1, name_source); 
			
		} catch ( Exception e) {
			System.out.println("Error.Fusion.fusion(): "+e.getMessage());
			log.error("Error.Fusion.fusion(): "+e.getMessage());
		}
		
		return fusionresult;		
		
	}
	
	
	public static int save(Connection con, JSONObject poi, String category, String name_sourceName, String position_sourceName, 
			ArrayList<POISource> poisourceArray, Logger log ){
				
		Integer poiid = -1;	
		if (log == null) log = Fusion.log;
		
		try{
			//Generate the POI object
			
			Double lat = new Double(CommonUtils.getLatitude(poi,log));			
			Double lon = new Double(CommonUtils.getLongitude(poi,log));				
			String name = CommonUtils.getName(poi,log);			
			Date date = CommonUtils.getFormatedDateAsDate();
			
			POI poi_object = new POI(name,lat,lon,date);  		
			
			
			poiid= POI.savePOI(con, poi_object,log);
			
						
			
			if (poiid!=null){    			
			    
				//This is just visual output of pois being inserted. You may comment/remove these two lines
			    System.out.println("poiid: "+poiid);
			    System.out.println(poi.toString());		    
			    
			    JSONArray object;
			    JSONObject aux;
			    String type, value, source, language, license;
			    Date updated = null;   
			    Integer licenseId = null;
			    Integer typeId = null;
			    Integer sourceId = null;
			    
			   		   
			    
			    //Insert category in POICategory table. We need to get previously the category_id				    
			    Category cat = Category.getCategoryClassByName(con,category,log);	
			    POICategory poicategory = new POICategory(poiid,cat.getId());
			    POICategory.savePOICategory(con, poicategory,log);
			    
			    
			    
			    //We need to fill the POISource table
			    for (int p=0;p<poisourceArray.size();p++){
			    	
			    	org.upv.satrd.fic2.fe.db.POISource poisource = poisourceArray.get(p);
			    	poisource.setPOiId(poiid);
			    	org.upv.satrd.fic2.fe.db.POISource.savePOISource(con, poisource,log);				
			    }
			    
			   
			   
			    //Insert the address value
			    String  address= CommonUtils.getAddress(poi,log);
			    if (address !=null){		
								
					type = "address"; 
					
					LabelType labeltype = LabelType.getLabelTypeClassByName(con, type,log);
					
					if (labeltype ==null){
						//the ' address' LabelType has not yet been inserted (probably it should during initialization). Create it
						labeltype = new LabelType(type);
						typeId = LabelType.saveLabelType(con, labeltype,log);					
					}else{
						typeId = labeltype.getId();
					}	
										
					//value
					value = address;					
					
					
					//'source' is the one we have used for the position
					sourceId = Source.getSourceClassByName(con, position_sourceName,log).getId();			
					
					language = "";
						
					license = "";				
					
					updated = CommonUtils.getFormatedDateAsDate();
				    
					POILabel poilabel = new POILabel(poiid,typeId, value, sourceId, language, licenseId, updated);
					POILabel.savePOILabel(con, poilabel,log);
								
				}
			    
			    
			    
				//Insert description in the POILabel table
			    try{
			    	object = poi.getJSONArray("description");
			    }catch (JSONException e){
					object = null;					
					log.debug("Fusion.save(). Saving POI. The POI does not have any description. No label to insert in the POILabel table" );
				}
			    
			   
			    
				if (object !=null){		
						
						aux =  object.getJSONObject(0);	
						
						
						//'term' should always be there. It should say 'description'
						type = (String)aux.get("term"); 
						
						LabelType labeltype = LabelType.getLabelTypeClassByName(con, type,log);
						
						if (labeltype ==null){
							//the ' description' LabelType has not yet been inserted (probably it should during initialization). Create it
							labeltype = new LabelType(type);
							typeId = LabelType.saveLabelType(con, labeltype,log);					
						}else{
							typeId = labeltype.getId();
						}	
						
						
						
						//'value' should always be there. However some POIS do not have a value field in the description object,
						// so we need to catch this potential Exception 
						try{
							value = (String)aux.get("value");
						}catch (JSONException e){
							value = " ";
							log.debug("Fusion.save().Saving POI. The POI has description, but no value field. It will be empty" );
						}
						
						
						
						//'source' should always be there
						source = (String)aux.get("base");
						sourceId = Source.getSourceClassByName(con, source,log).getId();
						
						
						try{
							language = (String)aux.get("lang"); 
					    }catch (JSONException e){
							language = "";
							//log.debug("Saving POI. The 'description' field does not have a 'lang' subfield" );
						}
						
						
						try{
							license = (String)aux.get("license");
							licenseId = License.getLicenseClassByName(con, license,log).getId();
					    }catch (JSONException e){
					    	license = "";
							//log.debug("Saving POI. The 'description' field does not have a 'license' subfield" );
						}		
						
						
						
						// FIXME: we should use updated = (String)aux.get("updated");
						updated = CommonUtils.getFormatedDateAsDate();
					    
						POILabel poilabel = new POILabel(poiid,typeId, value, sourceId, language, licenseId, updated);
						POILabel.savePOILabel(con, poilabel,log);
						
						
					
				}
				
				
				
				
				//Generate poilabel for 'name' and 'position' in the POI table. So we can know from which source 'name' and 'position' comes from.
				//In these cases the 'value' field does not make sense, so we insert "-1" instead of null 
				//name
				LabelType labeltype = LabelType.getLabelTypeClassByName(con, "name",log);
				Integer name_labeltypeid;
				if (labeltype ==null){
					//the ' name' LabelType has not yet been inserted (probably it should during initialization). Create it
					labeltype = new LabelType("name");
					name_labeltypeid = LabelType.saveLabelType(con, labeltype,log);					
				}else{
					name_labeltypeid = labeltype.getId();
				}
				sourceId = Source.getSourceClassByName(con, name_sourceName,log).getId();
				POILabel poilabel_aux = new POILabel(poiid, name_labeltypeid, name_sourceName, sourceId, null, null, updated);
				POILabel.savePOILabel(con,poilabel_aux,log);
				//position. Here we do not need to save the position (analog as with name)
				labeltype = LabelType.getLabelTypeClassByName(con, "position",log);
				Integer position_labeltypeid;
				if (labeltype ==null){
					//the ' position' LabelType has not yet been inserted (probably it should during initialization). Create it
					labeltype = new LabelType("position");
					position_labeltypeid = LabelType.saveLabelType(con, labeltype,log);					
				}else{
					position_labeltypeid = labeltype.getId();
				}				
				sourceId = Source.getSourceClassByName(con, position_sourceName,log).getId();
				poilabel_aux = new POILabel(poiid, position_labeltypeid, position_sourceName, sourceId, null, null, updated);				
				POILabel.savePOILabel(con,poilabel_aux,log);
				    
					    
				
				
				
				//Insert link in the POILabel table. The link field has as term values: web, url					
			    try{
			    	object = poi.getJSONArray("link");
			    }catch (JSONException e){
					object = null;
					log.debug("Fusion.save(). Saving POI. Fusion object does not have any link. No label to insert in the POILabel table" );
				}
				if (object !=null){	
					
					for(int i=0; i<object.length(); i++){							
							aux =  object.getJSONObject(i);	
							
							//'term' should always be there
							//type = (String)aux.get("term"); We do not use this as we may find 'web' in poiproxy and 'url' in dbpedia
							type = "link";
							labeltype = LabelType.getLabelTypeClassByName(con, type,log);
							if (labeltype == null){
								labeltype = new LabelType(type);
								typeId = LabelType.saveLabelType(con, labeltype,log);
							}else{
								typeId = LabelType.getLabelTypeClassByName(con, type,log).getId();
							}
							
							//'value' should always be there
							value = (String)aux.get("value");
							
							//'source' should always be there
							source = (String)aux.get("base");
							sourceId = Source.getSourceClassByName(con, source,log).getId();
							
							
							try{
								language = (String)aux.get("lang"); 
						    }catch (JSONException e){
								language = "";
								//og.debug("Saving POI. The 'link' field does not have a 'lang' subfield" );
							}
							
							try{
								license = (String)aux.get("license");
								licenseId = License.getLicenseClassByName(con, license,log).getId();
						    }catch (JSONException e){
						    	licenseId = null;
								//log.debug("Saving POI. The 'link' field does not have a 'license' subfield" );
							}				
							
							
							// FIXME: we should use updated = (String)aux.get("updated");
							updated = CommonUtils.getFormatedDateAsDate();							
							
							
							POILabel poilabel = new POILabel(poiid,typeId, value, sourceId, language, licenseId, updated);
							POILabel.savePOILabel(con, poilabel,log);	
							
					}					
				}	   	
				
				
			    
				
				//Insert all additional labels in the POILabel table. 				
			    try{
			    	object = poi.getJSONArray("label");
			    }catch (JSONException e){
					object = null;
					log.debug("Fusion.save(). Saving POI. Fusion object does not have any labels. No label to insert in the POILabel table" );
				}
				if (object !=null){							
					for(int i=0; i<object.length(); i++){
							
							aux =  object.getJSONObject(i);	
							
							//'term' should always be there
							type = (String)aux.get("term"); 
							labeltype = LabelType.getLabelTypeClassByName(con, type,log);
							if (labeltype == null){
								labeltype = new LabelType(type);
								typeId = LabelType.saveLabelType(con, labeltype,log);
							}else{
								typeId = LabelType.getLabelTypeClassByName(con, type,log).getId();
							}
							
							
							//Skip id term. It will be translated in the POISource table
							if (!type.equalsIgnoreCase("id")){
							
								
								//'value' should always be there. However some POIS do not have a value field in the description object,
								// so we need to catch this potential Exception 
								try{
									value = (String)aux.get("value");
								}catch (JSONException e){
									value = " ";
									log.debug("Fusion.save(). Saving POI. The POI has a label, but no value field. It will be empty" );
								}								
								
								//'source' should always be there
								source = (String)aux.get("base");
								sourceId = Source.getSourceClassByName(con, source,log).getId();
								
								
								try{
									language = (String)aux.get("lang"); 
							    }catch (JSONException e){
									language = "";
									//log.debug("Saving POI. This 'label' field does not have a 'lang' subfield" );
								}
								
								try{
									license = (String)aux.get("license");									
									License license_aux = License.getLicenseClassByName(con, license,log);
									if (license_aux == null) licenseId = null;
									else licenseId = license_aux.getId();																		
									
							    }catch (Exception e){
							    	licenseId = null;
									//log.debug("Saving POI. This 'label' field does not have a 'license' subfield" );							    	
								}				
								
								
								// FIXME: we should use updated = (String)aux.get("updated");
								updated = CommonUtils.getFormatedDateAsDate();
								
								
								POILabel poilabel = new POILabel(poiid,typeId, value, sourceId, language, licenseId, updated);
								POILabel.savePOILabel(con, poilabel,log);	
								
								
							}
					}
				}	
				
			    
			}	        

		} catch ( Exception e) {
			System.out.println("Error.Fusion. save(): "+e.getMessage());
			log.error("Error.Fusion.save(): "+e.getMessage());
		}
		return poiid;
	}
	
	

}
