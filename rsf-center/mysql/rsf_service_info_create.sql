create table `RSF_ServiceInfo` (
	`si_appID`			BIGINT			NOT NULL 			COMMENT '所属应用ID',
	`si_bindID`			VARCHAR(300)	NOT NULL 			COMMENT '服务ID',
	`si_bindName`		VARCHAR(200)	NOT NULL 			COMMENT '服务名',
	`si_bindGroup`		VARCHAR(100)	NOT NULL 			COMMENT '所属分组',
	`si_bindVersion`	VARCHAR(50)		NOT NULL 			COMMENT '接口版本',
	`si_bindType`		VARCHAR(300)	NOT NULL 			COMMENT '接口类型',
	`si_onwer`			VARCHAR(200)	NULL DEFAULT NULL	COMMENT '接口Owner',
	`si_hashCode`		VARCHAR(36)		NOT NULL 			COMMENT '服务HashCode',
	PRIMARY KEY (`si_appID`,`si_bindID`)
);