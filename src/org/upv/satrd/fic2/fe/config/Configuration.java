package org.upv.satrd.fic2.fe.config;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Configuration {

	private String dbPlatform;
	private String dbDriverName;
	private String dbUser;		
	private String dbPwd;
	
	private String dbHost;
	private String dbPort;
	private String dbName;
	
	
	private String dbResetBaseScript;
	private String dbResetOCDScript;
	private String dbEnable2DScript;
	private String dbScriptSeparator = ";";
	
	
	private String geometryFromLonLatSrid;
	private String lonFromPoint;
	private String latFromPoint;
		
	
	private String mappingDir;
	private String fusionDir;
	private String apiRulesDir;
	private String dbScriptsDir;
	private String logDir;
	
	private String firstTime;
	
	
	private static org.apache.log4j.Logger log;
	
	public Configuration(String path) {  
		
	    Element fstElmnt, fstElmnt2 ; 
	    NodeList fstNmElmntLst, fstNmElmntLst2; 	   
	    
	    log = Logger.getLogger(org.upv.satrd.fic2.fe.config.Configuration.class);
	    
	    
		
		//Read the XML configuration file
		try{
			File file = new File(path);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("configuration");
		
			for (int s = 0; s < nodeLst.getLength(); s++) {
				Node fstNode = nodeLst.item(s);
							    
			    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
			    	fstElmnt = (Element) fstNode;
			    	 
			    	NodeList nodeLst2 = fstElmnt.getElementsByTagName("DB"); 
				    for (int j = 0; j < nodeLst2.getLength(); j++) {
				    	Node fstNode2 = nodeLst2.item(j);				    
				    	if (fstNode2.getNodeType() == Node.ELEMENT_NODE) {
				    		fstElmnt2 = (Element) fstNode2;	      
				    		
			    			
			    			fstNmElmntLst2 = fstElmnt2.getElementsByTagName("platform");
			    			dbPlatform = (fstNmElmntLst2.item(0)).getTextContent();

			    			fstNmElmntLst2 = fstElmnt2.getElementsByTagName("driverName");
			    			dbDriverName = (fstNmElmntLst2.item(0)).getTextContent();

			    			fstNmElmntLst2 = fstElmnt2.getElementsByTagName("user");
						    dbUser = (fstNmElmntLst2.item(0)).getTextContent();
		    				
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("pwd");
						    dbPwd = (fstNmElmntLst2.item(0)).getTextContent();
						    
						    			    			
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("host");
						    dbHost = (fstNmElmntLst2.item(0)).getTextContent();
			    			
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("port");
						    dbPort = (fstNmElmntLst2.item(0)).getTextContent();		    				
		    				
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("name");
						    dbName = (fstNmElmntLst2.item(0)).getTextContent();
						    
						    

		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("resetBaseScript");
		    				dbResetBaseScript = (fstNmElmntLst2.item(0)).getTextContent();
		    				
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("resetOCDScript");
		    				dbResetOCDScript = (fstNmElmntLst2.item(0)).getTextContent();
		    				
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("resetBaseScript");
		    				dbResetBaseScript = (fstNmElmntLst2.item(0)).getTextContent();		    				
		    				
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("enable2DScript");
		    				dbEnable2DScript = (fstNmElmntLst2.item(0)).getTextContent();
		    				
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("scriptSeparator");
		    				dbScriptSeparator = (fstNmElmntLst2.item(0)).getTextContent();
		    				
		    				
		    				
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("geometryFromLonLatSrid");
		    				geometryFromLonLatSrid = (fstNmElmntLst2.item(0)).getTextContent();
		    				
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("lonFromPoint");
		    				lonFromPoint = (fstNmElmntLst2.item(0)).getTextContent();
		    				
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("latFromPoint");
		    				latFromPoint = (fstNmElmntLst2.item(0)).getTextContent();

				    	} //if
				    	  
				    }//for   				    	
			    	
					
					
					
					fstNmElmntLst = fstElmnt.getElementsByTagName("mappingDir");
					mappingDir = (fstNmElmntLst.item(0)).getTextContent();
					
					fstNmElmntLst = fstElmnt.getElementsByTagName("fusionDir");
					fusionDir = (fstNmElmntLst.item(0)).getTextContent();
					
					fstNmElmntLst = fstElmnt.getElementsByTagName("apiRulesDir");
					apiRulesDir = (fstNmElmntLst.item(0)).getTextContent();
					
					fstNmElmntLst = fstElmnt.getElementsByTagName("dbScriptsDir");
					dbScriptsDir = (fstNmElmntLst.item(0)).getTextContent();
					
					fstNmElmntLst = fstElmnt.getElementsByTagName("logDir");
					logDir = (fstNmElmntLst.item(0)).getTextContent();
					
					fstNmElmntLst = fstElmnt.getElementsByTagName("firstTime");
					firstTime = (fstNmElmntLst.item(0)).getTextContent();
					
			    } //if
			    
			}	//for	
			
		}catch (Exception  e){
			log.error(e.getMessage());
						
		}
	}	//constructor	
	
	
	
	
	
	//saves Configuration object into a  given config.xml file 
		public boolean save(String path) {    
		
				String comment;
				
			try{
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("configuration");
				doc.appendChild(rootElement);
				
				// DB element
				comment = "database values for accessing to POSTGIS";
				rootElement.appendChild(doc.createComment(comment));
				
				Element db = doc.createElement("DB");
				rootElement.appendChild(db);
		 
					//dbPlatform subelement
					Element dbPlatform = doc.createElement("platform");
					dbPlatform.appendChild(doc.createTextNode(this.dbPlatform));
					db.appendChild(dbPlatform);
					
					//dbDriverName subelement
					Element dbDriverName = doc.createElement("driverName");
					dbDriverName.appendChild(doc.createTextNode(this.dbDriverName));
					db.appendChild(dbDriverName);
				
					//dbUser subelement
					Element dbUser = doc.createElement("user");
					dbUser.appendChild(doc.createTextNode(this.dbUser));
					db.appendChild(dbUser);
					
					//dbPwd subelement
					Element dbPwd = doc.createElement("pwd");
					dbPwd.appendChild(doc.createTextNode(this.dbPwd));
					db.appendChild(dbPwd);
					
					
					
					//dbHost subelement
					Element dbHost = doc.createElement("host");
					dbHost.appendChild(doc.createTextNode(this.dbHost));
					db.appendChild(dbHost);
					
					//dbPort subelement
					Element dbPort = doc.createElement("port");
					dbPort.appendChild(doc.createTextNode(this.dbPort));
					db.appendChild(dbPort);					
					
					//dbName subelement
					Element dbName = doc.createElement("name");
					dbName.appendChild(doc.createTextNode(this.dbName));
					db.appendChild(dbName);		
				
					
					
					
				    //dbResetBaseScript subelement
					Element dbResetBaseScript = doc.createElement("resetBaseScript");
					dbResetBaseScript.appendChild(doc.createTextNode(this.dbResetBaseScript));
					db.appendChild(dbResetBaseScript);
					
					//dbResetOCDScript subelement
					Element dbResetOCDScript = doc.createElement("resetOCDScript");
					dbResetOCDScript.appendChild(doc.createTextNode(this.dbResetOCDScript));
					db.appendChild(dbResetOCDScript);				
					
					//dbEnable2DScript subelement
					Element dbEnable2DScript = doc.createElement("enable2DScript");
					dbEnable2DScript.appendChild(doc.createTextNode(this.dbEnable2DScript));
					db.appendChild(dbEnable2DScript);
					
					//dbScriptSeparator subelement
					Element dbScriptSeparator = doc.createElement("scriptSeparator");
					dbScriptSeparator.appendChild(doc.createTextNode(this.dbScriptSeparator));
					db.appendChild(dbScriptSeparator);
				    
					
					
				    
					//geometryFromLonLatSrid subelement
					Element geometryFromLonLatSrid = doc.createElement("geometryFromLonLatSrid");
					geometryFromLonLatSrid.appendChild(doc.createTextNode(this.geometryFromLonLatSrid));
					db.appendChild(geometryFromLonLatSrid);
					
					//lonFromPoint subelement
					Element lonFromPoint = doc.createElement("lonFromPoint");
					lonFromPoint.appendChild(doc.createTextNode(this.lonFromPoint));
					db.appendChild(lonFromPoint);					
					
					//latFromPoint subelement
					Element latFromPoint = doc.createElement("latFromPoint");
					latFromPoint.appendChild(doc.createTextNode(this.latFromPoint));
					db.appendChild(latFromPoint);		
					
							
				
				//mappingDir element
				comment = "mappind directory. A mapping file (one per data source) tells the correspondnce between categories in FE and categories in the data source";
				rootElement.appendChild(doc.createComment(comment));	
				Element mappingDir = doc.createElement("mappingDir");
				mappingDir.appendChild(doc.createTextNode(this.mappingDir));
				rootElement.appendChild(mappingDir);
				
				//fusionDir element
				comment = "fusion directory. A fusion file (one per OCD) tells the FE the fusion rules to be applied when generating the OCD";
				rootElement.appendChild(doc.createComment(comment));	
				Element fusionDir = doc.createElement("fusionDir");
				fusionDir.appendChild(doc.createTextNode(this.fusionDir));
				rootElement.appendChild(fusionDir);
				
				//apiRulesDir element
				comment = "api rules. An API fiile (one per API) describes and API and how itr can be used to retrieve data.TBD";
				rootElement.appendChild(doc.createComment(comment));	
				Element apiRulesDir = doc.createElement("apiRulesDir");
				apiRulesDir.appendChild(doc.createTextNode(this.apiRulesDir));
				rootElement.appendChild(apiRulesDir);
				
				//dbScriptsDir element
				comment = "db scripts. Database scripts are used to autogenerate empty OCDs with a particular data model. A new OCD is modeled in POSTGIS as a new database";
				rootElement.appendChild(doc.createComment(comment));	
				Element dbScriptsDir = doc.createElement("dbScriptsDir");
				dbScriptsDir.appendChild(doc.createTextNode(this.dbScriptsDir));
				rootElement.appendChild(dbScriptsDir);
				
				//logDir element
				comment = "log directory. ocds_will output logs here <ocd_name.log>";
				rootElement.appendChild(doc.createComment(comment));	
				Element logDir = doc.createElement("logDir");
				logDir.appendChild(doc.createTextNode(this.logDir));
				rootElement.appendChild(logDir);
				
				
				//firstTime element
				comment = "Only yes at the beginning prior to building the databases";
				rootElement.appendChild(doc.createComment(comment));	
				Element firstTime = doc.createElement("firstTime");
				firstTime.appendChild(doc.createTextNode(this.firstTime));
				//irstTime.appendChild(doc.createTextNode("no"));
				rootElement.appendChild(firstTime);
					
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				File file = new File(path);
				StreamResult result = new StreamResult(file);
		 
					 
				transformer.transform(source, result);	
				//log.debug("Configuration saved successfully");
				return true;
				
			}catch (Exception  e){
				log.error(e.getMessage());
				return false;
			}
			
		}
	
	// GET METHODS
	
	public String getConnectionString() {
		String str = "jdbc:"+dbPlatform+"://"+dbHost+":"+dbPort+"/"+dbName;
		return  str;
	}	
	
	public String getPlatform() {return  dbPlatform;}
	public String getDriverName() {return  dbDriverName;}
	public String getUser() {return dbUser;}		
	public String getPwd() {return dbPwd;}
	
	public String getHost() {return dbHost;}
	public String getPort() {return dbPort;}
	public String getDBName() {return dbName;}
	
	
	public String getResetBaseScript() {return  dbResetBaseScript;}
	public String getResetOCDScript() {return  dbResetOCDScript;}
	public String getEnable2DScript() {return  dbEnable2DScript;}
	public String getScriptSeparator() {return  dbScriptSeparator;}
	
	
	public String getGeometryFromLonLatSrid() {return  geometryFromLonLatSrid;}
	public String getLonFromPoint() {return  lonFromPoint;}
	public String getLatFromPoint() {return  latFromPoint;}
	
	
	
	
	public String getMappingDir() {return mappingDir;}
	public String getFusionDir() {return fusionDir;}
	public String getApiRulesDir() {return apiRulesDir;}
	public String getDBScriptsDir() {return dbScriptsDir;}
	
	public String getLogDir() {return logDir;}
	
	public String getFirstTime() {return firstTime;}
	

	
	// SET METHODS
	
		
	public void setPlatform(String str) {dbPlatform =str;}
	public void setDriverName(String str) {dbDriverName=str;}
	public void setUser(String str) {dbUser = str;}		
	public void setPwd(String str) {dbPwd = str;}
	
	public void setHost(String str) {dbHost = str ;}
	public void setPort(String str) {dbPort = str;}
	public void setDBName(String str) {dbName = str;}
	
	
	public void setResetBaseScript(String str) {dbResetBaseScript = str;}
	public void setResetOCDScript(String str) {dbResetOCDScript = str;}
	public void setEnable2DScript(String str) {dbEnable2DScript = str;}
	public void setScriptSeparator(String str) {dbScriptSeparator = str;}
	
	
	public void setGeometryFromLonLatSrid(String str) {geometryFromLonLatSrid = str;}
	public void setLonFromPoint(String str) {lonFromPoint = str;}
	public void setLatFromPoint(String str) {latFromPoint=str;}
	
	
	
	
	public void setMappingDir(String str) {mappingDir = str;}
	public void setFusionDir(String str) {fusionDir = str;}
	public void setApiRulesDir(String str) {apiRulesDir = str;}
	public void setDBScriptsDir(String str) {dbScriptsDir = str;}
	public void setLogDir(String str) {logDir = str;}
	
	public void setFirstTime(String str) {firstTime = str;}
	
	
	public String toString () {
		String response = null;
		
		response = "configuration\n";
		response = response + "\tDB\n";
		response = response + "\t\tplatform: "+this.getConnectionString()+"\n";
		response = response + "\t\tplatform: "+dbPlatform+"\n";
		response = response + "\t\tdriverName: "+dbDriverName+"\n";
		response = response + "\t\tuser: "+dbUser+"\n";
		response = response + "\t\tpwd: "+dbPwd+"\n";
		
		response = response + "\t\thost: "+dbHost+"\n";		
		response = response + "\t\tport: "+dbPort+"\n";
		response = response + "\t\tname: "+dbName+"\n";
		
		
		response = response + "\t\tresetBaseScript: "+dbResetBaseScript+"\n";
		response = response + "\t\tresetOCDScript: "+dbResetOCDScript+"\n";
		response = response + "\t\tenable2DScript: "+dbEnable2DScript+"\n";
		response = response + "\t\tscriptSeparator: "+dbScriptSeparator+"\n";
				
		
		response = response + "\tmappingDir: "+mappingDir+"\n";
		response = response + "\tfusionDir: "+fusionDir+"\n";
		response = response + "\tapiRulesDir: "+apiRulesDir+"\n";
		response = response + "\tdbScriptsDir: "+dbScriptsDir+"\n";
		
		response = response + "\tlogDir: "+logDir+"\n";
		
		response = response + "\tfirstTime: "+firstTime+"\n"; 

		return response;
	}


	
	
	
}	//class
