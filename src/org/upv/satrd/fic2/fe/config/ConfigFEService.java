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

public class ConfigFEService {

	private String dbPlatform;
	private String dbDriverName;
	private String dbUser;		
	private String dbPwd;
	
	private String dbHost;
	private String dbPort;
	private String dbName;
	
	
	private String dbResetOCDPOIScript;
	
	private String dbScriptSeparator = ";";
	
	
	private String geometryFromLonLatSrid;
	private String lonFromPoint;
	private String latFromPoint;	
	
	private String logFile;	
	
	
	
	
	
	public ConfigFEService(String path) {  
		
	    Element fstElmnt, fstElmnt2 ; 
	    NodeList fstNmElmntLst, fstNmElmntLst2; 	   
	    
	    
	    
	    
		
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
						    

		    						    				
		    				fstNmElmntLst2 = fstElmnt2.getElementsByTagName("resetOCDPOIScript");
		    				dbResetOCDPOIScript = (fstNmElmntLst2.item(0)).getTextContent();
		    				
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
			    						
					
					fstNmElmntLst = fstElmnt.getElementsByTagName("logFile");
					logFile = (fstNmElmntLst.item(0)).getTextContent();
					
					
			    } //if
			    
			}	//for	
			
		}catch (Exception  e){
			//log.error(e.getMessage());
						
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
				
					
					
					
				    
					
					//dbResetOCDPOIScript subelement
					Element dbResetOCDPOIScript = doc.createElement("resetOCDPOIScript");
					dbResetOCDPOIScript.appendChild(doc.createTextNode(this.dbResetOCDPOIScript));
					db.appendChild(dbResetOCDPOIScript);				
					
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
					
					
				
				//logFile element
				comment = "log directory. ocds_will output logs here <ocd_name.log>";
				rootElement.appendChild(doc.createComment(comment));	
				Element logFile = doc.createElement("logFile");
				logFile.appendChild(doc.createTextNode(this.logFile));
				rootElement.appendChild(logFile);
				
					
				
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
				//log.error(e.getMessage());
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
	
	
	
	public String getResetOCDPOIScript() {return  dbResetOCDPOIScript;}	
	public String getScriptSeparator() {return  dbScriptSeparator;}
	
	
	public String getGeometryFromLonLatSrid() {return  geometryFromLonLatSrid;}
	public String getLonFromPoint() {return  lonFromPoint;}
	public String getLatFromPoint() {return  latFromPoint;}
	
	
	
	
	
	
	public String getLogFile() {return logFile;}
	
	
	

	
	// SET METHODS
	
		
	public void setPlatform(String str) {dbPlatform =str;}
	public void setDriverName(String str) {dbDriverName=str;}
	public void setUser(String str) {dbUser = str;}		
	public void setPwd(String str) {dbPwd = str;}
	
	public void setHost(String str) {dbHost = str ;}
	public void setPort(String str) {dbPort = str;}
	public void setDBName(String str) {dbName = str;}
	
	
	
	public void setResetOCDPOIScript(String str) {dbResetOCDPOIScript = str;}	
	public void setScriptSeparator(String str) {dbScriptSeparator = str;}
	
	
	public void setGeometryFromLonLatSrid(String str) {geometryFromLonLatSrid = str;}
	public void setLonFromPoint(String str) {lonFromPoint = str;}
	public void setLatFromPoint(String str) {latFromPoint=str;}
	
	
	
	
	public void setLogFile(String str) {logFile = str;}
	
	
	
	
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
		
		response = response + "\t\tresetOCDScript: "+dbResetOCDPOIScript+"\n";	
		response = response + "\t\tscriptSeparator: "+dbScriptSeparator+"\n";	
		
		
		response = response + "\t\tgeometryFromLonLatSrid: "+geometryFromLonLatSrid+"\n";
		response = response + "\t\tlonFromPoint: "+lonFromPoint+"\n";	
		response = response + "\t\tlatFromPoint: "+latFromPoint+"\n";			
		
		
		response = response + "\t\tlogFile: "+logFile+"\n";	

		return response;
	}


	
	
	
}	//class
