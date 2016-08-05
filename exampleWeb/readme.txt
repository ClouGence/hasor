exampleWeb 工程说明


== 打包部署
    1. example 项目通过 mvn pacakge 产出 war 包。
    2. 无论是线上、预发、日常、开发机。都使用该 war包,无需替换 war 包中配置文件。
    3. 不同的环境配置通过 env.config 属性文件传递给程序。

== 开发环境配置
    1. src/main/webapp/WEB-INF/daily_home/env.config 为 开发配置。

== 线上环境配置
    1. 地址 : htp://www.hasor.net
    2. conf/tomcat                 为 tomcat 的真实配置。
    3. conf/online_home            为 线上的 WORK_HOME。
    4. conf/online_home/env.config 为 线上配置。

== 线上运行环境
    1. 线上运行在 docker 里, Dockerfile 在工程根目录中。
