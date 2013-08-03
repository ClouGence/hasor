FAQ：
	
	Q: Hasor启动正常，但是为什么我注册的HttpServlet “/business/scene1.do” 没有生效？
	A: 检查日志是否含如下内容片段。
		...
		ServletAnnoSupportModule:loadServlet ->> loadServlet ... bind ... on [ /business/scene1.do ].
		...
		ActionController:init ->> ActionController intercept *.do.
		...
		如果启动日志中包含如上片段则表示配置的HttpServlet “/business/scene1.do”和Action控制器的拦截发生冲突。
	 
	 Q：
	 A：