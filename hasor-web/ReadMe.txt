Hasor-Web

介绍：
	Hasor-Web 是Hasor针对Web开发而封装的一套开发接口，该项目依赖于 Hasor-Core。使用 Hasor-Web
	可以通过编码形式动态注册 Servlet/Filter ，Hasor-Web 为它们建立了统一的 Dispatcher。 
	使用Hasor-Web需要继承 WebModule 类，或者参照该类进行仿制。 guice-servlet是它的技术雏形。