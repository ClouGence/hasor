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
![RsfCenter架构设计](http://project.hasor.net/resources/005201_W9C1_1166271.jpg)

----------
### Demo
	<!-- 引入依赖 -->
	<dependency>
		<groupId>net.hasor</groupId>
		<artifactId>rsf-core</artifactId>
		<version>1.0.0</version>
	</dependency>

	<!-- 配置文件 -->
	<!-- server-config.xml or client-config.xml -->
	<hasor.rsfConfig enable="true" port="9001" console.port="9002" unitName="default">
		<centerServers>
			<server>rsf://center-host:2180</server>
		</centerServers>
	</hasor.rsfConfig>

	//Server
	Hasor.createAppContext("server-config.xml", new RsfModule() {
		public void loadRsf(RsfContext rsfContext) throws Throwable {
			EchoService echoService = new EchoServiceImpl();
			rsfContext.binder().rsfService(EchoService.class).toInstance(echoService).register();
		}
	});

	//Client
	AppContext clientContext = Hasor.createAppContext("client-config.xml", new RsfModule() {
		public void loadRsf(RsfContext rsfContext) throws Throwable {
			rsfContext.binder().rsfService(EchoService.class).register();
		}
	});
	RsfClient client = clientContext.getInstance(RsfClient.class);
	EchoService echoService = client.wrapper(EchoService.class);
	String echoMessage = echoService.sayHello("Hello Word");
	System.out.println(echoMessage);

----------
### 相关连接

* WebSite：[http://www.hasor.net](http://www.hasor.net)
* Issues：[http://git.oschina.net/teams/hasor/issues](http://git.oschina.net/teams/hasor/issues)
* Team：[http://team.oschina.net/hasor](http://team.oschina.net/hasor)
* QQ群：193943114