--------------------
基于Spring Boot
--------------------

用法
------------------------------------
在 Spring Boot 中只需要一个 ``@EnableHasor`` 注解即可在 Spring 中开启 Hasor 的支持。

.. code-block:: java
    :linenos:

    @EnableHasor
    @SpringBootApplication
    public class ExampleApp {
        public static void main(String[] args) {
            SpringApplication.run(ExampleApp.class, args);
        }
    }

然后新建一个 Hasor 的 Module 并将其用 Spring 管理起来，同时通过 @DimModule 注解标记声明它即可。

.. code-block:: java
    :linenos:

    @DimModule
    @Component()
    public class MyModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            ...
        }
    }

最后启动 Spring Boot 项目看到 HasorBoot 的欢迎信息就表示一切都 OK了。

共享Spring配置
------------------------------------
Hasor 在启动之后会将 Spring 加载的属性文件全部作为 Hasor 的环境变量，例如：application.properties 文件。

Hasor 会自动将Spring 的属性文件导入到环境变量中若想要进一步在 Settings 中使用 Spring 的属性文件可以通过
配置 ``@EnableHasor(useProperties = true)`` 进一步的将环境变量导入到 Settings。

.. code-block:: java
    :linenos:

    Environment environment = appContext.getEnvironment();
    Settings settings = environment.getSettings();
    //
    assert "HelloWord".equals(environment.getVariable("msg"));
    assert "HelloWord".equals(settings.getString("msg")); // 若 useProperties = false，这里获取不到任何值


@EnableHasor 注解
------------------------------------
EnableHasor 注解是 Spring Boot 启动 Hasor 的根本，下面是这个注解的属性说明。

**scanPackages**

用来配置扫描 Module 的范围，一般情况下如果 Module 已经被 Spring 作为 Bean 托管之后就无需在配置扫描范围。
scanPackages 的作用是，用来加载那些还未被 Spring 托管的 Module。 Hasor 在加载这些 Module 的时候会 new 它们。

**mainConfig**

虽然共享 Spring 的配置已经解决了大部分配置文件读取的问题，但有时候还是需要更高级的 hconfig.xml 配置文件。
这个时候就可以通过这个属性来指定 Hasor 的 hconfig.xml。

**useProperties**

Hasor 在启动的时候会将 Spring Environment 中属性信息全部导入到 Hasor Environment 接口中。
``useProperties`` 属性的作用是告诉 Hasor ,是否将 Hasor Environment 接口信息进一步导入到 Settings 接口里。
默认值为 false，表示不导入。

**startWith**

用来声明启动入口。如果配置的启动入口类已经在 Spring 中托管，那么就会通过 Spring 进行创建。否则就直接 new 出这个对象。

**customProperties**

这个属性的意义是可以设定一些特殊的属性K/V信息传递给 Hasor Environment 中。这些特别的属性配置只会在 Hasor 中存在，不会污染 Spring。

@EnableHasorWeb 注解
------------------------------------
Hasor-Web 是一款和 Spring 无关的 WebMVC 框架。它的功能与 SpringMVC 是等价的，都是针对 JavaWeb 开发，同时都具备 Restful 能力。
而 ``EnableHasorWeb`` 注解的功效就是在 SpringWeb 环境中启用 Hasor-Web 能力。更多细节请到 Web 专区了解。

使用 Hasor-web 还需要引入对应的依赖

.. code-block:: java
    :linenos:

    <dependency>
        <groupId>net.hasor</groupId>
        <artifactId>hasor-web</artifactId>
        <version>4.1.7</version>
    </dependency>


这个注解有两个属性配置：

**path**

Hasor-Web 的全局拦截器配置的拦截路径，默认值是： ``/*``

**order**

Hasor 全局拦截器的顺序，默认值是： ``0``

**at**

Hasor-web 在 Spring 中的工作模式，由 ``net.hasor.spring.boot.WorkAt`` 枚举定义。默认是：Filter

- Filter：过滤器模式
- Interceptor：拦截器模式

在 Spring 生态中，SpringMVC 提供了拦截器功能，通过拦截器可以实现诸如权限控制的能力。
但是拦截器和 j2ee 的 过滤器如果同时在 Spring 中出现，过滤器会被优先处理。

这就造成了如果基于 SpringMVC 的拦截器实现权限控制，在整合 Hasor 之后。拦截器无法拦截 Hasor 请求的问题。

这时候可以使用 at 属性来调整 Hasor-web 的工作模式，默认情况下hasor-web 是工作在 Filter 模式下的
