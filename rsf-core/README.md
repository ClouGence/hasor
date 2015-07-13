#RSF-Core

&emsp;&emsp;一个轻量化的分布式服务框架。典型的应用场景是，将同一个服务部署在多个`Server`上提供分布式的 request、response 消息通知。RSF是 `RemoteServiceFramework` 的缩写。

----------

&emsp;&emsp;该项目是RSF核心项目，RSF的所有核心代码都在这里。使用 `RSF-Core` 可以建立分布式服务，也可以作为两个单点之间 RPC 通信。

===== Hasor-RSF v0.0.1
* 新增:
后续要做的：
ChooseOther机制
 1.失效地址定时清理
 3.流控沉降到Service层面，目前流控是整个app。
 4.支持接口Mock数据。
 5.多网卡客户端向注册中心注册问题