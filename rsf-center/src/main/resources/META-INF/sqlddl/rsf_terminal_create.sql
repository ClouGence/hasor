create table RSF_Terminal (
	ter_terminalID		VARCHAR(100)	NOT NULL,
	ter_terminalSecret	VARCHAR(50)		NOT NULL,
	ter_remoteIP		VARCHAR(50)		NOT NULL,
	ter_remotePort		INT				NOT NULL,
	ter_remoteUnit		VARCHAR(50)		NOT NULL,
	ter_remoteVersion	VARCHAR(50)		NOT NULL,
	PRIMARY KEY (ter_terminalID)
);