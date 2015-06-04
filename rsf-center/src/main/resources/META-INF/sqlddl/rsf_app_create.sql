create table RSF_APP (
	app_id				BIGINT			NOT NULL,
	app_name			VARCHAR(100)	NOT NULL,
	app_accessKey		VARCHAR(50)		NOT NULL,
	app_accessSecret	VARCHAR(128)	NULL,
	app_onwer			VARCHAR(200)	NULL,
	app_desc			VARCHAR(1000)	NULL,
	PRIMARY KEY (app_id)
);