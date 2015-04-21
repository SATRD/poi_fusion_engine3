-- delete tables and sequences

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


DROP SEQUENCE IF EXISTS seq_apitype_id;
DROP SEQUENCE IF EXISTS seq_city_id;
DROP SEQUENCE IF EXISTS seq_component_id;  
DROP SEQUENCE IF EXISTS seq_license_id;
DROP SEQUENCE IF EXISTS seq_ocd_id;
DROP SEQUENCE IF EXISTS seq_source_id;
DROP SEQUENCE IF EXISTS seq_category_id;


--create tables

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


-- create sequences


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




-- Set Primary Keys (PKs) and Unique Keys (UKs) in the sources

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



ALTER TABLE ONLY sourcecity
    ADD CONSTRAINT pk_sourcecity PRIMARY KEY (sourceid, cityid);

ALTER TABLE ONLY category
    ADD CONSTRAINT pk_category PRIMARY KEY (id);
    
ALTER TABLE ONLY categoryrelation
    ADD CONSTRAINT pk_categoryrelation PRIMARY KEY (categoryid1, categoryid2);

ALTER TABLE ONLY ocdcategory
    ADD CONSTRAINT pk_ocdcategory PRIMARY KEY (ocdid, categoryid);
    

-- Set Foreign Keys (FKs)

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
    
    
    

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;



INSERT INTO apitype (name, description, apirules) VALUES ('CitySDK', 'Following CitySDK project and OGC POI SWG', NULL);
INSERT INTO apitype (name, description, apirules) VALUES ('FI-PPP POI API', 'Following FI-PPP project', NULL);



INSERT INTO city (name, bbox) VALUES ('all', '');
INSERT INTO city (name, bbox) VALUES ('tenerife', '27.964418,-16.964231,28.647544,-16.055112');
INSERT INTO city (name, bbox) VALUES ('valencia', '39.445333,-0.426171,39.496086,-0.313046');



INSERT INTO license (name, description, info) VALUES ('ODbl', 'Open Data Commons Open Database License', 'http://opendatacommons.org/licenses/odbl/');
INSERT INTO license (name, description, info) VALUES ('CCAS', 'Creative Commons Attribution-ShareAlike 3.0', 'https://creativecommons.org/licenses/by-sa/3.0/');
INSERT INTO license (name, description, info) VALUES ('GNU Free Doc', 'GNU Free Documentation License', 'http://www.gnu.org/copyleft/fdl.html');
INSERT INTO license (name, description, info) VALUES ('Commercial issues', 'Cannot be used commercially unless there is an agreement ', NULL);
INSERT INTO license (name, description, info) VALUES ('Apache v2', 'Apache License v2 ', 'https://www.apache.org/licenses/LICENSE-2.0');


INSERT INTO category(name,description,level,icon) VALUES('fallas',NULL, 1,NULL);
INSERT INTO category(name,description,level,icon) VALUES('museum',NULL, 1,NULL);
INSERT INTO category(name,description,level,icon) VALUES('monument',NULL, 1,NULL);
INSERT INTO category(name,description,level,icon) VALUES('accommodation',NULL, 1,NULL);
INSERT INTO category(name,description,level,icon) VALUES('tourist_info',NULL, 1,NULL);
INSERT INTO category(name,description,level,icon) VALUES('shop',NULL, 1,NULL);







--INSERT INTO city (id, name, bbox) VALUES (2, 'barcelona', '2.227310,41.468609,2.071920,41.321060');
--INSERT INTO city (id, name, bbox) VALUES (3, 'berlin', '13.08820973,52.34182342,13.76061055,52.66972405');
--INSERT INTO city (id, name, bbox) VALUES (4, 'bretagne', '-1.03933668,47.29674911,-5.13262701,48.87675857');
--INSERT INTO city (id, name, bbox) VALUES (5, 'zurich', '8.605620,47.431870,8.465100,47.322731');
--INSERT INTO city (id, name, bbox) VALUES (6, 'lancaster', '-2.764900,54.071129,-2.834230,54.003422');
--INSERT INTO city (id, name, bbox) VALUES (7, 'colonia', '7.156390,51.085369,6.766590,50.830711');




--INSERT INTO component (id, name) VALUES (1, 'fw_core');
--INSERT INTO component (id, name) VALUES (2, 'fw_media');
--INSERT INTO component (id, name) VALUES (3, 'fw_contact');
--INSERT INTO component (id, name) VALUES (4, 'fw_xml3d');
--INSERT INTO component (id, name) VALUES (5, 'fw_marker');
--INSERT INTO component (id, name) VALUES (6, 'fw_time');
--INSERT INTO component (id, name) VALUES (7, 'fw_relationships');
--INSERT INTO component (id, name) VALUES (8, 'fic2_fusion_tracking');
--INSERT INTO component (id, name) VALUES (9, 'fic2_dynamic_distance');






