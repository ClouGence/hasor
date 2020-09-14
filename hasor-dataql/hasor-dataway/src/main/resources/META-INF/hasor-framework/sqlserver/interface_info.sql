create table interface_info (
  api_id          varchar(64)     constraint pk_interface_info primary key,
  api_method      varchar(12)     not null,
  api_path        varchar(512)    not null,
  api_status      varchar(4)      not null,
  api_comment     varchar(255)    not null,
  api_type        varchar(24)     not null,
  api_script      text            not null,
  api_schema      text            not null,
  api_sample      text            not null,
  api_option      text            not null,
  api_create_time varchar(32)     not null,
  api_gmt_time    varchar(32)     not null
)
go
exec sp_addextendedproperty 'MS_Description', 'Dataway 中的API', 'SCHEMA', 'dbo', 'TABLE', 'interface_info'
exec sp_addextendedproperty 'MS_Description', 'ID', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_id'
exec sp_addextendedproperty 'MS_Description', 'HttpMethod：GET、PUT、POST', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_method'
exec sp_addextendedproperty 'MS_Description', '拦截路径', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_path'
exec sp_addextendedproperty 'MS_Description', '状态：0草稿，1发布，2有变更，3禁用', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_status'
exec sp_addextendedproperty 'MS_Description', '注释', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_comment'
exec sp_addextendedproperty 'MS_Description', '脚本类型：SQL、DataQL', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_type'
exec sp_addextendedproperty 'MS_Description', '查询脚本：xxxxxxx', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_script'
exec sp_addextendedproperty 'MS_Description', '接口的请求/响应数据结构', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_schema'
exec sp_addextendedproperty 'MS_Description', '请求/响应/请求头样本数据', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_sample'
exec sp_addextendedproperty 'MS_Description', '扩展配置信息', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_option'
exec sp_addextendedproperty 'MS_Description', '创建时间', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_create_time'
exec sp_addextendedproperty 'MS_Description', '修改时间', 'SCHEMA', 'dbo', 'TABLE', 'interface_info', 'COLUMN', 'api_gmt_time'
go

create unique index uk_interface_info on interface_info (api_path)
go