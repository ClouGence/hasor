&emsp;&emsp;在 Hasor Web 中使用 ServletContextListener 如下所示，首先编写我们自己的 ServletContextListener，然后将它注册到 Hasor 中：
```java
public class MyServletContextListener implements ServletContextListener {
    ...
}
```

&emsp;&emsp;然后将其注册到 Hasor 框架中：
```java
public class StartModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        ...
        apiBinder.addServletListener(MyServletContextListener.class);
        ...
    }
}
```