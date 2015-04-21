<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*,java.util.*,org.json.*,com.google.gson.*  " %>
<%@ page import="org.upv.satrd.fic2.fe.config.*,org.upv.satrd.fic2.fe.main.*,java.sql.*, org.upv.satrd.fic2.fe.db.*,java.util.ArrayList, java.text.*,java.util.HashMap" %>
<%@ page import="org.apache.log4j.Logger" %>
<% Logger log = Logger.getLogger("JSP.right");%>

<% 
	



	String[] categoryList = request.getParameterValues("category");
	String ocdName = request.getParameter("ocdName");
	Integer limit = 400;
    

	//Load configuration parameters of the OCD in order to access the OutputDB database
	String relativeWebPath = "/config/config.xml";
	String configPath = getServletContext().getRealPath(relativeWebPath);
	Configuration conf = new Configuration(configPath);
	
	OutputDB.setConfiguration(configPath);
	
	conf.setDBName("ocd_"+ocdName);
	
	//Get Connection			
	Connection con = OutputDB.connectDB(
		conf.getConnectionString(),
		conf.getUser(),
		conf.getPwd(),
		conf.getDriverName(),null);	

	//Check ocd status
	OCD ocd = OCD.getOCDClassByName(con,ocdName,null);
	
	//Get POI List
	ArrayList<POI> poiList = new ArrayList<POI>();
	
	
	if ( (categoryList!=null) && (ocd.getStatus().equalsIgnoreCase(OCD.STATUS_FINISHED_OK)) )  {
		
		
		for (int p=0;p<categoryList.length;p++){
			
			String cat = categoryList[p];
			
			ArrayList<POI> small_poiList = POI.getPOIListByCategoryName(con, cat, limit,null);   
			if ( (small_poiList!=null) && (!small_poiList.isEmpty()) ){				
				poiList.addAll(small_poiList);
			}
			
			
		}
	}else{
		out.println("error");
	}
	
	//ArrayList<POI> poiList = new ArrayList<POI>();
	//poiList = POI.getPOIList(con, null);
	

%>
		
