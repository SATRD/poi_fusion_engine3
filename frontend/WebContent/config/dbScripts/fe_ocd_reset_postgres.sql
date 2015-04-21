

DROP TABLE IF EXISTS ocdsource;
DROP TABLE IF EXISTS sourcecity;
DROP TABLE IF EXISTS ocd;
DROP TABLE IF EXISTS city;
DROP TABLE IF EXISTS licensesource;
DROP TABLE IF EXISTS source;
DROP TABLE IF EXISTS license;
DROP TABLE IF EXISTS component;
DROP TABLE IF EXISTS apitype;
DROP TABLE IF EXISTS categoryrelation;
DROP TABLE IF EXISTS ocdcategory;
DROP TABLE IF EXISTS category;

DROP TABLE IF EXISTS poicategory;
DROP TABLE IF EXISTS poisource;
DROP TABLE IF EXISTS poificontent;
DROP TABLE IF EXISTS poicomponent;
DROP TABLE IF EXISTS poilabel;
DROP TABLE IF EXISTS labeltype;
DROP TABLE IF EXISTS poi;


DROP SEQUENCE IF EXISTS seq_apitype_id;
DROP SEQUENCE IF EXISTS seq_city_id;
DROP SEQUENCE IF EXISTS seq_component_id;  
DROP SEQUENCE IF EXISTS seq_license_id;
DROP SEQUENCE IF EXISTS seq_ocd_id;
DROP SEQUENCE IF EXISTS seq_source_id;
DROP SEQUENCE IF EXISTS seq_category_id;
  

CREATE TABLE apitype (
    id integer NOT NULL,
    name character varying(45) NOT NULL,
    description character varying(255),
    apirules character varying(255)
);


CREATE TABLE city (
    id integer NOT NULL,
    name character varying(45) NOT NULL,
    bbox character varying(127) NOT NULL
);

CREATE TABLE component (
    id integer NOT NULL,
    name character varying(45)
);


CREATE TABLE license (
    id integer NOT NULL,
    name character varying(45) NOT NULL,
    description character varying(255) DEFAULT NULL::character varying,
    info character varying(255)
);


CREATE TABLE licensesource (
    sourceid integer NOT NULL,
    licenseid integer NOT NULL
);


CREATE TABLE ocd (
    id integer NOT NULL,
    name character varying(45) NOT NULL,
    city integer NOT NULL,
    description character varying(255) DEFAULT NULL::character varying,
    fusionrules character varying(255) NOT NULL,
    accesskey character varying(45) DEFAULT NULL::character varying,
    fusiondate date,
    status character varying(45) NOT NULL
);

CREATE TABLE ocdsource (
    ocdid integer NOT NULL,
    sourceid integer NOT NULL
);


CREATE TABLE category (
    id integer NOT NULL,
    name character varying(45) NOT NULL,
    description character varying(255) DEFAULT NULL::character varying,
    level integer NOT NULL,
    icon character varying(255)
);


CREATE TABLE categoryrelation (
    categoryid1 integer NOT NULL,
    categoryid2 integer NOT NULL
);

CREATE TABLE ocdcategory (
    ocdid integer NOT NULL,
    categoryid integer NOT NULL
);

CREATE TABLE source (
    id integer NOT NULL,
    name character varying(45) NOT NULL,
    description character varying(255) DEFAULT NULL::character varying,
    urlaccess character varying(255) NOT NULL,
    categorymapping character varying(255) NOT NULL,
    apitypeid integer NOT NULL
);


CREATE TABLE sourcecity (
    sourceid integer NOT NULL,
    cityid integer NOT NULL
);



CREATE SEQUENCE seq_apitype_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;




CREATE SEQUENCE seq_city_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;



CREATE SEQUENCE seq_component_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


CREATE SEQUENCE seq_license_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;



CREATE SEQUENCE seq_ocd_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;
    
CREATE SEQUENCE seq_category_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;






CREATE SEQUENCE seq_source_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;







