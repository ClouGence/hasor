# 分布式 RPC 服务框架

&emsp;&emsp;一个高可用、高性能、轻量级的分布式服务框架。支持容灾、负载均衡、集群。一个典型的应用场景是，将同一个服务部署在多个`Server`上提供 request、response 消息通知。

----------
## 特性
01. 支持服务热插拔：支持服务动态发布、动态卸载
02. 支持服务分组：支持服务分组、分版本
03. 支持多种方式调用：同步、异步、回调、接口代理
04. 支持多种模式调用：RPC模式调用、Message模式调用
        &emsp;&emsp;RPC     模式: 远程调用会等待并返回执行结果。适用于一般方法。遇到耗时方法会有调用超时风险
        &emsp;&emsp;Message 模式: 远程调用当作消息投递到远程机器，不会产生等待，可以看作是一个简单的 MQ。适合于繁重的耗时方法
05. 支持点对点调用。RSF的远程调用可以点对点定向调用，也可以集群大规模部署集中提供同一个服务
06. 支持虚拟机房。通过配置虚拟机房策略可以降低跨机房远程调用
07. 支持泛化调用。简单的理解，泛化调用就是不依赖二方包，通过传入方法名，方法签名和参数值，就可以调用服务
08. 支持隐式传参。可以理解隐式传参的含义为，不需要在接口上明确声明参数。在发起调用的时传递到远端
09. 内置 Telnet 控制台，可以命令行方式直接管理机器
10. 支持 offline/online 动作

## 样例

服务端
```java
public class ProviderServer {
    public static void main(String[] args) throws Throwable {
        AppContext appContext = Hasor.create().addVariable("RSF_SERVICE_PORT","2181").build((RsfModule) apiBinder -> {
            apiBinder.rsfService(EchoService.class).to(EchoServiceImpl.class).register();
        });
        //
        System.out.println("server start.");
        appContext.joinSignal();//阻塞当前线程的继续执行，直到 shutdown 或接收到 kill -15 or kill -2 信号
    }
}
```

客户端
```java
public class CustomerClient {
    public static void main(String[] args) throws Throwable {
        AppContext appContext = Hasor.create().addVariable("RSF_SERVICE_PORT","2171").build((RsfModule) apiBinder -> {
            InterAddress remote = new InterAddress("rsf://localhost:2181/default");
            apiBinder.rsfService(EchoService.class).bindAddress(remote).register();
        });
        //
        System.out.println("client start.");
        RsfClient client = clientContext.getInstance(RsfClient.class);
        EchoService echoService = client.wrapper(EchoService.class);
        for (int i = 0; i < 20; i++) {
            String res = echoService.sayHello("Hello Word for Invoker");
        }
    }
}
```
