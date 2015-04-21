<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.save_source");%>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*, java.text.*" %>
<%@ page import="javax.servlet.http.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href="../css/main.css">  
</head>
<body bgcolor="white" style="{ margin:0;}" background="../img/centerf2.png" style="{ background-repeat: no-repeat;}">

<%    
	    
    //Connect to the database
    String relativeWebPath = "/config/config.xml";
	String configPath = getServletContext().getRealPath(relativeWebPath);
	Configuration configFile = new Configuration(configPath);
	
	Connection con = OutputDB.connectDB(
			configFile.getConnectionString(),
			configFile.getUser(),
			configFile.getPwd(),
			configFile.getDriverName(),null);
    
    
    int error = 1;
	String state = "";
    
    //Get all values from the posted form (name, des, access, mapping, mapping2,delete2, api,city,license)
    // request.getParameter will return null as it is a multipart message
    String name="";     
	String description="";
	String access="";	
	String api ="1";
	Vector<String> city = new Vector<String>();
	Vector<String> license = new Vector<String>();    	
	
	
	
		
	
	//Obtain and save categoryMapping file
	String filePath = getServletContext().getRealPath(configFile.getMappingDir())+"/"; 
	String fullPath = filePath; 
 	DiskFileItemFactory factory = new DiskFileItemFactory();
 	File file;
 	
    ServletFileUpload upload = new ServletFileUpload(factory); // Create a new file upload handler
    try{ 
        // Parse the request to get file items.
        List<FileItem> fileItems = upload.parseRequest(request);

        // Process the uploaded file items
        Iterator<FileItem> i = fileItems.iterator();
        
        while ( i.hasNext () ) {
        	
           FileItem fi = (FileItem)i.next();
           if ( !fi.isFormField () ){
        	   
	            
        	    // Get the uploaded file parameters
	            String fieldName = fi.getFieldName();
	            String fileName = fi.getName();
	            
	            boolean isInMemory = fi.isInMemory();
	            long sizeInBytes = fi.getSize();
	            
	            //Check that the file is valid (i.e. it is a properties file)
	            String fileaux = fileName.substring( fileName.lastIndexOf("."));
	           
	            
	            if (fileaux.equalsIgnoreCase(".properties")){
	         	
		         	 // Write the file
		             int index_aux = fileName.lastIndexOf("/");
		         	 if( index_aux >= 0 )  	fullPath = filePath + fileName.substring( index_aux);
		             else               	fullPath = filePath + fileName.substring(index_aux+1);
		         	 		         	 
		             
		         	 file = new File (fullPath);
		         	 if (!file.isFile()){
		         		fi.write( file ) ;			               
			             
		         	 }else{
		         		 //change the filename in order not to overwrite the existing file. Take the current date (including sec) as discriminator		         		 
		         		SimpleDateFormat dt = new SimpleDateFormat("yyyyy_MM_dd_hh_mm_ss");
		         		Calendar cal = Calendar.getInstance();
		         		String date = dt.format(cal.getTime()); 
		         		
		         		//Detect the '.' to insert the discriminator
		         		String pre = fullPath.substring(0,fullPath.lastIndexOf("."));
		         		String post = fullPath.substring(fullPath.lastIndexOf("."));
		         		fullPath = pre+"_"+date+post;		         		
		         		
		         		file = new File (fullPath);
		         		fi.write( file ) ;
		         		
		         	 }
		         	error = 0;
	             
	            }else {
	            	state = "Error: Wrong file format, only .properties file is supported";
	            	fullPath = "";
	            }
	            		              
           } else{  // if ( !fi.isFormField () )
        	   //get parameters 
        	   String fieldname = fi.getFieldName();
               if (fieldname.equalsIgnoreCase("name")) name = fi.getString();
               if (fieldname.equalsIgnoreCase("desc")) description = fi.getString();
               if (fieldname.equalsIgnoreCase("access")) access = fi.getString();
               if (fieldname.equalsIgnoreCase("api")) api = fi.getString();
               
               if (fieldname.equalsIgnoreCase("city")) city.add(fi.getString());
               if (fieldname.equalsIgnoreCase("license")) license.add(fi.getString());    
               
               
        	   
           }
        } //  while ( i.hasNext () )
        
        if (state.isEmpty()){	
        
	        Source source = Source.getSourceClassByName(con,name,null);
	        
	        if (source==null){
	    		
	        	Source source2 = new Source(name, description , access, fullPath, new Integer(api));	        	
	        	int id_selec = Source.saveSource(con, source2,null);
	        	
	        	
	    		
	    		if (id_selec > 0){
	    			
	    			//Update the sourcecity table	    			
	    			boolean check = true;
	    			boolean bool;
	    			for (int j=0;j<city.size();j++){
	    				Integer in = new Integer(city.get(j));
		        		SourceCity sourcecity = new SourceCity(id_selec,in);
		        		bool = SourceCity.saveSourceCity(con,sourcecity,null);
		        		if (!bool) check = false;
		        		
	    			}
	    			
	    			if (check){
	    				
	    				//Update the licensesource table	    			
		    			check = true;
		    			
		    			for (int j=0;j<license.size();j++){
		    				Integer in = new Integer(license.get(j));
			        		LicenseSource licensesource = new LicenseSource(id_selec,in);
			        		bool = LicenseSource.saveLicenseSource(con,licensesource,null);
			        		if (!bool) check = false;
			        		
		    			}
		    			
		    			if (check){
		    				error = 0;
			    			state = "Source stored properly";
		    			}else{
		    				error = 1;
			    			state = "There was a problem storing the Source while updating LicenseSource table";
			    			//delete the file. No need to store it
			    			file = new File(fullPath); 
			    			file.delete();
		    				
		    			}
	    				
	    				
	    				
	    				
	    				
	    			}else{
	    				error = 1;
		    			state = "There was a problem storing the Source while updating SourceCity table";
		    			//delete the file. No need to store it
		    			file = new File(fullPath); 
		    			file.delete();
	    				
	    			}
	    			
	    			
	    			
	    		}else{
	    			error = 1;
	    			state = "There was a problem storing the Source";
	    			//delete the file. No need to store it
	    			file = new File(fullPath); 
	    			file.delete();
	    			
	    		}
	    	}else{
	    		error = 1;
	    		state = "Error: The source already exists";  
	    		//delete the file. No need to store it
				file = new File(fullPath); 
				file.delete();
	    	}	
        }
        	
        	
        
    }catch(Exception ex) {
        System.out.println(ex);
        
    }
	
    
    

    
    
    
%>
    
  
  
  <center>
  <div class='main_content'>
    <form name='formulario' method='post' class='texto' enctype='multipart/form-data'>
      <input type='hidden' name='id' value='' />      
      <center><font class='titulo'>Data source</font><br><br></center>
      <table border='0' cellspacing='15'>        
        <tr>
        	<td colspan='2' align='center' class='<% if (error ==1) out.println("msgerror"); else out.println("msgcorrect");   %>'>
        		<%=state%>
        	</td>
        </tr>        
        <tr>
        	<td colspan='2' align='center'>
          		<br>
          		<input type='button'  value='Return' OnClick="location.href='list_sources.jsp'" class='boton'>
        	</td>
        </tr>
      </table>
    </form>
  </div>
  </center>

 <%  OutputDB.disconnectDB(con,null); %>

  

</body>
</html>