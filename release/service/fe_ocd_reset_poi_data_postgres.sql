
DELETE from poicomponent;
DELETE from component;
DELETE from poilabel;
DELETE from labeltype;

DELETE from poificontent;
DELETE from poicategory;
DELETE from poisource;
DELETE from poi;


ALTER SEQUENCE seq_component_id RESTART WITH 1;
ALTER SEQUENCE seq_poi_id RESTART WITH 1;
ALTER SEQUENCE seq_poilabel_id RESTART WITH 1;
ALTER SEQUENCE seq_labeltype_id RESTART WITH 1;


ALTER TABLE component ALTER COLUMN id SET DEFAULT nextval('seq_component_id');
ALTER TABLE poi ALTER COLUMN id SET DEFAULT nextval('seq_poi_id');
ALTER TABLE poilabel ALTER COLUMN id SET DEFAULT nextval('seq_poilabel_id');
ALTER TABLE labeltype ALTER COLUMN id SET DEFAULT nextval('seq_labeltype_id');

--ALTER SEQUENCE seq RESTART WITH 1;
--UPDATE foo SET id = DEFAULT;
UPDATE poi SET id=DEFAULT;