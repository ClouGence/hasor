create table interface_info (
	api_id          int             identity(0, 1) constraint pk_interface_info primary key,
	api_method      varchar(12)     not null,
	api_path        varchar(512)    not null,
	api_status      int             not null,
	api_comment     varchar(255),
	api_type        varchar(24)     not null,
	api_script      text            not null,
	api_schema      text,
	api_sample      text,
	api_option      text,
	api_create_time datetime       default getdate(),
	api_gmt_time    datetime       default getdate()
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

create unique index idx_interface_info on interface_info (api_method, api_path)
go

create table interface_release (
	pub_id           int            identity(0, 1) constraint pk_interface_release primary key,
	pub_api_id       int            not null,
	pub_method       varchar(12)    not null,
	pub_path         varchar(512)   not null,
	pub_status       int            not null,
	pub_type         varchar(24)    not null,
	pub_script       text           not null,
	pub_script_ori   text           not null,
	pub_schema       text,
	pub_sample       text,
	pub_option       text,
	pub_release_time datetime       default getdate()
)
go
exec sp_addextendedproperty 'MS_Description', 'Dataway API 发布记录', 'SCHEMA', 'dbo', 'TABLE', 'interface_release'
exec sp_addextendedproperty 'MS_Description', 'Publish ID', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_id'
exec sp_addextendedproperty 'MS_Description', '所属API ID', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_api_id'
exec sp_addextendedproperty 'MS_Description', 'HttpMethod：GET、PUT、POST', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_method'
exec sp_addextendedproperty 'MS_Description', '拦截路径', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_path'
exec sp_addextendedproperty 'MS_Description', '状态：0有效，1无效（可能被下线）', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_status'
exec sp_addextendedproperty 'MS_Description', '脚本类型：SQL、DataQL', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_type'
exec sp_addextendedproperty 'MS_Description', '查询脚本：xxxxxxx', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_script'
exec sp_addextendedproperty 'MS_Description', '原始查询脚本，仅当类型为SQL时不同', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_script_ori'
exec sp_addextendedproperty 'MS_Description', '接口的请求/响应数据结构', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_schema'
exec sp_addextendedproperty 'MS_Description', '请求/响应/请求头样本数据', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_sample'
exec sp_addextendedproperty 'MS_Description', '扩展配置信息', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_option'
exec sp_addextendedproperty 'MS_Description', '发布时间（下线不更新）', 'SCHEMA', 'dbo', 'TABLE', 'interface_release', 'COLUMN', 'pub_release_time'
go

create index idx_interface_release on interface_release (pub_api_id)
go