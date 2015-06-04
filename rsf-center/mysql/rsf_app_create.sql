create table `RSF_APP` (
	`app_id`			BIGINT			NOT NULL			COMMENT '应用程序ID',
	`app_name`			VARCHAR(100)	NOT NULL			COMMENT '程序名称',
	`app_accessKey`		VARCHAR(50)		NOT NULL			COMMENT '授权Key',
	`app_accessSecret`	VARCHAR(128)	NULL DEFAULT NULL	COMMENT '授权密钥',
	`app_onwer`			VARCHAR(200)	NULL DEFAULT NULL	COMMENT '应用Owner',
	`app_desc`			VARCHAR(1000)	NULL				COMMENT '程序描述',
	PRIMARY KEY (`app_id`)
);