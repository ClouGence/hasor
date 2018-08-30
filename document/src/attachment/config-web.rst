Web.xml 环境参数
------------------------------------

Web.xml 环境参数
String rootModule = sc.getInitParameter("hasor-root-module");       // 启动入口
String configName = sc.getInitParameter("hasor-hconfig-name");      // 配置文件名
String envProperties = sc.getInitParameter("hasor-env-properties"); // 环境变量配置