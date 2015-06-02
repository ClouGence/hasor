create table `RSF_APP` (
	`app_id` BIGINT(20) NOT NULL COMMENT '程序ID，唯一',
	`app_name` VARCHAR(50) NOT NULL COMMENT '程序名称',
	`app_accessKey` VARCHAR(50) NOT NULL COMMENT '授权Key',
	`app_accessSecret` VARCHAR(50) NULL DEFAULT NULL COMMENT '授权密钥',
	`app_developers` VARCHAR(50) NULL DEFAULT NULL COMMENT '开发者列表',
	`app_desc` TEXT NULL COMMENT '程序描述'
);