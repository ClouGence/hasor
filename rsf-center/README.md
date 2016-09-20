#RSF-Center

&emsp;&emsp;RSF分布式服务框架的注册中心。支持：单机部署，集群部署，嵌入式集成。工作模式上支持（alone、master、slave）三种部署方式。
此外您还可以通过RsfCenter Telnet控制台管理服务，路由&流控策略。

&emsp;&emsp;RSF注册中心的服务注册信息保存在ZooKeeper中，并通过RSF-Center的推送机制来实现服务通知更新。也就是说RSF客户端并不需要直接和ZooKeeper进行通信。
这样做的目的是为了减轻使用RSF的应用程序依赖，同时ZooKeeper相关状态的维护也统一交给Center进行管理。

&emsp;&emsp;RSF分布式服务框架专用的，之上的为其专门设计Netty实现对自有协议数据交互的封装，支持高并发、高可靠的分布式RPC框架，设计原理参考了淘宝HSF。

----------
### 工作原理
!!!!!!

----------
### 架构设计
![RsfCenter架构设计](http://project.hasor.net/resources/005201_W9C1_1166271.jpg)

----------
### 特性
01. 支持服务动态发布、动态卸载。

----------
### 后续优化
1. RSF校验机制
2. 集群下事件分发机制
3. ZK启动稳定性能。
4. 注册中心指令。
5. 心跳服务请求相应优化
1. leader 的数据清理功能。
2. 分布式下，RSF服务事件同步通知到其它Center。
3. RSF各种路由脚本和流控规则的推送和更新
4. CenterContext接口封装。


最低要求:jdk8