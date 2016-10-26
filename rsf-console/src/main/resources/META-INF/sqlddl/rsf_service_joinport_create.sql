create table RSF_ServiceJoinPort (
	sjp_appID			BIGINT			NOT NULL,
	sjp_serviceID		VARCHAR(300)	NOT NULL,
	sjp_terminalID		VARCHAR(100)	NOT NULL,
	sjp_timeout			INT				NOT NULL,
	sjp_serializeType	VARCHAR(50)		NOT NULL,
	sjp_persona			CHAR(1)			NOT NULL,
	PRIMARY KEY (sjp_appID , sjp_serviceID , sjp_terminalID)
);