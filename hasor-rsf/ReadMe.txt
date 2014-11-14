Hasor-RemoteServiceFramework

Multiplex Group Message Transmission Protocol (MGMTP)

默认情况下  RSF 最多会创建10条线程来协同工作。
	1 条负责处理网络监听的线程。
	2 条负责处理网络IO的线程。
	7 条负责处理服务调用工作的线程。