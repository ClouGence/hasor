create table interface_release (
  pub_id           varchar(64)    constraint pk_interface_release primary key,
  pub_api_id       varchar(64)    not null,
  pub_method       varchar(12)    not null,
  pub_path         varchar(512)   not null,
  pub_status       varchar(4)     not null,
  pub_comment      varchar(255)   not null,
  pub_type         varchar(24)    not null,
  pub_script       text           not null,
  pub_script_ori   text           not null,
  pub_schema       text           not null,
  pub_sample       text           not null,
  pub_option       text           not null,
  pub_release_time varchar(32)    not null
)
go
exec sp_addextendedproperty 'MS_Description', 'Dataway API 发布记录', 'SCHEMA', 'dbo', 'TABLE', 'interface_release'
exec sp_addextendedproperty 'MS_Description', 'Publish ID', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_id'
exec sp_addextendedproperty 'MS_Description', '所属API ID', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_api_id'
exec sp_addextendedproperty 'MS_Description', 'HttpMethod：GET、PUT、POST', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_method'
exec sp_addextendedproperty 'MS_Description', '拦截路径', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_path'
exec sp_addextendedproperty 'MS_Description', '状态：0有效，1无效（可能被下线）', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_status'
exec sp_addextendedproperty 'MS_Description', '注释', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_comment'
exec sp_addextendedproperty 'MS_Description', '脚本类型：SQL、DataQL', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_type'
exec sp_addextendedproperty 'MS_Description', '查询脚本：xxxxxxx', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_script'
exec sp_addextendedproperty 'MS_Description', '原始查询脚本，仅当类型为SQL时不同', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_script_ori'
exec sp_addextendedproperty 'MS_Description', '接口的请求/响应数据结构', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_schema'
exec sp_addextendedproperty 'MS_Description', '请求/响应/请求头样本数据', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_sample'
exec sp_addextendedproperty 'MS_Description', '扩展配置信息', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_option'
exec sp_addextendedproperty 'MS_Description', '发布时间（下线不更新）', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_release_time'
go

create index idx_interface_release_api on interface_release (pub_api_id)
go
create index idx_interface_release_path on interface_release (pub_path)
go