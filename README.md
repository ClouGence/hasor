#Land

&emsp;&emsp;项目计划：
1. 分布式协调器
2. 开关
3. 分布式DTS
4. 故障植入
5. 配置中心
6. 限流平台 & 流控平台


##故障植入
1. 故障率
1. RT超时模拟
1. RT增加模拟
1. 调用异常模拟
1. 单点故障
1. 集群单元故障
1. WEB拦截器


----------
### 相关连接

* WebSite：[http://www.hasor.net](http://www.hasor.net)
* Issues：[http://git.oschina.net/teams/hasor/issues](http://git.oschina.net/teams/hasor/issues)
* Team：[http://team.oschina.net/hasor](http://team.oschina.net/hasor)
* QQ群：193943114

### 正式发布

* mvn release:prepare -P release
* mvn deploy -P release