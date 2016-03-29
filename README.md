#RSF

&emsp;&emsp;一个高可用、高性能、轻量级的分布式服务框架。支持容灾、负载均衡、集群。一个典型的应用场景是，将同一个服务部署在多个`Server`上提供 request、response 消息通知。

&emsp;&emsp;使用RSF可以点对点调用，也可以分布式调用。部署方式上：可以搭配注册中心，也可以独立使用。

----------
### 工作原理
![工作原理](http://project.hasor.net/resources/224933_BV6Q_1166271.jpg)

----------
### RSF架构设计
![RSF架构](http://project.hasor.net/resources/002011_mz60_1166271.jpg)

----------
### Center架构设计
![RsfCenter架构设计](http://project.hasor.net/resources/002011_mz60_1166271.jpg)

----------
### 特性
01. 支持服务动态发布、动态卸载。
02. 支持服务分组、分版本。
03. 支持四种调用方式：同步、异步、回调。
04. 支持点对点定向通信，也可以集群大规模部署集中提供同一个服务。
05. 支持虚拟机房。通过配置虚拟机房策略可以降低跨机房远程调用。
06. 支持QoS流量控制。流控可以精确到：接口、方法、地址。
07. 支持动态路由脚本。路由可以精确到：接口、方法、参数。
08. 支持泛化调用。简单的理解，泛化调用就是不依赖二方包，通过传入方法名，方法签名和参数值，就可以调用服务。
09. 支持服务地址缓存。当应用启动或重启时，自动尝试恢复服务的提供者地址列表。再也不用担心注册中心挂掉的问题。
10. 支持临时冻结失效地址。当某个地址失效之后，RSF会冻结一段时间，在这段时间内不会有请求发往这个地址。
11. 支持自定义序列化。默认使用内置 Hessian 4.0.7 序列化库。
12. 支持请求、响应分别使用不同序列化规则。
13. 支持调用拦截器RsfFilter。
14. 支持调用之外的信息通过选项发送给远端，或者由远端响应给调用方。
15. IO线程、调用线程分离式设计。
16. Rsf注册中心和客户机之间双向校验。
17. 支持优雅上下线。
18. 最小依赖：所有功能仅依赖，hasor.jar、netty.jar、groovy.jar 三个JAR包(包名忽略版本)。

--应用程序保护--

01. 调用线程参数设置（队列容量、线程数）。
02. 监听线程数、Worker线程数设置。
03. 默认最大发起请求数限制。
04. 默认最大发起请求超限制策略设置（等待1秒重试、抛异常）。
05. 请求超时设置。
06. RSF在启动链接注册中心之后，会根据授权码来校验来自Center的指令。通过校验码来防止恶意伪装RsfCenter的攻击。

----------
### Demo
	<!-- 引入依赖 -->
	<dependency>
		<groupId>net.hasor</groupId>
		<artifactId>rsf-core</artifactId>
		<version>1.0.0-SNAPSHOT</version>
	</dependency>

	<!-- server-config.xml or client-config.xml -->
	<config xmlns="http://project.hasor.net/hasor/schema/main">
		<!-- 如果在一台机器上同时运行提供者和消费者，那么请为两个程序分别指定不同的 port端口号 -->
		<hasor.rsfConfig enable="true" address="127.0.0.1" port="8000">
			<centerServers>
				<!-- 注册中心 -->
				<server>rsf://127.0.0.1:2177</server>
			</centerServers>
		</hasor.rsfConfig>
	</config>

	public class RsfProviderServer {
	    public static void main(String[] args) throws Throwable {
	        //Server
	        Hasor.createAppContext("server-config.xml", new RsfModule() {
	            @Override
	            public void loadRsf(RsfContext rsfContext) throws Throwable {
	                RsfBinder rsfBinder = rsfContext.binder();
	                rsfBinder.rsfService(EchoService.class).toInstance(new EchoServiceImpl()).register();
	            }
	        });
	        //
	        System.out.println("server start.");
	        Thread.sleep(10000);
	    }
	}

	public class RsfCustomerClient {
	    public static void main(String[] args) throws Throwable {
	        //Client
	        AppContext clientContext = Hasor.createAppContext("client-config.xml", new RsfModule() {
	            @Override
	            public void loadRsf(RsfContext rsfContext) throws Throwable {
	                RsfBinder rsfBinder = rsfContext.binder();
	                rsfBinder.rsfService(EchoService.class).register();
	            }
	        });
	        System.out.println("server start.");
	        //
	        //Client -> Server
	        RsfClient client = clientContext.getInstance(RsfClient.class);
	        EchoService echoService = client.wrapper(EchoService.class);
	        String res = echoService.sayHello("Hello Word");
	        System.out.println(res);
	    }
	}

----------
### 交流平台

* QQ群：**193943114**
* Issues：[http://git.oschina.net/teams/hasor/issues](http://git.oschina.net/teams/hasor/issues)
* Team：[http://team.oschina.net/hasor](http://team.oschina.net/hasor)