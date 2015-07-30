create table RSF_ServiceInfo (
	si_serviceID	BIGINT			NOT NULL IDENTITY,
	si_appID		BIGINT			NOT NULL,
	si_bindName		VARCHAR(200)	NOT NULL,
	si_bindGroup	VARCHAR(100)	NOT NULL,
	si_bindVersion	VARCHAR(50)		NOT NULL,
	si_bindType		VARCHAR(300)	NOT NULL,
	si_onwer		VARCHAR(50)		NULL,
	si_desc			VARCHAR(1000)	NULL,
	si_contactUsers	VARCHAR(200)	NULL,
	si_hashCode		VARCHAR(36)		NOT NULL,
	si_create_time		DATETIME	NOT NULL,
	si_modify_time		DATETIME	NOT NULL,
	PRIMARY KEY (si_serviceID)
);