CREATE TABLE interface_info (                 -- Dataway 中的API
  api_id          TEXT  NOT NULL PRIMARY KEY, -- ID
  api_method      TEXT  NOT NULL,             -- HttpMethod：GET、PUT、POST
  api_path        TEXT  NOT NULL,             -- 拦截路径
  api_status      TEXT  NOT NULL,             -- 状态：-1-删除, 0-草稿，1-发布，2-有变更，3-禁用
  api_comment     TEXT  NOT NULL,             -- 注释
  api_type        TEXT  NOT NULL,             -- 脚本类型：SQL、DataQL
  api_script      TEXT  NOT NULL,             -- 查询脚本：xxxxxxx
  api_schema      TEXT  NOT NULL,             -- 接口的请求/响应数据结构
  api_sample      TEXT  NOT NULL,             -- 请求/响应/请求头样本数据
  api_option      TEXT  NOT NULL,             -- 扩展配置信息
  api_create_time TEXT  NOT NULL,             -- 创建时间
  api_gmt_time    TEXT  NOT NULL              -- 修改时间
);

CREATE UNIQUE INDEX uk_interface_info on interface_info (api_path);