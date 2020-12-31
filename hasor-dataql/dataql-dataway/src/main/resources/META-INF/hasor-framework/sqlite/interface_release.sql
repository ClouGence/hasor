CREATE TABLE interface_release (              -- Dataway API 发布历史。
  pub_id            TEXT NOT NULL PRIMARY KEY,-- Publish ID
  pub_api_id        TEXT NOT NULL,            -- 所属API ID
  pub_method        TEXT NOT NULL,            -- HttpMethod：GET、PUT、POST
  pub_path          TEXT NOT NULL,            -- 拦截路径
  pub_status        TEXT NOT NULL,            -- 状态：-1-删除, 0-草稿，1-发布，2-有变更，3-禁用
  pub_comment       TEXT NOT NULL,            -- 注释
  pub_type          TEXT NOT NULL,            -- 脚本类型：SQL、DataQL
  pub_script        TEXT NOT NULL,            -- 查询脚本：xxxxxxx
  pub_script_ori    TEXT NOT NULL,            -- 原始查询脚本，仅当类型为SQL时不同
  pub_schema        TEXT NOT NULL,            -- 接口的请求/响应数据结构
  pub_sample        TEXT NOT NULL,            -- 请求/响应/请求头样本数据
  pub_option        TEXT NOT NULL,            -- 扩展配置信息
  pub_release_time  TEXT NOT NULL             -- 发布时间（下线不更新）
);

CREATE INDEX idx_interface_release_api on interface_release (pub_api_id);
CREATE INDEX idx_interface_release_path on interface_release (pub_path);
