create table `RSF_ServiceJoinPort` (
	`sjp_appID`			BIGINT			NOT NULL COMMENT '所属应用ID',
	`sjp_serviceID`		VARCHAR(300)	NOT NULL COMMENT '服务ID',
	`sjp_terminalID`	VARCHAR(100)	NOT NULL COMMENT '终端ID',
	`sjp_timeout`		INT				NOT NULL COMMENT '终端配置的服务超时时间',
	`sjp_serializeType`	VARCHAR(50)		NOT NULL COMMENT '通讯序列化方式',
	`sjp_persona`		CHAR(1)			NOT NULL COMMENT '终端连接身份(消费者 or 提供者)',
	PRIMARY KEY (`sjp_appID` , `sjp_serviceID` , `sjp_terminalID`)
);