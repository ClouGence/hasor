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
				<server>rsf://127.0.0.1:2177</server><!-- 注册中心，可以配置多个 -->
			</centerServers>
		</hasor.rsfConfig>
	</config>

	//Server
	Hasor.createAppContext("server-config.xml", new RsfModule() {
	    @Override
	    public void loadRsf(RsfContext rsfContext) throws Throwable {
	        RsfBinder rsfBinder = rsfContext.binder();
	        rsfBinder.rsfService(EchoService.class).toInstance(new EchoServiceImpl()).register();
	    }
	});

	//Client
	AppContext clientContext = Hasor.createAppContext("client-config.xml", new RsfModule() {
	    @Override
	    public void loadRsf(RsfContext rsfContext) throws Throwable {
	        RsfBinder rsfBinder = rsfContext.binder();
	        rsfBinder.rsfService(EchoService.class).register();
	    }
	});
	RsfClient client = clientContext.getInstance(RsfClient.class);
	EchoService echoService = client.wrapper(EchoService.class);
	String res = echoService.sayHello("Hello Word");
	System.out.println(res);

----------
### 交流平台

* QQ群：**193943114**
* Issues：[http://git.oschina.net/teams/hasor/issues](http://git.oschina.net/teams/hasor/issues)
* Team：[http://team.oschina.net/hasor](http://team.oschina.net/hasor)