package org.upv.satrd.fic2.fe.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class POILabel {

	private Integer id;
	private Integer poiId;
	private Integer labeltypeId;
	private String value;
	private Integer sourceId;
	private String language;
	private Integer licenseId;
	private Date updated;
	

	
	
	private static org.apache.log4j.Logger log =Logger.getLogger(org.upv.satrd.fic2.fe.db.POILabel.class);
	
	
	//The POILabel object has been inserted in the DDB, and the id is known
	public POILabel(Integer id, Integer poiid, Integer labeltypeId, String value, Integer sourceId, String language, Integer licenseId,Date updated){
		this.id = id;
		this.poiId = poiid;
		this.labeltypeId = labeltypeId;
		this.value = value;
		this.sourceId = sourceId;
		this.language = language;
		this.licenseId = licenseId;
		this.updated = updated;	
	}
	
	
	//The POILabel object has not been inserted in the DDB, and the id is not known
	public POILabel(Integer poiid, Integer labeltypeId, String value, Integer sourceId, String language, Integer licenseId,Date updated){
		this.id = null;
		this.poiId = poiid;
		this.labeltypeId = labeltypeId;
		this.value = value;
		this.sourceId = sourceId;
		this.language = language;
		this.licenseId = licenseId;
		this.updated = updated;	
	}
	
	//Empty POILabel. Useful for creating a new POILabel and insert step by step all values extracted from a JSON file
	public POILabel(){
		this.id = null;
		this.poiId = null;
		this.labeltypeId = null;
		this.value = null;
		this.sourceId = null;
		this.language = null;
		this.licenseId = null;
		this.updated = null;	
	}
	
	
	
	//GET METHODS
	public Integer getId(){ return this.id;}
	public Integer getPOIId(){return this.poiId;}
	public Integer getLabelTypeId(){return this.labeltypeId;}
	public String getValue(){return this.value;}
	public Integer getSourceId(){return this.sourceId;}
	public String getLanguage(){return this.language;}
	public Integer getLicenseId(){return this.licenseId;}
	public Date getUpdated(){return this.updated;}
	
	
	
	
	//SET METHODS
	public void setId(Integer id){ this.id = id;}
	public void setPOIId(Integer poi_id){this.poiId=poi_id;}
	public void setLabeltypeId(Integer labeltypeId){this.labeltypeId = labeltypeId;}
	public void setValue(String value){this.value = value;}
	public void setSourceId(Integer sourceId){this.sourceId= sourceId;}
	public void setLanguage(String language){this.language = language;}
	public void setLicenseId(Integer licenseId){this.licenseId = licenseId;}
	public void setUpdated(Date updated){this.updated = updated;}
	
	
	

	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////DB methods/////////////////////////////////////////
	
	public static POILabel getPOILabelClassById(Connection con, Integer id, Logger log){
		POILabel poilabel = null;
		
		Statement stmt;
		ResultSet rs;
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POILabel.log;
		
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM poilabel WHERE id="+id;
			rs = stmt.executeQuery(sql);
			Object aux= null;
			
			
			list = resultSetToArrayList(rs);        		
			
			if (!list.isEmpty()){				
					
				//poiid field mandatory
				Integer poiId = new Integer((list.get(0)).get("poiid").toString());
				
				//typeid field mandatory
				Integer labeltypeId = new Integer((list.get(0)).get("typeid").toString());
				
				//value field mandatory
				String value = (list.get(0)).get("value").toString();
				
				//sourceId field mandatory
				Integer sourceId = new Integer((list.get(0)).get("sourceid").toString());				
				
				//language field optional
				String language = null;
				aux = (list.get(0)).get("language");				
				if (aux!=null) language = aux.toString();
				
				//licenseId mandatory, but maybe optional
				Integer licenseId = null;
				aux = (list.get(0)).get("licenseId");				
				if (aux!=null) licenseId = new Integer(aux.toString());
				
				//updated field mandatory
				Date updated = null;
				aux = (list.get(0)).get("updated");
				updated = Date.valueOf(aux.toString());				
										 
				poilabel = new POILabel(id, poiId,labeltypeId,value,sourceId,language,licenseId,updated);
				
			}			
			rs.close();
	        stmt.close();  
			
		} catch ( SQLException e ) {
			System.out.println("Error POILabel.getPOILabelClassById(): "+e.getMessage());
			log.error("Error POILabel.getPOILabelClassById(): "+e.getMessage());
		}
		
		return poilabel;
		
	}
	
	
	public static Integer savePOILabel(Connection con, POILabel poilabel, Logger log){
	
	
		Integer id=null;		
		String sql;
		PreparedStatement ps;	
		if (log == null) log = POILabel.log;
		
		
			//avoid inserting POILabels whose value is null or empty
			if ((poilabel.getValue()!=null) && (!poilabel.getValue().trim().isEmpty()) ){
				try{   	
					
					sql = "INSERT INTO POILabel (poiid,typeid,value,"
							+ "sourceId,language,licenseId,updated) "
							+ "VALUES (?,?,?,?,?,?,?)";	
		
					String generatedColumns[] = { "id" };
					ps = con.prepareStatement(sql,
							generatedColumns);
					
					ps.setInt(1,poilabel.getPOIId());
					ps.setInt(2,poilabel.getLabelTypeId());		
					//value field should be mandatory, but maybe one can insert NULL (e.g inserting source of POI.position as POILabel)
					if (poilabel.getValue() == null) {
						ps.setNull(3, java.sql.Types.VARCHAR);
					} else {
						String val = poilabel.getValue();
						if (val.length() > 1990) {
							val = val.substring(0, 1990);
						}
						ps.setString(3,val);
					}

					ps.setInt(4,poilabel.getSourceId());
					//language field might be set to null if it unknown
					if (poilabel.getLanguage() == null) ps.setNull(5, java.sql.Types.VARCHAR); else ps.setString(5,poilabel.getLanguage());
					//language field might be set to null if it unknown
					if (poilabel.getLicenseId() == null) ps.setNull(6, java.sql.Types.INTEGER); else ps.setInt(6,poilabel.getLicenseId());
					if (poilabel.getUpdated() == null) ps.setNull(7, java.sql.Types.DATE); else ps.setDate(7,poilabel.getUpdated());										
					
					//System.out.println(ps.toString());
					ps.executeUpdate();
					
					ResultSet rs = ps.getGeneratedKeys();
					if (rs.next()) { id = rs.getInt(1); }	
									
					rs.close();				
			        ps.close();
					
				} catch ( SQLException e) {
					System.out.println("Error POILabel.savePOILabel(): "+e.getMessage());
					log.error("Error POILabel.savePOILabel(): "+e.getMessage());
				}	
			}
	
		return id;
	}
	
	
	
	public static boolean deletePOILabelById(Connection con, Integer id, Logger log){			
			
		String sql;		
		Statement stmt;
		if (log == null) log = POILabel.log;
				
		try {
			
			
			stmt = con.createStatement();
			
			//delete dependencies with other tables
			sql = "DELETE FROM poicomponent WHERE poilabelid="+id+";";
			stmt.executeUpdate(sql);
			
			//finally delete the poilabel
			sql = "DELETE FROM poilabel WHERE id="+id+";";
			stmt.executeUpdate(sql);
			
			stmt.close();
			
				
		} catch ( SQLException e) {
			System.out.println("Error POILabel.deletePOILabelById(): "+e.getMessage());
			log.error("Error POILabel.deletePOILabelById(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	public static boolean deletePOILabelByPOIId(Connection con, Integer id, Logger log){			
		
		if (log == null) log = POILabel.log;	
		try {				
			
			//delete dependencies with other tables
			//Get all POILables, retrieve their id and delelete them from 'poicomponent' table
			ArrayList<POILabel> poilabel_array = getPOILabelListByPOIid(con,id,log);
			for (int k=0;k<poilabel_array.size();k++){
				
				POILabel poilabel = poilabel_array.get(k);
				deletePOILabelById(con,poilabel.getId(),log);				
			}			
			
				
		} catch ( Exception e) {
			System.out.println("Error POILabel.deletePOILabelByPOIId(): "+e.getMessage());
			log.error("Error POILabel.deletePOILabelByPOIId(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	public static boolean deletePOILabelByLicenseId(Connection con, Integer id, Logger log){			
		
		if (log == null) log = POILabel.log;
		try {					
			
			//delete dependencies with other tables
			//Get all POILables, retrieve their id and delelete them from 'poicomponent' table
			ArrayList<POILabel> poilabel_array = getPOILabelListByLicenseId(con,id,log);
			for (int k=0;k<poilabel_array.size();k++){
				POILabel poilabel = poilabel_array.get(k);
				deletePOILabelById(con,poilabel.getId(),log);				
			}			
				
		} catch ( Exception e) {
			System.out.println("Error POILabel.deletePOILabelByLicenseId(): "+e.getMessage());
			log.error("Error POILabel.deletePOILabelByLicenseId(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	
	public static boolean deletePOILabelByLabelTypeId(Connection con, Integer id, Logger log){			
		
		if (log == null) log = POILabel.log;
		try {				
			//delete dependencies with other tables
			//Get all POILables, retrieve their id and delelete them from 'poicomponent' table
			ArrayList<POILabel> poilabel_array = getPOILabelListByLabelTypeId(con,id,log);
			for (int k=0;k<poilabel_array.size();k++){
				POILabel poilabel = poilabel_array.get(k);
				deletePOILabelById(con,poilabel.getId(), log);
				
			}		
				
		} catch ( Exception e) {
			System.out.println("Error POILabel.deletePOILabelByLabelTypeId(): "+e.getMessage());
			log.error("Error POILabel.deletePOILabelByLabelTypeId(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	public static boolean deletePOILabelBySourceId(Connection con, Integer id, Logger log){			
		
		if (log == null) log = POILabel.log;
		try {				
			//delete dependencies with other tables
			//Get all POILables, retrieve their id and delelete them from 'poicomponent' table
			ArrayList<POILabel> poilabel_array = getPOILabelListBySourceId(con,id,log);
			for (int k=0;k<poilabel_array.size();k++){
				POILabel poilabel = poilabel_array.get(k);
				deletePOILabelById(con,poilabel.getId(),log);
				
			}		
				
		} catch ( Exception e) {
			System.out.println("Error POILabel.deletePOILabelBySourceId(): "+e.getMessage());
			log.error("Error POILabel.deletePOILabelBySourceId(): "+e.getMessage());
			return false;
		}	
	
		return true;
	}
	
	
	public static String getValuebyPOIidandLabelTypeName(Connection con, Integer poiId, String labeltypeName, Logger log){			
		
		if (log == null) log = POILabel.log;
		String address = null;
		
		//search POILabel by poiid and labeltype
	    LabelType labeltype = LabelType.getLabelTypeClassByName(con, labeltypeName,log);
	    
	    if (labeltype !=null){	
	    	Integer labeltypeId = labeltype.getId();
	    	ArrayList<POILabel> poilabel_list = POILabel.getPOILabelListByPOIidAndLabelTypeId(con, poiId, labeltypeId,log);
	    	
	    	if ((poilabel_list!=null) && (!poilabel_list.isEmpty())){
	    		
	    		//typically we should only have one value/poilabel, take the first one	    		
    			POILabel poilabel = poilabel_list.get(0);    			
			    address = poilabel.getValue();	    		
	    	}
	    	
	    }
	
		return address;
	}
	
	
	
		
	
	public static boolean updatePOILabel(Connection con, POILabel poilabel, Logger log){
				
		String sql;
		PreparedStatement ps;	
		if (log == null) log = POILabel.log;
		
			try{   	
				
				sql = "UPDATE POILabel SET poiid=?, typeid=?, value=?, sourceid=?, languageid=?, licenseid=?, updated  WHERE id="+poilabel.getId();
	
				ps = con.prepareStatement(sql);	
				
				ps.setInt(1,poilabel.getPOIId());
				ps.setInt(2,poilabel.getLabelTypeId());		
				//value field should be mandatory, but maybe one can insert NULL (e.g inserting source of POI.position as POILabel)
				
				if (poilabel.getValue() == null) {
					ps.setNull(3, java.sql.Types.VARCHAR);
				} else {
					String val = poilabel.getValue();
					if (val.length() > 1990) {
						val = val.substring(0, 1990);
					}
					ps.setString(3,val);
				}
				
				
				ps.setInt(4,poilabel.getSourceId());
				//language field might be set to null if it unknown
				if (poilabel.getLanguage() == null) ps.setNull(5, java.sql.Types.VARCHAR); else ps.setString(5,poilabel.getLanguage());
				//language field might be set to null if it unknown
				if (poilabel.getLicenseId() == null) ps.setNull(6, java.sql.Types.INTEGER); else ps.setInt(6,poilabel.getLicenseId());
				if (poilabel.getUpdated() == null) ps.setNull(7, java.sql.Types.DATE); else ps.setDate(6,poilabel.getUpdated());											
				
				ps.executeUpdate();
				
				ps.close();								
				
				
			} catch ( SQLException e) {
				System.out.println("Error POILabel.updatePOILabel(): "+e.getMessage());
				log.error("Error POILabel.updatePOILabel(): "+e.getMessage());
				return false;
			}	
	
		return true;
	}
	
	
	
	
	public static ArrayList<POILabel> getPOILabelListByPOIid (Connection con, Integer poiId, Logger log){
		
		ArrayList<POILabel> poilabel_array = new ArrayList<POILabel>();
		String sql;
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POILabel.log;
		
		try {
			Statement stmt = con.createStatement();
			sql = "SELECT * FROM poilabel WHERE poiid="+poiId;
			//System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			Object aux=null;
			
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){	
				
				for (int k=0;k<list.size();k++){
				
					//id field mandatory
					Integer id = new Integer((list.get(k)).get("id").toString());
					
					//typeid field mandatory
					Integer labeltypeId = new Integer((list.get(k)).get("typeid").toString());
					
					//value field mandatory
					String value = (list.get(k)).get("value").toString();
					
					
					//sourceId field mandatory
					Integer sourceId = new Integer((list.get(k)).get("sourceid").toString());				
					
					//language field optional
					String language = null;
					aux = (list.get(k)).get("language");				
					if (aux!=null) language = aux.toString();
					
					//licenseId mandatory, but maybe optional
					Integer licenseId = null;
					aux = (list.get(k)).get("licenseId");				
					if (aux!=null) licenseId = new Integer(aux.toString());
					
					//updated field mandatory
					Date updated = null;
					aux = (list.get(k)).get("updated");
					if (aux!=null) updated = Date.valueOf(aux.toString());				
											 
					POILabel poilabel = new POILabel(id, poiId,labeltypeId,value,sourceId,language,licenseId,updated);
					
					poilabel_array.add(poilabel);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error POILabel.getPOILabelListByPOIid(): "+e.getMessage());
			log.error("Error POILabel.getPOILabelListByPOIid(): "+e.getMessage());
		}
		
		return poilabel_array;
	}
	
	public static ArrayList<POILabel> getPOILabelListByPOIidAndLabelTypeId (Connection con, Integer poiId, Integer labelTypeId, Logger log){
		
		ArrayList<POILabel> poilabel_array = new ArrayList<POILabel>();
		String sql;
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POILabel.log;
		
		try {
			Statement stmt = con.createStatement();
			sql = "SELECT * FROM poilabel WHERE poiid="+poiId+" AND typeid="+labelTypeId;
			ResultSet rs = stmt.executeQuery(sql);
			Object aux=null;
			
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){	
				
				for (int k=0;k<list.size();k++){
				
					//id field mandatory
					Integer id = new Integer((list.get(k)).get("id").toString());
					
					//typeid field mandatory
					Integer labeltypeId = new Integer((list.get(k)).get("typeid").toString());
					
					//value field mandatory
					String value = (list.get(k)).get("value").toString();				
					
					//sourceId field mandatory
					Integer sourceId = new Integer((list.get(k)).get("sourceid").toString());				
					
					//language field optional
					String language = null;
					aux = (list.get(k)).get("language");				
					if (aux!=null) language = aux.toString();
					
					//licenseId mandatory, but maybe optional
					Integer licenseId = null;
					aux = (list.get(k)).get("licenseId");				
					if (aux!=null) licenseId = new Integer(aux.toString());
					
					//updated field mandatory
					Date updated = null;
					aux = (list.get(k)).get("updated");
					updated = Date.valueOf(aux.toString());				
											 
					POILabel poilabel = new POILabel(id, poiId,labeltypeId,value,sourceId,language,licenseId,updated);
					
					poilabel_array.add(poilabel);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error POILabel.getPOILabelListByPOIid(): "+e.getMessage());
			log.error("Error POILabel.getPOILabelListByPOIid(): "+e.getMessage());
		}
		
		return poilabel_array;
	}
	
	
	
	public static ArrayList<POILabel> getPOILabelListByLabelTypeId (Connection con, Integer labeltypeId, Logger log){
		
		ArrayList<POILabel> poilabel_array = new ArrayList<POILabel>();
		String sql;
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POILabel.log;
		
		try {
			Statement stmt = con.createStatement();
			sql = "SELECT * FROM poilabel WHERE typeid="+labeltypeId;
			ResultSet rs = stmt.executeQuery(sql);
			Object aux=null;
			
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){	
				
				for (int k=0;k<list.size();k++){
				
					//id field mandatory
					Integer id = new Integer((list.get(k)).get("id").toString());
					
					//poiid field mandatory
					Integer poiId = new Integer((list.get(k)).get("poiid").toString());
					
					//value field mandatory
					String value = (list.get(k)).get("value").toString();				
					
					//sourceId field mandatory
					Integer sourceId = new Integer((list.get(k)).get("sourceid").toString());				
					
					//language field optional
					String language = null;
					aux = (list.get(k)).get("language");				
					if (aux!=null) language = aux.toString();
					
					//licenseId mandatory, but maybe optional
					Integer licenseId = null;
					aux = (list.get(k)).get("licenseId");				
					if (aux!=null) licenseId = new Integer(aux.toString());
					
					//updated field mandatory
					Date updated = null;
					aux = (list.get(k)).get("updated");
					updated = Date.valueOf(aux.toString());				
											 
					POILabel poilabel = new POILabel(id, poiId,labeltypeId,value,sourceId,language,licenseId,updated);
					
					poilabel_array.add(poilabel);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error POILabel.getPOILabelListByLabelTypeId(): "+e.getMessage());
			log.error("Error POILabel.getPOILabelListByLabelTypeId(): "+e.getMessage());
		}
		
		return poilabel_array;
	}
	
	
	public static ArrayList<POILabel> getPOILabelListBySourceId (Connection con, Integer sourceId, Logger log){
		
		ArrayList<POILabel> poilabel_array = new ArrayList<POILabel>();
		String sql;
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POILabel.log;
		
		try {
			Statement stmt = con.createStatement();
			sql = "SELECT * FROM poilabel WHERE typeid="+sourceId;
			ResultSet rs = stmt.executeQuery(sql);
			Object aux=null;
			
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){	
				
				for (int k=0;k<list.size();k++){
				
					//id field mandatory
					Integer id = new Integer((list.get(k)).get("id").toString());
					
					//poiid field mandatory
					Integer poiId = new Integer((list.get(k)).get("poiid").toString());
					
					//value field mandatory
					String value = (list.get(k)).get("value").toString();				
					
					//sourceId field mandatory
					Integer labeltypeId = new Integer((list.get(k)).get("typeid").toString());				
					
					//language field optional
					String language = null;
					aux = (list.get(k)).get("language");				
					if (aux!=null) language = aux.toString();
					
					//licenseId mandatory, but maybe optional
					Integer licenseId = null;
					aux = (list.get(k)).get("licenseId");				
					if (aux!=null) licenseId = new Integer(aux.toString());
					
					//updated field mandatory
					Date updated = null;
					aux = (list.get(k)).get("updated");
					updated = Date.valueOf(aux.toString());				
											 
					POILabel poilabel = new POILabel(id, poiId,labeltypeId,value,sourceId,language,licenseId,updated);
					
					poilabel_array.add(poilabel);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error POILabel.getPOILabelListByLabelTypeId(): "+e.getMessage());
			log.error("Error POILabel.getPOILabelListByLabelTypeId(): "+e.getMessage());
		}
		
		return poilabel_array;
	}
	
	
	public static ArrayList<POILabel> getPOILabelListByLicenseId (Connection con, Integer licenseId, Logger log){
		
		ArrayList<POILabel> poilabel_array = new ArrayList<POILabel>();
		String sql;
		
		ArrayList<HashMap<String, Object>> list;
		if (log == null) log = POILabel.log;
		
		try {
			Statement stmt = con.createStatement();
			sql = "SELECT * FROM poilabel WHERE typeid="+licenseId;
			ResultSet rs = stmt.executeQuery(sql);
			Object aux=null;
			
			
			list = resultSetToArrayList(rs);
			
			if (!list.isEmpty()){	
				
				for (int k=0;k<list.size();k++){
				
					//id field mandatory
					Integer id = new Integer((list.get(k)).get("id").toString());
					
					//poiid field mandatory
					Integer poiId = new Integer((list.get(k)).get("poiid").toString());
					
					//typeid field mandatory
					Integer labeltypeId = new Integer((list.get(k)).get("typeid").toString());
					
					//value field mandatory
					String value = (list.get(k)).get("value").toString();				
					
					//sourceId field mandatory
					Integer sourceId = new Integer((list.get(k)).get("sourceid").toString());				
					
					//language field optional
					String language = null;
					aux = (list.get(k)).get("language");				
					if (aux!=null) language = aux.toString();
					
										
					//updated field mandatory
					Date updated = null;
					aux = (list.get(k)).get("updated");
					updated = Date.valueOf(aux.toString());				
											 
					POILabel poilabel = new POILabel(id, poiId,labeltypeId,value,sourceId,language,licenseId,updated);
					
					poilabel_array.add(poilabel);
				}
				
			}			
		
			rs.close();
	        stmt.close();
			
		} catch ( Exception e ) {
			System.out.println("Error POILabel.getPOILabelListByLabelTypeId(): "+e.getMessage());
			log.error("Error POILabel.getPOILabelListByLabelTypeId(): "+e.getMessage());
		}
		
		return poilabel_array;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////Utility method///////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	
	public static ArrayList<HashMap<String, Object>> resultSetToArrayList(ResultSet rs) throws SQLException{
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    ArrayList<HashMap<String, Object>> results = new ArrayList<HashMap<String, Object>>();

	    while (rs.next()) {
	        HashMap<String, Object> row = new HashMap<String, Object>();
	        
	        for(int i=1; i<=columns; i++){
	          row.put(md.getColumnName(i).toLowerCase(),rs.getObject(i));
	        }
	        
	        results.add(row);
	    }
	    
	    return results;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////testing method///////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	
	public static void testPOILabel(Connection con) throws ParseException{
		System.out.println("****************************************************************************************");
		System.out.println("POILabel test. Creating, inserting,selecting and deleting a POILabel object from DB");	
		log.info("****************************************************************************************");
		log.info("POILabel test. Creating, inserting,selecting and deleting a POILabel object from DB");	
		
		
		//Before creating a POILabel, we need to previously create objects: License, POI, LabelType, Source
		LabelType labeltype = new LabelType("some_name");
		Integer labeltypeId = LabelType.saveLabelType(con, labeltype,null);
		
		License license = new License("some name","some_description","some_info");
		Integer licenseId = License.saveLicense(con, license,null);
		
		//Before creating source, we need previously a APIType object
		APIType apitype = new APIType("some_name","some_description","some_apirules");
		Integer apitypeId = APIType.saveAPIType(con, apitype,null);
		Source source = new Source("some_name","some_description","some_url_access","some_categorymapping",apitypeId);
		Integer sourceId = Source.saveSource(con, source,null);
		
		//Set the current time
		Calendar cal = Calendar.getInstance();				
		SimpleDateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");				
		String date1 = outputformat.format(cal.getTime());		
		java.util.Date date2 = outputformat.parse(date1);						
		java.sql.Date sqlDate = new java.sql.Date(date2.getTime());
		
		Double latitude = -0.345547;
		Double longitude = 39.473408;
		POI poi = new POI("some_name",latitude,longitude,sqlDate);
		Integer poiId = POI.savePOI(con, poi,null);
		
		
		
		
		POILabel poilabel = new POILabel(poiId,labeltypeId,"some_value",sourceId,"some_language",licenseId,sqlDate);
		System.out.println("Creating POILabel class...... OK");
		log.info("Creating POILabel class......OK");
		
		
		Integer id = POILabel.savePOILabel(con, poilabel,null);
		if (id==null){
			System.out.println("Saving POILabel class...... Error ");
			log.info("Saving POILabel class...... Error ");
		}else{
			System.out.println("Saving POILabel class...... OK ");
			log.info("Saving POILabel class...... OK ");
			
			POILabel poilabel2 = POILabel.getPOILabelClassById(con, id,null);
			if (poilabel2==null){
				System.out.println("Getting POILabel class by ID...... Error ");
				log.info("Getting POILabel class by ID...... Error ");
			}else{
				System.out.println("Getting POILabel class by ID...... OK ");
				log.info("Getting POILabel class by ID...... OK ");	
				
				
				if (!POILabel.deletePOILabelById(con, id,null)){
					System.out.println("Deleting POILabel class by ID ...... Error ");
					log.info("Deleting POILabel class by ID ...... Error  ");
				}else{
					System.out.println("Deleting POILabel class by ID ...... OK ");
					log.info("Deleting POILabel class by ID ...... OK  ");
					
					System.out.println("All tests passed OK");
					log.info("All tests passed OK");	
					
					//Delete previous objects
					LabelType.deleteLabelTypeById(con, labeltypeId,null);
					License.deleteLicenseById(con, licenseId,null);
					Source.deleteSourceById(con, sourceId,"base",null);	
					APIType.deleteAPITypeById(con, apitypeId,"base",null);
								
					
				}			
				
			}
		}
		
		System.out.println("****************************************************************************************");
		log.info("****************************************************************************************");
		
		
	}
	
	

	

	
		
}
