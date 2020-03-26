CREATE TABLE `interface_info` (
    `api_id`          int(11)      NOT NULL AUTO_INCREMENT   COMMENT 'ID',
    `api_method`      varchar(12)  NOT NULL                  COMMENT 'HttpMethod：GET、PUT、POST',
    `api_path`        varchar(512) NOT NULL                  COMMENT '拦截路径',
    `api_status`      int(2)       NOT NULL                  COMMENT '状态：0草稿，1发布，2有变更，3禁用',
    `api_comment`     varchar(255)     NULL                  COMMENT '注释',
    `api_type`        varchar(24)  NOT NULL                  COMMENT '脚本类型：SQL、DataQL',
    `api_script`      mediumtext   NOT NULL                  COMMENT '查询脚本：xxxxxxx',
    `api_schema`      mediumtext       NULL                  COMMENT '接口的请求/响应数据结构',
    `api_sample`      mediumtext       NULL                  COMMENT '请求/响应/请求头样本数据',
    `api_create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `api_gmt_time`    datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`api_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='Dataway 中的API';