ALTER TABLE apitype ALTER COLUMN id SET DEFAULT nextval('seq_apitype_id');
ALTER TABLE city ALTER COLUMN id SET DEFAULT nextval('seq_city_id');
ALTER TABLE component ALTER COLUMN id SET DEFAULT nextval('seq_component_id');
ALTER TABLE license ALTER COLUMN id SET DEFAULT nextval('seq_license_id');
ALTER TABLE ocd ALTER COLUMN id SET DEFAULT nextval('seq_ocd_id');
ALTER TABLE source ALTER COLUMN id SET DEFAULT nextval('seq_source_id');
ALTER TABLE category ALTER COLUMN id SET DEFAULT nextval('seq_category_id');








ALTER TABLE ONLY city
    ADD CONSTRAINT city_name_uk UNIQUE (name);


ALTER TABLE ONLY ocd
    ADD CONSTRAINT ocd_name_uk UNIQUE (name);


ALTER TABLE ONLY apitype
    ADD CONSTRAINT pk_apitype PRIMARY KEY (id);




ALTER TABLE ONLY city
    ADD CONSTRAINT pk_city PRIMARY KEY (id);



ALTER TABLE ONLY component
    ADD CONSTRAINT pk_component PRIMARY KEY (id);


ALTER TABLE ONLY license
    ADD CONSTRAINT pk_license PRIMARY KEY (id);



ALTER TABLE ONLY licensesource
    ADD CONSTRAINT pk_licensesource PRIMARY KEY (sourceid, licenseid);


ALTER TABLE ONLY ocd
    ADD CONSTRAINT pk_ocd PRIMARY KEY (id);


ALTER TABLE ONLY source
    ADD CONSTRAINT pk_source PRIMARY KEY (id);
    
    
ALTER TABLE ONLY ocdsource
    ADD CONSTRAINT pk_ocdsource PRIMARY KEY (ocdid, sourceid);


ALTER TABLE ONLY sourcecity
    ADD CONSTRAINT pk_sourcecity PRIMARY KEY (sourceid, cityid);


ALTER TABLE ONLY category
    ADD CONSTRAINT pk_category PRIMARY KEY (id);
    
ALTER TABLE ONLY categoryrelation
    ADD CONSTRAINT pk_categoryrelation PRIMARY KEY (categoryid1, categoryid2);

ALTER TABLE ONLY ocdcategory
    ADD CONSTRAINT pk_ocdcategory PRIMARY KEY (ocdid, categoryid);
    
    
    
    

ALTER TABLE ONLY licensesource
    ADD CONSTRAINT licensesource_license FOREIGN KEY (licenseid) REFERENCES license(id);



ALTER TABLE ONLY licensesource
    ADD CONSTRAINT licensesource_source FOREIGN KEY (sourceid) REFERENCES source(id);



ALTER TABLE ONLY ocd
    ADD CONSTRAINT ocd_city FOREIGN KEY (city) REFERENCES city(id);




ALTER TABLE ONLY ocdsource
    ADD CONSTRAINT ocdsource_odcid FOREIGN KEY (ocdid) REFERENCES ocd(id);



ALTER TABLE ONLY ocdsource
    ADD CONSTRAINT ocdsource_sourceid FOREIGN KEY (sourceid) REFERENCES source(id);


ALTER TABLE ONLY source
    ADD CONSTRAINT source_apitype FOREIGN KEY (apitypeid) REFERENCES apitype(id);



ALTER TABLE ONLY sourcecity
    ADD CONSTRAINT sourcecity_city FOREIGN KEY (cityid) REFERENCES city(id);



ALTER TABLE ONLY sourcecity
    ADD CONSTRAINT sourcecity_source FOREIGN KEY (sourceid) REFERENCES source(id);

    
ALTER TABLE ONLY categoryrelation
    ADD CONSTRAINT categoryrelation_categoryid1 FOREIGN KEY (categoryid1) REFERENCES category(id);

ALTER TABLE ONLY categoryrelation
    ADD CONSTRAINT categoryrelation_categoryid2 FOREIGN KEY (categoryid2) REFERENCES category(id);

ALTER TABLE ONLY ocdcategory
    ADD CONSTRAINT ocdcategory_categoryid FOREIGN KEY (categoryid) REFERENCES category(id);

ALTER TABLE ONLY ocdcategory
    ADD CONSTRAINT ocdcategory_ocdid FOREIGN KEY (ocdid) REFERENCES ocd(id);    
    
--new to ocd
    
CREATE TABLE poi(	
id integer NOT NULL,
name varchar(128) NOT NULL,
Updated date
); 

