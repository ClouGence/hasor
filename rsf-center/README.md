#RSF-Center

&emsp;&emsp;RSF分布式服务框架的注册中心。支持：单机部署，集群部署，嵌入式集成。


----------
### 设计思想

&emsp;&emsp;RSF注册中心的服务注册信息保存在ZooKeeper中，并通过RSF-Center的推送机制来实现服务通知更新。也就是说RSF客户端并不需要直接和ZooKeeper进行通信。
这样做的目的是为了减轻使用RSF的应用程序依赖，同时ZooKeeper相关状态的维护也统一交给Center进行管理。

RSF分布式服务框架专用的，之上的为其专门设计Netty实现对自有协议数据交互的封装，支持高并发、高可靠的分布式RPC框架，设计原理参考了淘宝HSF。



1.leader 的数据清理功能。
2.分布式下，RSF服务事件同步通知到其它Center。
3.RSF各种路由脚本和流控规则的推送和更新
4.CenterContext接口封装。