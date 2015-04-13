#RSF

&emsp;&emsp;一个轻量化的分布式服务框架。典型的应用场景是，将同一个服务部署在多个`Server`上提供分布式的 request、response 消息通知。

### 项目配置

&emsp;&emsp;项目配置参考：[http://git.oschina.net/zycgit/configuration](http://git.oschina.net/zycgit/configuration)

&emsp;&emsp;RoadMap：（详见roadmap.pdf）

----------
### 交流平台

QQ群：**193943114**

issues：[http://git.oschina.net/teams/hasor/issues](http://git.oschina.net/teams/hasor/issues)

Team：[http://team.oschina.net/hasor](http://team.oschina.net/hasor)

----------
### 功能介绍

1.分布式<br/>
&emsp;&emsp;消费者会自动轮询本地可用IP地址表以减少对单一服务提供者的访问压力。

2.接口泛化<br/>
&emsp;&emsp; `Provider` 和 `Consumer`，各自可以是不同的接口定义，不需要统一约束。RSF 的接口泛化会根据调用的服务信息和方法签名自动匹配目标服务。

3.自定义序列化<br/>
&emsp;&emsp;支持自定义序列化，内置 `Hessian`、`Java` 两种序列化方案，默认配置 `Hessian` 版本为 `4.0`。 如需自定义序列化方式只需实现接口即可。

4.同步 or 异步<br/>
&emsp;&emsp;`Consumer` 方面发起 request 消息时可以根据需要选择 `syhc` 或者 `async` 方式。除此之外 RSF 还提供了 `CallBack` 和 `Future` 两种异步请求方式。

5.热装载/热卸载<br/>
&emsp;&emsp;RSF上的服务都是动态的，在 RSF 容器启动之后可以 `随时上线` 服务、也可以 `随时下线` 服务，这一切全由程序自己决定。

6.多版本<br/>
&emsp;&emsp;定位一个RSF服务采用 `Group`、`Name`、`Version` 三个属性，因此一台服务器可以同时发布同一个服务的不同版本。

7.服务拦截器<br/>
&emsp;&emsp;为服务的使用授权、还有调用统计以及本地服务调用优先等提供扩展支持。