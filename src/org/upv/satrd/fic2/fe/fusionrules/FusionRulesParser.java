package org.upv.satrd.fic2.fe.fusionrules;


import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;



public class FusionRulesParser {
	
	private static org.apache.log4j.Logger log =Logger.getLogger(org.upv.satrd.fic2.fe.fusionrules.FusionRulesParser.class);
		
	public FusionRules getFusionRules(String path){
			
		
		FusionRules fusionRules = new FusionRules();
		
		ArrayList<FusionRule> fusionrules = new ArrayList<FusionRule>();
		 
		
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(path);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			
			
			//Get the city
			XPathExpression expr = xpath.compile("//fusion_rules/@city");
			String city = (String) expr.evaluate(doc, XPathConstants.STRING);
			fusionRules.setCity(city);
			//System.out.println(city);
			
			//Get bbox
			expr = xpath.compile("//fusion_rules/@bbox");
			String bbox = (String) expr.evaluate(doc, XPathConstants.STRING);
			fusionRules.setBbox(bbox);
			
			
			//Get all categories. This is the same as the number of different fusion_rule items. It is supposed that categories are not repeated
			expr = xpath.compile("//@category");			
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);		
			
			//It is supposed that each <fusion_rule> element has one attribute 'category' and one attribute 'limit', always. Otherwise, nl_limit(i) may overflow
			XPathExpression expr_limit = xpath.compile("//@limit");
			NodeList nl_limit = (NodeList) expr_limit.evaluate(doc, XPathConstants.NODESET);	
			
			
			for (int i=0;i<nl.getLength();i++){
				
				FusionRule frule = new FusionRule();
				
				String category = nl.item(i).getTextContent();
				frule.setCategory(category);
				
				String limit = nl_limit.item(i).getTextContent();
				frule.setLimit(new Integer(limit));
				
				//Get the fusion_rule subelements for each category
				
				//Get the location subelements	
				expr = xpath.compile("//fusion_rule[@category='"+category+"']/general_settings/location/source"); 
				NodeList nl2 = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);				
				
				for (int j=0;j<nl2.getLength();j++){
					String source = nl2.item(j).getTextContent();
					frule.addNewLocation(source);
					
				}
				
				//Get the name subelements	
				expr = xpath.compile("//fusion_rule[@category='"+category+"']/general_settings/name/source"); 
				nl2 = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);			
				
				
				for (int j=0;j<nl2.getLength();j++){
					String source = nl2.item(j).getTextContent();
					frule.addNewName(source);
					
				}
				
				//Get the description subelements	
				expr = xpath.compile("//fusion_rule[@category='"+category+"']/general_settings/description/source"); 
				nl2 = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);			
				
				
				for (int j=0;j<nl2.getLength();j++){
					String source = nl2.item(j).getTextContent();
					frule.addNewDescription(source);
					
				}
				
				//Get the image subelements	
				expr = xpath.compile("//fusion_rule[@category='"+category+"']/general_settings/image/source"); 
				nl2 = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);			
				
				
				for (int j=0;j<nl2.getLength();j++){
					String source = nl2.item(j).getTextContent();
					frule.addNewImgSource(source);
					
				}
				expr = xpath.compile("//fusion_rule[@category='"+category+"']/general_settings/image/active[1]/text()"); 
				String imgActive =  (String) expr.evaluate(doc, XPathConstants.STRING);				
				frule.setImgActive(imgActive);
				
				expr = xpath.compile("//fusion_rule[@category='"+category+"']/general_settings/image/limit[1]/text()"); 
				String imgLimit = (String) expr.evaluate(doc, XPathConstants.STRING);				
				frule.setImgLimit(imgLimit);
				
				
				//Get the specific settings
				
				expr = xpath.compile("//fusion_rule[@category='"+category+"']/specific_settings/max_distance[1]/text()"); 
				String max_distance =  (String) expr.evaluate(doc, XPathConstants.STRING);				
				frule.setMaxDistance(max_distance);
				//System.out.println("max_distance: "+max_distance);
				
				expr = xpath.compile("//fusion_rule[@category='"+category+"']/specific_settings/similarity_percentage[1]/text()"); 
				String similarity = (String) expr.evaluate(doc, XPathConstants.STRING);				
				frule.setSimilarityPercentage(similarity);
				//System.out.println(similarity);
				
				//Add the fusion_rule item to the list
				fusionrules.add(i, frule);
				
			}
			fusionRules.setFusionRules(fusionrules);
			
			
			
		}catch(Exception ex){
			System.out.println("Error FusionRulesParser.getFusionRules(): "+ex.getMessage());
			log.error("Error FusionRulesParser.getFusionRules(): "+ex.getMessage());
		}
		
		
		return fusionRules;
		
	}
	
	
	
}