<!DOCTYPE html>
<html>
  <head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <style>
      html { height: 100% }
      body { height: 100%; margin: 0; padding: 0 }
      #map_canvas { height: 100% }
    </style>
    <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCro2G_rFQO5hNve56fBNdbY30IMNsZvS4&sensor=false&language=es"></script>

     

    <script type="text/javascript">

      function initialize() {

        
        <%
        //Get the city bbox
        //ArrayList<City> cityList = City.getCityList(con,null);
        City city = City.getCityClassById(con, ocd.getCityId(), null);
        if (city!= null) {
        	//City city = cityList.get(0);
        	String bbox= city.getBbox();
        	String[] geospatial = bbox.split(",");
        	Double ymin= new Double(geospatial[0]);
        	Double xmin = new Double(geospatial[1]);
        	Double ymax= new Double(geospatial[2]);
        	Double xmax = new Double(geospatial[3]);
        	
        	Double lat_middle = (ymin+ymax)/2;
        	Double lon_middle = (xmin+xmax)/2;
        	
        	out.println("var centro = new google.maps.LatLng("+lat_middle+","+lon_middle+");");
        	
        }        		
       %> 		
        //var centro = new google.maps.LatLng(28.23,-16.40);
          
        
        <%
        	for (int i=0;i<poiList.size();i++){
        		POI poi = poiList.get(i);
        		Double lat = poi.getLatitude();
        		Double lon = poi.getLongitude();
        		out.println("var p"+i+"=new google.maps.LatLng("+lat+","+lon+");");        		
        	}
        
        %>

        var mapOptions = {
          center: centro,
          zoom: <%if (ocdName.equalsIgnoreCase("valencia_demo")) out.println("13"); else out.println("10");%>,

          disableDefaultUI: false,       //disable all
          panControl: false,             //upper arrows
          rotateControl: false,          //rotation
          zoomControl: true,             //zoom bar 
          mapTypeControl: true,          //map type 
          mapTypeControlOptions: {
           style: google.maps.MapTypeControlStyle.DEFAULT
          },
          scaleControl: true,            //down scale
          streetViewControl: true,       //Street view
          overviewMapControl: false,     //Mini map

          mapTypeId: google.maps.MapTypeId.ROADMAP
        };

        var map = new google.maps.Map(document.getElementById('map_canvas'), mapOptions);

               
        
        <%	for (int i=0;i<poiList.size();i++){  
        		POI poi = poiList.get(i);
        		String name=poi.getName();
        		name = name.replace("\"", "'");
        %>
    		
	    		var marker<%out.print(i);%> = new google.maps.Marker({
	    	          position: p<%out.print(i);%>,
	    	          map: map,
	    	          draggable:false,
	    	          animation: google.maps.Animation.DROP,
	    	          //icon: grey,
	    	          title:"<%=name%>"
	    	        });
    		
    	<% }  %>

        

        //// Boton home
        function HomeControl(controlDiv, map) {

          // Set CSS styles for the DIV containing the control
          // Setting padding to 5 px will offset the control
          // from the edge of the map
          controlDiv.style.padding = '5px';

          // Set CSS for the control border
          var controlUI = document.createElement('div');
          controlUI.style.backgroundColor = 'white';
          controlUI.style.borderStyle = 'solid';
          controlUI.style.borderWidth = '2px';
          controlUI.style.cursor = 'pointer';
          controlUI.style.textAlign = 'center';
          controlUI.title = 'Click to set the map to Home';
          controlDiv.appendChild(controlUI);

          // Set CSS for the control interior
          var controlText = document.createElement('div');
          controlText.style.fontFamily = 'Arial,sans-serif';
          controlText.style.fontSize = '12px';
          controlText.style.paddingLeft = '4px';
          controlText.style.paddingRight = '4px';
          controlText.innerHTML = '<b>Home</b>';
          controlUI.appendChild(controlText);

          // Setup the click event listeners: simply set the map to home
          google.maps.event.addDomListener(controlUI, 'click', function() {
            map.setCenter(centro),
            map.setZoom(17)
          });

        }
        var homeControlDiv = document.createElement('div');
        var homeControl = new HomeControl(homeControlDiv, map);
        homeControlDiv.index = 1;
        map.controls[google.maps.ControlPosition.TOP_RIGHT].push(homeControlDiv);


        //// Windows 

        <%
        	for (int i=0;i<poiList.size();i++){  
        		
        		POI poi = poiList.get(i);
        		ArrayList<POILabel> poilabelList = POILabel.getPOILabelListByPOIid(con, poi.getId(),null);
        		
        		ArrayList <String> content = new ArrayList<String>();
        		String name = poi.getName();
        		name = name.replace("\"", "'");
        		//name = name.replace("\'", " ");
                //name = name.replace("Ó", "O");
        		
        %>
        		     		
        google.maps.event.addListener(marker<%out.print(i);%>, 'click', function() {
            infowindow<%out.print(i);%>.open(map, marker<%out.print(i);%>);
          });
        
        var contentString<%out.print(i);%> = "<div><table style='border: 1px solid black;'>"+
        "<tr><td>TERM</td><td>VALUE</td></tr>"+
        "<tr><td>name</td><td><%out.print(name);%></td></tr>"+
        "<tr><td>latitude</td><td><%out.print(poi.getLatitude());%></td></tr>"+
        "<tr><td>longitude</td><td><%out.print(poi.getLongitude());%></td></tr>"+        
		        "<% for (int k=0;k<poilabelList.size()-2;k++){
					POILabel poilabel = poilabelList.get(k);
					String term = LabelType.getLabelTypeClassById(con, poilabel.getLabelTypeId(),null).getName();
					//term = term.replace("ïz½a", "ía");
					//term = term.replace("ïz½", "ó");
					//term = term.replace("\"", "'");
					String labval = poilabel.getValue();
					
					labval = labval.replace("ïz½a", "ía");
					labval = labval.replace("ïz½", "ó");
					labval = labval.replace("\"", "'");
					labval = labval.replace("S/N", "SN");
					if (labval.contains("SN")) labval="SN";
					//name = name.replace("\'", " ");
                    //name = name.replace("Ó", "O");
					
					//exclude the name and position from poilabel
					if ( (!term.equalsIgnoreCase("name"))  && (!term.equalsIgnoreCase("position"))   ) {
						out.print("<tr><td>"+term+"</td><td>"+labval+"</td></tr>");  
					}
				}%>"+				
        "<tr><td></td><td></td></tr>"+
        "</table></div>" ;
        
        var infowindow<%out.print(i);%> = new google.maps.InfoWindow({
            content: contentString<%out.print(i);%>
          });
        	
        	
        <%	} %>
        
        
      }

      google.maps.event.addDomListener(window, 'load', initialize);

    </script>
  </head>
  <body bgcolor="#f9e283">
  	<p>&nbsp;&nbsp; Found <%out.println(poiList.size()-2); %>POIS from selected categories </p>
    <div id="map_canvas"></div>
  </body>
</html>
<% OutputDB.disconnectDB(con, null);  %>