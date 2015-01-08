Hasor-RSF（RemoteServiceFramework）

介绍：
	远程服务调用



Multiplex Group Message Transmission Protocol (MGMTP)

默认情况下  RSF 最多会创建10条线程来协同工作。
	1 条负责处理网络监听的线程。
	8 条负责处理网络IO的线程（2xCPU Core）
	4 条负责处理服务调用工作的线程。
	
	
TODO：
	server 端应该避免同一个 requestID 在一个请求响应周期内重复请求（重放攻击）。
	client 处理ChooseOther