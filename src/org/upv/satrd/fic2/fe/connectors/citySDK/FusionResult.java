package org.upv.satrd.fic2.fe.connectors.citySDK;

import org.json.JSONObject;


//This class stored the fusioned POI as a JSON object. It also includes the source that has been used to set the name
public class FusionResult {
	
	JSONObject fusioned_poi;
	String name_source;
	
	//TODO. This object may include statistic information
	
	
	public FusionResult(){
		this.fusioned_poi = null;
		this.name_source = null;		
	}
	
	public FusionResult(JSONObject fusioned_poi, String name_source){
		this.fusioned_poi = fusioned_poi;
		this.name_source = name_source;		
	}
	
	
	//GET methods
	public JSONObject getFusionedPOI(){return this.fusioned_poi;}
	public String getSourceNameFromName(){return this.name_source;}
	
	
	//SET methods	
	public void setFusionedPOI(JSONObject poi){ this.fusioned_poi = poi;}
	public void setSourceNameFromName(String source){this.name_source = source;}
	
		
	

}
