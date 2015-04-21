package org.upv.satrd.fic2.fe.fusionrules;


import java.util.ArrayList;

/*
 * This class wraps a fusion rule  XML file such as 
 * 
  <fusion_rules city="tenerife" bbox="28.402909,-16.438604,28.564655,-16.161199" fe_version="1.0">
  	<fusion_rule category="museum" limit="50">
    </fusion_rule>
    
    <fusion_rule category="monument" limit="20">
    </fusion_rule>
 * 
 * 
 * 
 * 
 */


public class FusionRules {
	
	private String city;	//name of the city or region to find POIs
	private String bbox;	//bbox is supposed to be [Ymin,Xmin,Ymax,Xmax]
	private ArrayList<FusionRule> fusionrules = new ArrayList<FusionRule>();
	
	public FusionRules(){}
	
	
	public void addFusionRule(FusionRule fusionrule){ fusionrules.add(fusionrule);}
	
	//GET methods
	public String getCity(){return city;}
	public String getBbox(){return bbox;}
	public ArrayList<FusionRule> GetFusionrules(){ return fusionrules;}
	
	
	//SET METHODS
	public void setCity(String city){this.city = city;}
	public void setBbox(String bbox){this.bbox = bbox;}
	public void setFusionRules(ArrayList<FusionRule> fusionrules){this.fusionrules = fusionrules;}
	
	
}
