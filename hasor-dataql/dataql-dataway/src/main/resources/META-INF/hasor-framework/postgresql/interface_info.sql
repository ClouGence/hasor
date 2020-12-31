create table interface_info (
  api_id          varchar(64)  NOT NULL constraint pk_interface_info primary key,
  api_method      varchar(12)  NOT NULL,
  api_path        varchar(512) NOT NULL,
  api_status      varchar(4)   NOT NULL,
  api_comment     varchar(255) NOT NULL,
  api_type        varchar(24)  NOT NULL,
  api_script      text         NOT NULL,
  api_schema      text         NOT NULL,
  api_sample      text         NOT NULL,
  api_option      text         NOT NULL,
  api_create_time varchar(32)  NOT NULL,
  api_gmt_time    varchar(32)  NOT NULL
);
create unique index uk_interface_info on interface_info (api_path);

comment on table interface_info is 'Dataway 中的API';
comment on column interface_info.api_id is 'ID';
comment on column interface_info.api_method is 'HttpMethod：GET、PUT、POST';
comment on column interface_info.api_path is '拦截路径';
comment on column interface_info.api_status is '状态：-1-删除, 0-草稿，1-发布，2-有变更，3-禁用';
comment on column interface_info.api_comment is '注释';
comment on column interface_info.api_type is '脚本类型：SQL、DataQL';
comment on column interface_info.api_script is '查询脚本：xxxxxxx';
comment on column interface_info.api_schema is '接口的请求/响应数据结构';
comment on column interface_info.api_sample is '请求/响应/请求头样本数据';
comment on column interface_info.api_option is '扩展配置信息';
comment on column interface_info.api_create_time is '创建时间';
comment on column interface_info.api_gmt_time is '修改时间';