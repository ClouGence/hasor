create table `RSF_Terminal` (
	`ter_terminalID`		VARCHAR(100)	NOT NULL COMMENT '终端ID',
	`ter_terminalSecret`	VARCHAR(50)		NOT NULL COMMENT '终端授权密钥',
	`ter_remoteIP`			VARCHAR(50)		NOT NULL COMMENT '终端IP',
	`ter_remotePort`		INT				NOT NULL COMMENT '终端端口',
	`ter_remoteUnit`		VARCHAR(50)		NOT NULL COMMENT '终端所处虚拟机房',
	`ter_remoteVersion`		VARCHAR(50)		NOT NULL COMMENT '终端版本',
	PRIMARY KEY (`ter_terminalID`)
);