create table interface_release (
  pub_id           varchar(64)    NOT NULL constraint pk_interface_release primary key,
  pub_api_id       varchar(64)    NOT NULL,
  pub_method       varchar(12)    NOT NULL,
  pub_path         varchar(512)   NOT NULL,
  pub_status       varchar(4)     NOT NULL,
  pub_comment      varchar(255)   NOT NULL,
  pub_type         varchar(24)    NOT NULL,
  pub_script       text           NOT NULL,
  pub_script_ori   text           NOT NULL,
  pub_schema       text           NOT NULL,
  pub_sample       text           NOT NULL,
  pub_option       text           NOT NULL,
  pub_release_time varchar(32)    NOT NULL
);

comment on table interface_release is 'Dataway API 发布记录';
comment on column interface_release.pub_id is 'Publish ID';
comment on column interface_release.pub_api_id is '所属API ID';
comment on column interface_release.pub_method is 'HttpMethod：GET、PUT、POST';
comment on column interface_release.pub_path is '拦截路径';
comment on column interface_release.pub_status is '状态：-1-删除, 0-草稿，1-发布，2-有变更，3-禁用';
comment on column interface_release.pub_comment is '注释';
comment on column interface_release.pub_type is '脚本类型：SQL、DataQL';
comment on column interface_release.pub_script is '查询脚本：xxxxxxx';
comment on column interface_release.pub_script_ori is '原始查询脚本，仅当类型为SQL时不同';
comment on column interface_release.pub_schema is '接口的请求/响应数据结构';
comment on column interface_release.pub_sample is '请求/响应/请求头样本数据';
comment on column interface_release.pub_option is '扩展配置信息';
comment on column interface_release.pub_release_time is '发布时间（下线不更新）';

create index idx_interface_release_api on interface_release (pub_api_id);
create index idx_interface_release_path on interface_release (pub_path);