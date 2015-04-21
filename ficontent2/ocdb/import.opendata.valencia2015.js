/*******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright 2014 Fraunhofer FOKUS
 *******************************************************************************/
require('mongoose');


var _ = require("underscore");    //underscore.js is a library that provides several functions (http://underscorejs.org/)
var http = require("http");

var Media = require('../../models.js').model.Media;
var Poi = require('../../models.js').model.Poi;
var City = require('../../models.js').model.City;
var SocialAttributes = require('../../models.js').model.SocialAttributes;

var id_city; //id of Valencia city stored in MongoDB
var APIBASE = "http://158.42.188.78:8080/fic2_fe_inquiry_valencia/citysdk/pois/search?category=";

var categories = [ "fallas"/*, "museum", "monument", "accommodation"*/ ];

var category;
var APIURL; 

var poiid;
var mediaid;

var execcmd = true;



//Main method
if(execcmd) {
	
	//var finished = _.after(1,importPOIs);




	//Find '_id' of the city 'Valencia /Valéncia' already in the OCDB. Curiously there are 3 cities named Valencia
	City.find( {displayName: new RegExp(/.*valencia \/.*/i)},function(err, cursor){
	
		id_city = cursor[0]._id;
		console.log("Found Valencia city with id: "+id_city);
		for(var i = 0; i < categories.length;i++){
			category = categories[i];
			console.log("Importing POIs from category: "+category);
			APIURL = APIBASE+category;
			importPOIs(category);
		}		
		//finished();
	});
	


execcmd = false;
}

function importPOIs(category){


    http.get(APIURL, function(res) {   
    var result = '', length=0;

    res.on('data', function(chunk) {
      length += chunk.length;
      process.stdout.write("Downloading " + length + " bytes\r");
        result += chunk;
    });

    res.on('end', function() {
        var datares = JSON.parse(result);
       

        _.each(datares.poi,function(r){
          var poi = {};
          var media ={};
          var medialink ={};
   	  
 	  var coordis = (r.location.point[0].Point.posList).split(",");	 
          var id=r.id;
	  
          poi.coords = [parseFloat(coordis[1]),parseFloat(coordis[0])];                   	
          
	  var label = r.label;
	  var name = "";
	  var description ="";

	  //initially set a link to a 'no image' picture
          var medialink = ["https://farm9.staticflickr.com/8599/16532916086_a8f33cb594_q_d.jpg","https://farm9.staticflickr.com/8599/16532916086_7f289d1641_o_d.png"];

	
	 if (category == "fallas"){
		 //This will allow insert the extra attributes for the fallas POI
		 var presidente="";
		 var seccion ="";
		 var lema="";
		 var artista="";
		 var faller="";
		 var official_img_n= "https://farm9.staticflickr.com/8599/16532916086_7f289d1641_o_d.png";
		 var official_img_t = "https://farm9.staticflickr.com/8599/16532916086_a8f33cb594_q_d.jpg";
		 var originalRef="";
	}


	if (category == "monument"){
		//This will allow insert the extra attributes for the fallas POI
		 var telefono="";
		 var ruta ="";
		 var numpol="";
		 var codvia="";		 
	}
	
	//Contact information 
	var address= "";
	var phone = "";
	var link ="";
	var email="";
	  	 
 	  for (i = 0; i < label.length; i++) {
		var item = label[i];
		if (item.term == "name"){ name = item.value; }
		if (item.term =="link"){ 
			// Do nothing. Skip bocetos as they are not going to be shown			
			medialink = [ item.value];
			if (category == "fallas"){
				 official_img_n = medialink;
				 official_img_t = medialink;
			}
		}
		if (item.term =="description"){ description = item.value;}
		
		  
		if (category == "fallas"){
			if (item.term == "presidente"){ presidente = item.value; }
			if (item.term == "seccion"){ seccion = item.value; }
			if (item.term == "lema"){ lema = item.value; }
			if (item.term == "faller"){ faller = item.value; }
			if (item.term == "artista"){ artista = item.value; }
			if (item.term == "originalRef"){originalRef = item.value; }
		}

		if (category == "monument"){
			if (item.term == "telefono"){ telefono = item.value; }
			if (item.term == "ruta"){ ruta = item.value; }
			if (item.term == "numpol"){ numpol = item.value; }
			if (item.term == "codvia"){ codvia = item.value; }		
		}

	  }

	 	 

	  
	  poi.source = APIBASE+"poiid="+id;
	  	  
          poi.name =  name;   
	  poi.public = true,
	  
 	  poi.description = description;  

	  poi.tags = [ category ];
          
          poi.city = {"refurl": "cities/"+id_city, "_city":id_city};

	  if (r.location.address.value) address= r.location.address.value ;
	  poi.contact ={ "address": address, "phone": phone, "link": link, "email": email };

	 
						
				
	  var sa = new SocialAttributes();
	  sa.save();
	  poi.socatt = sa._id;
	   
                     
          new Poi(poi).save(function(err,poiInserted) {
		  if(err){
			 console.log('error saving #' + poi.name);
		  }else{
			poiid = poiInserted._id;			 
			//console.log("saved #" + poi.name + " id: "+poiid);

		 


			//update POI with the media
			 
			
			var mediaObj = [{
                
				"title": "boceto",
				"type": "image",
				"featured": true,				
				"coords": poi.coords,
				"timestamp": Math.floor(new Date().getTime() / 1000) ,
				"__v": 0,
				"poi": {
				    "refurl": poi.source,
				    "_poi": poiid
				  },
				"url": [ medialink]
			    }];	


			
			if (category == "fallas"){

				//Store the media
				//Do not store media for the moment
			
				var mediaObjects = _.map(mediaObj, function(v) { var m = new Media(v); m.save(); return m; } );
			
			
				//Model.update(conditions, update, options, callback);
				//Assign this  media item to the POI			
				Poi.update({_id: poiid},{$pushAll: { 'media._media': mediaObjects }, $inc: { 'media.mediaCount': mediaObjects.length } },
					{upsert: true},
					function(e,r){
						if(!e && r) {
							//do nothing
							//console.log("# media saved and assigned to poi");	
						} else {
							console.log(e);						
						}
					}
				);
			}
			

			
			
			if (category == "fallas"){
				var attr = { 
					"presidente": presidente,
		 			"seccion": seccion,
		 			"lema": lema,
		 			"artista": artista,
		 			"faller":faller,
					"official_img_n": official_img_n,
					"official_img_t": official_img_t,
					"original_ref": originalRef

				};
			}

			if (category == "monument"){
				var attr = { 
					"telefono": telefono,
	 				"ruta": ruta,
	 				"numpol": numpol,
	 				"codvia": codvia	 			
				};			
			
			}

			
						
			if ((category == "fallas") || (category =="monument"  )){
			
				Poi.update({_id: poiid},{'attributes': attr },
					{upsert: true},
					function(e,r){
						if(!e && r) {
							//do nothing
							//console.log("# attributes saved and assigned to poi");	
						} else {
							console.log(e);						
						}
					}
				);
			}		
				
		  }
          })
	 
        });

        setTimeout(function(){
          process.exit(0);
        },10000);
    });
  }).on('error', function(e) {
    console.log("Open data API error: ",e);
    process.exit(1);
  });
  
}











