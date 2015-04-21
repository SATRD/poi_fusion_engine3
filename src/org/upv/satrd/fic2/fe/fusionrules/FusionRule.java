package org.upv.satrd.fic2.fe.fusionrules;

import java.util.ArrayList;

/*
 * This class wraps a fusion rule such as 
 * 
  <fusion_rule category="museum" limit="50">
		<general_settings>
			<location>
				<source>poiproxy_local_valencia</source>				
				<source>dbpedia</source>
			</location>
			<name>
				<source>poiproxy_local_valencia</source>					
				<source>dbpedia</source>
			</name>
			<description>
				<source>dbpedia</source>
				<source>poiproxy_local_valencia</source>					
			</description>
			<image>
				<active>true</active>
				<limit>10</limit>
				<source>flickr</source>							
			</image>				  
		</general_settings>
		<specific_settings>			
			<max_distance>50</max_distance>			
			<similarity_percentage>70</similarity_percentage>
		</specific_settings>
	</fusion_rule>
 * 
 * 
 * 
 * 
 */

public class FusionRule {
	
	private String category;											//category to search for in the original data sources
	
	private Integer limit;												//limits the number of POIs to be extracted
	
	private ArrayList<String> location = new ArrayList<String>();		//list of sources to priorize location
	private ArrayList<String> name = new ArrayList<String>();			//list of sources to priorize name
	private ArrayList<String> description = new ArrayList<String>();	//list of sources to priorize description
	
	private String imgActive;											//indicates if images/photos are going to be extractd or not
	private String imgLimit;
	private ArrayList<String> imgSource = new ArrayList<String>();		//list of sources to priorize images/photos
	
	
	private String maxDistance;											//radius (in m) to find matches between two POIs
	private String similarityPercentage;								//criteriun to decide if two (POI)names are the same or not
	
	
	public FusionRule() {	}
	   
	    
	
	//Methods to add new Elements at the end of the ArrayList
	public void addNewLocation(String location_source){ this.location.add(location_source); }
	public void addNewName(String name_source){this.name.add(name_source);}
	public void addNewDescription(String description_source){this.description.add(description_source);}
	public void addNewImgSource(String img_source){this.imgSource.add(img_source);}
	
	
	
	//SET methods
	public void setCategory(String category){this.category = category;}
	public void setLimit(Integer limit){this.limit = limit;}
	public void setLocation(ArrayList<String> location){this.location = location;} 
	public void setName(ArrayList<String> name){this.name = name;}
	public void setDescription(ArrayList<String> description){this.description = description;}
	public void setImgActive(String imgActive) {this.imgActive = imgActive;}
	public void setImgLimit(String imgLimit){this.imgLimit = imgLimit;}
	public void setImgSource(ArrayList<String> imgSource){this.imgSource = imgSource;}
	public void setMaxDistance(String maxDistance){this.maxDistance = maxDistance;}
	public void setSimilarityPercentage(String similarityPercentage){this.similarityPercentage = similarityPercentage;}
	
	
	
	//GET methods
	public String getCategory() { return this.category;} 
	public Integer getLimit() { return this.limit;} 
	
	public ArrayList<String> getLocation() {return  location;}
	public ArrayList<String> getName() {return  name;}
	public ArrayList<String> getDescription() {return  description;}
	
	public String getImgActive() {return  imgActive;}
	public String getImgLimit() {return  imgLimit;}
	public ArrayList<String> getImgSource() {return  imgSource;}
	
	public String getMaxDistance() {return  maxDistance;}
	public String getSimilarityPercentage() {return  similarityPercentage;}
	
}
