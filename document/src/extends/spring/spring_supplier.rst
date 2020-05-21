委托 Spring Bean 管理
------------------------------------

当 Hasor 遇上 Spring 最让人关心的就是 Bean 管理问题。
Hasor 和 Spring 各有一套 Bean 容器，而在架构中通常只会使用一套 Bean 容器。
Spring 通常是作为主力开发框架，Hasor 的某些功能会作为补充协助开发例如 Dataway。这时候就需要让 Bean 的管理委托给 Spring。

把 Bean 委托给 Spring 需要通过 `TypeSupplier` 把创建过程委托出去。

例如：

.. code-block:: java
    :linenos:

    public class MyModule implements WebModule, SpringModule {
    public void loadModule(WebApiBinder apiBinder) {
        final TypeSupplier springTypeSupplier = springTypeSupplier(apiBinder);
        //
        // 在 Hasor 中注册一个 Bean 但 Bean 真实是委托给 Spring 创建。
        apiBinder.bindType(Hello.class).toProvider(() -> springTypeSupplier.get(Hello.class));
        //
        // 在注册 SPI 创建委托给 Spring 创建。
        apiBinder.bindSpiListener(HelloSpi.class, () -> springTypeSupplier.get(HelloSpi.class));
        //
        // 加载一个配置在 Spring 中的 Module
        apiBinder.loadModule(InSpringModule.class, springTypeSupplier);
        //
        // 加载一个 Web 控制器，控制器的创建委托给 Spring
        apiBinder.loadMappingTo(Hello.class, springTypeSupplier);
    }
}


综上所述使用委托创建主要是依托两种模式：

- 模式1：利用 Supplier 延迟创建 Bean，在创建 Bean 的使用通过 Spring TypeSupplier 来创建。或者干脆把 Spring 的上下文注入进来直接从 Spring 中获取。
- 模式2：利用 ApiBinder 上提供的重载方法，指定 TypeSupplier 参数。


例如上述例子中：InSpringModule 位于 spring 中的 Module

.. code-block:: java
    :linenos:

    @Component
    public class ExampleModule implements Module {
        @Autowired
        private DataSource dataSource = null; // Spring 的注入

        public void loadModule(WebApiBinder apiBinder) {
            ....
        }
    }