CREATE SEQUENCE seq_poi_id
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	START WITH 1
	CYCLE
	OWNED BY poi.id;
ALTER TABLE poi ALTER COLUMN id SET DEFAULT nextval('seq_poi_id');
ALTER TABLE poi ADD CONSTRAINT PK_POI PRIMARY KEY (id);


CREATE TABLE poificontent(	
poiid integer NOT NULL,
ficontentpoiid integer NOT NULL
); 
ALTER TABLE poificontent ADD CONSTRAINT PK_poificontent PRIMARY KEY (poiid);


CREATE TABLE poisource(	
poiid integer NOT NULL,
sourceid integer NOT NULL,
originalref varchar(255)DEFAULT NULL,
poiproxyattribute varchar(255)DEFAULT ''
); 
ALTER TABLE poisource ADD CONSTRAINT PK_poisource PRIMARY KEY (poiid,sourceid,poiproxyattribute);
    

CREATE TABLE poilabel(	
id integer NOT NULL,
poiid integer NOT NULL,
typeid integer NOT NULL,
value varchar(4096),
sourceid integer NOT NULL,
language varchar(45),
licenseid integer DEFAULT NULL,
updated date DEFAULT NULL
); 

CREATE SEQUENCE seq_poilabel_id
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	START WITH 1
	CYCLE
	OWNED BY poilabel.id;
ALTER TABLE poilabel ALTER COLUMN Id SET DEFAULT nextval('seq_poilabel_id');
ALTER TABLE poilabel ADD CONSTRAINT PK_poilabel PRIMARY KEY (id);


CREATE TABLE labeltype(	
Id integer NOT NULL,
Name varchar(45) NOT NULL
); 
CREATE SEQUENCE seq_labeltype_id
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	START WITH 1
	CYCLE
	OWNED BY labeltype.id;
ALTER TABLE labeltype ALTER COLUMN id SET DEFAULT nextval('seq_labeltype_id');
ALTER TABLE labeltype ADD CONSTRAINT PK_labeltype PRIMARY KEY (id);




CREATE TABLE poicomponent(	
poilabelid integer NOT NULL	,
componentid integer NOT NULL
); 

ALTER TABLE poicomponent ADD CONSTRAINT PK_poicomponents PRIMARY KEY (poilabelid,componentid);



CREATE TABLE poicategory(	
poiid integer NOT NULL,
categoryid integer NOT NULL
); 
ALTER TABLE poicategory ADD CONSTRAINT PK_poicategory PRIMARY KEY (poiid,categoryid);




SELECT addGeometryColumn(
	'poi',
	'position',
	4326,
	'POINT',
	2																											
);


CREATE INDEX idx_poi_3 ON poi
USING GIST (
	"position");
	


ALTER TABLE poificontent ADD CONSTRAINT POIFicontent_POIId FOREIGN KEY (poiid) REFERENCES poi(id) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE poisource ADD CONSTRAINT POISource_SourceId FOREIGN KEY (sourceid) REFERENCES source(id) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE poisource ADD CONSTRAINT POISource_POIId FOREIGN KEY (poiid) REFERENCES poi(id) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE poicategory ADD CONSTRAINT POICategory_CategoryId FOREIGN KEY (categoryid) REFERENCES category(id) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE poicategory ADD CONSTRAINT POICategory_POIId FOREIGN KEY (poiid) REFERENCES poi(id) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;


ALTER TABLE poilabel ADD CONSTRAINT POILabel_SourceId FOREIGN KEY (sourceid) REFERENCES source(id) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE poilabel ADD CONSTRAINT POIProperty_POIId FOREIGN KEY (poiid) REFERENCES poi(id) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE poilabel ADD CONSTRAINT POILabel_TypeId FOREIGN KEY (typeid) REFERENCES labeltype(id) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE poilabel ADD CONSTRAINT POILabel_LicenseId FOREIGN KEY (licenseid) REFERENCES license(id) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE poicomponent ADD CONSTRAINT POIComponent_POILabelId FOREIGN KEY (poilabelid) REFERENCES poilabel(id) MATCH SIMPLE ON DELETE NO ACTION ON UPDATE NO ACTION NOT DEFERRABLE INITIALLY IMMEDIATE;









---

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;



