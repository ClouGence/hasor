&emsp;&emsp;在 Hasor 中您可以直接使用 J2EE 的接口实现你想要的功能，然后通过 Hasor 的 Module 将其注册到框架中来。

&emsp;&emsp;通过注册 J2EE 的 Servlet 和 Filter 等常见接口，您可以不需要投入任何框架集成改造。就可以将 Spring、Struts 定一系列经典的 Web 框架集成到 Hasor 中来。

&emsp;&emsp;下面就在本章中介绍一下 Servlet 、Filter 、HttpSessionListener、ServletContextListener 的用法。