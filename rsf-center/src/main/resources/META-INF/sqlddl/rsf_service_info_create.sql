create table RSF_ServiceInfo (
	si_appID		BIGINT			NOT NULL,
	si_bindID		VARCHAR(300)	NOT NULL,
	si_bindName		VARCHAR(200)	NOT NULL,
	si_bindGroup	VARCHAR(100)	NOT NULL,
	si_bindVersion	VARCHAR(50)		NOT NULL,
	si_bindType		VARCHAR(300)	NOT NULL,
	si_onwer		VARCHAR(200)	NULL,
	si_hashCode		VARCHAR(36)		NOT NULL,
	PRIMARY KEY (si_appID,si_bindID)
);