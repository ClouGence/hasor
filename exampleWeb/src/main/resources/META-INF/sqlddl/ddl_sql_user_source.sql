create table `USER_SOURCE` (
	`id`			          BIGINT			  NOT NULL AUTO_INCREMENT	COMMENT 'UserSourceID（PK，自增）',
	`provider`			    VARCHAR(100)  NOT NULL                COMMENT '来源',
	`unique_id`			    VARCHAR(200)	NOT NULL	    	        COMMENT '外部唯一码',
	`status`			      INT	          NOT NULL					      COMMENT '是否有效',
	`login_count`			  BIGINT    	  NOT NULL					      COMMENT '登录次数',
	`user_id`			      BIGINT	      NOT NULL					      COMMENT '关联的UserID',
	`access_info`			  TEXT	        NOT NULL                COMMENT '各种联系方式(json格式)',
	`first_login_time`	DATETIME		  NULL					          COMMENT '首次登陆时间',
	`last_login_time`	  DATETIME		  NULL					          COMMENT '最后一次登陆时间',
	`create_time`	      DATETIME		  NOT NULL			    	    COMMENT '创建时间',
	`modify_time`	      DATETIME		  NULL					          COMMENT '修改时间',
	PRIMARY KEY (`id`),
  index(`status`),
  index(`user_id`),
  unique index(`provider`,`unique_id`),
  unique index(`provider`,`user_id`)
);
