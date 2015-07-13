create table RSF_ServiceInfo (
	si_serviceID	BIGINT			NOT NULL,
	si_appID		BIGINT			NOT NULL,
	si_bindID		VARCHAR(300)	NOT NULL,
	si_bindName		VARCHAR(200)	NOT NULL,
	si_bindGroup	VARCHAR(100)	NOT NULL,
	si_bindVersion	VARCHAR(50)		NOT NULL,
	si_bindType		VARCHAR(300)	NOT NULL,
	si_onwer		VARCHAR(50)		NULL,
	si_contactUsers	VARCHAR(200)	NULL,
	si_hashCode		VARCHAR(36)		NOT NULL,
	si_create_time		DATETIME		NULL,
	si_modify_time		DATETIME		NULL,
	PRIMARY KEY (si_serviceID)
);