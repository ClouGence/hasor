create table `USER_INFO` (
	`id`			          BIGINT			  NOT NULL AUTO_INCREMENT	COMMENT 'UserID（PK，自增）',
	`account`			      VARCHAR(100)	NULL			    	        COMMENT '帐号（唯一）',
	`email`		          VARCHAR(150)	NULL				            COMMENT 'Email',
	`mobile_phone`			VARCHAR(100)	NULL				            COMMENT '电话',
	`password`			    TEXT	        NOT NULL				        COMMENT '密码(非明文)',
	`type`			        INT 	        NOT NULL					      COMMENT '状态',
	
	`nick`			        VARCHAR(100)	NULL					          COMMENT '昵称',
	`gender`			      VARCHAR(10)	  NULL					          COMMENT '用户性别 - 男 女',
	`avatar`	          VARCHAR(200)	NULL		                COMMENT '头像',
	`status`			      INT	          NOT NULL					      COMMENT '状态',
	`login_count`			  BIGINT    	  NOT NULL					      COMMENT '登录次数',
	
	`contact_info`			TEXT	        NULL                    COMMENT '各种联系方式(json格式)',
	`futures`	          TEXT	        NULL                    COMMENT '扩展信息(json格式)',
	
	`first_login_time`	DATETIME		  NULL					          COMMENT '首次登陆时间',
	`last_login_time`	  DATETIME		  NULL					          COMMENT '最后一次登陆时间',
	`create_time`	      DATETIME		  NOT NULL			    	    COMMENT '创建时间',
	`modify_time`	      DATETIME		  NULL					          COMMENT '修改时间',
	PRIMARY KEY (`id`),
	index(`account`),
  index(`email`),
  index(`mobile_phone`),
  index(`status`)
);