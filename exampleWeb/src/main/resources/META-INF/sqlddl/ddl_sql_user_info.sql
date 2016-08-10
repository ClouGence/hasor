create table `UserInfo` (
	`app_id`			      BIGINT			  NOT NULL AUTO_INCREMENT	COMMENT '应用ID（PK，自增）',
	`app_code`			    VARCHAR(100)	NOT NULL			    	COMMENT '应用Code（唯一）',
	`app_name`			    VARCHAR(100)	NOT NULL				    COMMENT '程序名称',
	`app_icon`			    VARCHAR(200)	NULL					      COMMENT '图片',
	`app_accessKey`		  VARCHAR(50)		NOT NULL				    COMMENT '授权Key',
	`app_accessSecret`	VARCHAR(128)	NULL DEFAULT NULL		COMMENT '授权密钥',
	`app_onwer`			    VARCHAR(50)		NULL DEFAULT NULL		COMMENT '应用Owner（应用创建人）',
	`app_contactUsers`	VARCHAR(200)	NULL DEFAULT NULL		COMMENT '应用联络人（可以是多人）',
	`app_desc`			    VARCHAR(1000)	NULL					      COMMENT '程序描述',
	`app_create_time`	  DATETIME		  NOT NULL			    	COMMENT '创建时间',
	`app_modify_time`	  DATETIME		  NOT NULL			    	COMMENT '修改时间',
	PRIMARY KEY (`app_id`),
	UNIQUE KEY `UK_RSF_APP_APP_CODE` (`app_code`)
);