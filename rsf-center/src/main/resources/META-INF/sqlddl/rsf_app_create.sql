create table RSF_APP (
	app_id				BIGINT			NOT NULL IDENTITY,
	app_code			VARCHAR(100)	NOT NULL,
	app_name			VARCHAR(100)	NOT NULL,
	app_accessKey		VARCHAR(50)		NOT NULL,
	app_accessSecret	VARCHAR(128)	NULL,
	app_onwer			VARCHAR(50)		NULL,
	app_contactUsers	VARCHAR(200)	NULL,
	app_desc			VARCHAR(1000)	NULL,
	app_create_time		DATETIME		NOT NULL,
	app_modify_time		DATETIME		NOT NULL,
);