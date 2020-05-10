create table interface_info (
	api_id          number(8)       generated as identity constraint pk_interface_info primary key,
	api_method      varchar(12)     NOT NULL,
	api_path        varchar(512)    NOT NULL,
	api_status      number(8)       NOT null,
	api_comment     varchar(255)        NULL,
	api_type        varchar(24)     NOT null,
	api_script      clob            NOT NULL,
	api_schema      clob                NULL,
	api_sample      clob                NULL,
	api_option      clob                NULL,
	api_create_time timestamp           default sysdate,
	api_gmt_time    timestamp           default sysdate
)
/comment on column interface_info.api_id is 'ID'
/comment on column interface_info.api_method is 'HttpMethod：GET、PUT、POST'
/comment on column interface_info.api_path is '拦截路径'
/comment on column interface_info.api_status is '状态：0草稿，1发布，2有变更，3禁用'
/comment on column interface_info.api_comment is '注释'
/comment on column interface_info.api_type is '脚本类型：SQL、DataQL'
/comment on column interface_info.api_script is '查询脚本：xxxxxxx'
/comment on column interface_info.api_schema is '接口的请求/响应数据结构'
/comment on column interface_info.api_sample is '请求/响应/请求头样本数据'
/comment on column interface_info.api_option is '扩展配置信息'
/comment on column interface_info.api_create_time is '创建时间'
/comment on column interface_info.api_gmt_time is '修改时间'
/

create table interface_release (
    pub_id           number(8)      generated as identity constraint pk_interface_release primary key,
    pub_api_id       number(8)      NOT NULL,
    pub_method       varchar(12)    NOT NULL,
    pub_path         varchar(512)   NOT NULL,
    pub_status       number(8)      NOT null,
    pub_type         varchar(24)    NOT null,
    pub_script       clob           NOT NULL,
    pub_script_ori   clob           NOT NULL,
    pub_schema       clob               NULL,
    pub_sample       clob               NULL,
    pub_option       clob               NULL,
    pub_release_time timestamp      default sysdate
)
/comment on column interface_release.pub_id is 'Publish ID'
/comment on column interface_release.pub_api_id is '所属API ID'
/comment on column interface_release.pub_method is 'HttpMethod：GET、PUT、POST'
/comment on column interface_release.pub_path is '拦截路径'
/comment on column interface_release.pub_status is '状态：0有效，1无效（可能被下线）'
/comment on column interface_release.pub_type is '脚本类型：SQL、DataQL'
/comment on column interface_release.pub_script is '查询脚本：xxxxxxx'
/comment on column interface_release.pub_script_ori is '原始查询脚本，仅当类型为SQL时不同'
/comment on column interface_release.pub_schema is '接口的请求/响应数据结构'
/comment on column interface_release.pub_sample is '请求/响应/请求头样本数据'
/comment on column interface_release.pub_option is '扩展配置信息'
/comment on column interface_release.pub_release_time is '发布时间（下线不更新）'
/
create index idx_interface_release on INTERFACE_RELEASE (pub_api_id)
/