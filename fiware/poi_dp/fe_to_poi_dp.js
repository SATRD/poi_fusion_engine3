var POI_DP_BASE_URL, FE_CITYSDK_BASE_URL;
var pois_to_load = [];

function set_progress_bar_text(text){document.getElementById("progress-bar").childNodes[3].innerHTML=text;}
function set_progress_bar_progress(x){document.getElementById("progress-bar").childNodes[1].style.width=document.getElementById("progress-bar").clientWidth*x;}


function start(){
	POI_DP_BASE_URL = document.getElementById("poi-dp-base-url").value;
	FE_CITYSDK_BASE_URL = document.getElementById("fe-citysdk-base-url").value;
	var updater = {
		text: function(text){document.getElementById("progress_text").innerHTML = text;},
		update: function(n,max){set_progress_bar_text(n+"/"+max); if(n==0 && max==0) max=1; set_progress_bar_progress(n/max);},
		onfinish: function(ocdname){document.getElementById("progress_text").innerHTML="";}
	};
	updater.update(0,0);
	var ocdbs = document.getElementById("ocdb-names").value.split(",");
	var ocdb_count = 0;
	for(var i in ocdbs) get_ocd_pois(ocdbs[i].trim(), updater, function(ocdb){
		if(++ocdb_count==ocdbs.length){
			updater.text("Adding pois to poi_dp...");
			var poi_count = 0;
			for(var i in pois_to_load) poi_dp_add_poi(pois_to_load[i], function(){updater.update(++poi_count, pois_to_load.length); if(poi_count==pois_to_load.length){
				updater.text("Done!");
				pois_to_load = [];
			}});
		}
	});
}

function get_ocd_pois(ocdname, up, cb){
	if(up) up.text("Retrieving categories from '"+ocdname+"'...");
	fe_get_categories(ocdname, function(ocd, categories){
		if(up) up.text(categories.length+" categories found in '"+ocd+"'");
		var cat_count = 0;
		for(var i in categories){
			if(up) up.text("Retrieving pois from '"+ocd+"'/'"+categories[i]+"'...");
			fe_get_pois(ocd, categories[i], function(ocd, category, pois){
				for(var j in pois) pois_to_load.push(fe_to_poi_dp(category, pois[j]));
				if(up){ up.text(pois.length+" pois found in '"+ocd+"'/'"+category+"'"); up.update(0,pois_to_load.length);}
				if(++cat_count==categories.length) cb(ocdname)
			});
		}
	});
}

function fe_get_categories(ocd,cb){
	do_get(FE_CITYSDK_BASE_URL+"categories/search?ocdName="+ocd+"&list=poi",function(r,e){
		if(e) console.log(e+": "+r);
		else{
			var cat = [];
			for(var i in r.categories) cat.push(r.categories[i].label[0].value);
			cb(ocd, cat);
		}
	});
}

function fe_get_pois(ocd, category, cb){
	do_get(FE_CITYSDK_BASE_URL+"pois/search?ocdName="+ocd+"&category="+category,function(r,e){
		if(e) console.log(e+": "+r);
		else cb(ocd, category, r.poi);
	});
}

function fe_to_poi_dp(category, poi){
	var latlon = poi.location.point[0].Point.posList.split(",");
	var name; for(var i in poi.label) if(poi.label[i].term=="name" && poi.label[i].source!=poi.label[i].value) name = poi.label[i].value;
	return {
		"fw_core": {
			"category": category, 
			"location": {
				"wgs84": { 
					"latitude": parseFloat(latlon[0].trim()),
					"longitude": parseFloat(latlon[1].trim())
				}
			}, 
			"name": {
				"": name.substring(0,31)
			}
		}
	};
}

function poi_dp_add_poi(poi, cb){
	do_post(POI_DP_BASE_URL+"add_poi.php", poi, function(r,e){if(e) console.log(e+": "+r); else cb();});
}

function do_get(u,cb){
	var xhr = new XMLHttpRequest();
	xhr.open("GET",u,true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200) cb(JSON.parse(xhr.responseText));
			else cb(xhr.responseText,xhr.status);
		}
	}
	xhr.send(null);
}

function do_post(u,o,cb){
	var xhr = new XMLHttpRequest();
	xhr.open("POST",u,true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200) cb(JSON.parse(xhr.responseText));
			else cb(xhr.responseText,xhr.status);
		}
	}
	xhr.send(JSON.stringify(o));
}