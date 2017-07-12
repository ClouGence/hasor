create table `TEST_USER_INFO` (
	`id`			          BIGINT			  NOT NULL AUTO_INCREMENT	COMMENT 'UserID（PK，自增）',
	`account`			      VARCHAR(100)	NULL			    	        COMMENT '帐号（唯一）',
	`email`		          VARCHAR(150)	NULL				            COMMENT 'Email',
	`password`			    TEXT	        NOT NULL				        COMMENT '密码(非明文)',
	`nick`			        VARCHAR(100)	NULL					          COMMENT '昵称',

	`create_time`	      DATETIME		  NOT NULL			    	    COMMENT '创建时间',
	`modify_time`	      DATETIME		  NULL					          COMMENT '修改时间',
	PRIMARY KEY (`id`),
	index(`account`),
  index(`email`)
);