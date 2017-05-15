&emsp;&emsp;在 Hasor Web 中使用 Servlet 如下所示，首先编写我们自己的 HttpServlet，然后将它注册到 Hasor 中：
```java
public class DemoHttpServlet extends HttpServlet{
    protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        ...
    }
}
```

&emsp;&emsp;第一种方式 Api 接口注册 Servlet 的地址。
```java
public class DemoModule extends WebModule{
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        apiBinder.jeeServlet("/your_point.do").with(DemoHttpServlet.class);
    }
}
```

&emsp;&emsp;第二种方式，通过 @MappingTo 注册 Servlet，如下：
```java
@MappingTo("/your_point.do")
public class DemoHttpServlet extends HttpServlet{
    protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        ...
    }
}
```

&emsp;&emsp;扫描所有 @MappingTo
```java
public class DemoModule extends WebModule{
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        apiBinder.scanMappingTo();
    }
}
```
