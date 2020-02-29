--------------------
基于Spring配置文件
--------------------

引入<h:*/>标签
------------------------------------
在 Spring 配置文件中，首先需要引入 ``h：*`` 标签的命名空间信息。

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:h="http://www.hasor.net/schema/spring-hasor"
           xmlns:context="http://www.springframework.org/schema/context"
           xsi:schemaLocation="
            http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd
            http://www.hasor.net/schema/spring-hasor http://www.hasor.net/schema/spring-hasor/spring-hasor-4.1.2.xsd">
        ...
    </beans>

启用Hasor
------------------------------------
引入命名空间之后，启用 Hasor 只需要一个标签即可。

.. code-block:: xml
    :linenos:

    <h:hasor/>


共享Spring配置
------------------------------------
Hasor 在启动的时候会将 Spring Environment 中属性信息全部导入到 Hasor Environment 接口中。
``useProperties`` 属性的作用是告诉 Hasor ,是否将 Hasor Environment 接口信息进一步导入到 Settings 接口里。
默认值为 false，表示不导入。

.. code-block:: xml
    :linenos:

    <h:hasor useProperties="true"/>


.. code-block:: java
    :linenos:

    Environment environment = appContext.getEnvironment();
    Settings settings = environment.getSettings();
    //
    assert "HelloWord".equals(environment.getVariable("msg"));
    assert "HelloWord".equals(settings.getString("msg")); // 若 useProperties = false，这里获取不到任何值


配置扫描范围
------------------------------------
一般情况下如果 Module 标注过 ``@DimModule`` 注解并同时被 Spring 托管之后无需在配置扫描范围。
但是有时候加载那些还未被 Spring 托管的 Module，但也标记了 ``@DimModule`` 的 Module 就可以用扫描范围。
Hasor 在加载这些 Module 的时候会 new 它们。

.. code-block:: xml
    :linenos:

    <h:hasor>
        <!-- 注意：在配置 scanPackages 属性的时候，Hasor 和 Spring 不同。Hasor 是配置一个通配符，而不是包名 -->
        <h:loadModule scanPackages="net.hasor.test.spring.mod2.*"/>
    </h:hasor>


指定 Hasor 的配置文件
------------------------------------
虽然共享 Spring 的配置已经解决了大部分配置文件读取的问题，但有时候还是需要更高级的 hconfig.xml 配置文件。
这个时候就可以通过这个属性来指定 Hasor 的 hconfig.xml。

.. code-block:: xml
    :linenos:

    <h:hasor>
        <h:mainConfig>classpath:net_hasor_spring/example-hconfig.xml</h:mainConfig>
    </h:hasor>

启动入口
------------------------------------
``startWithRef`` 和 ``startWith`` 可以用来声明启动入口。两者不同的是，前者是应用 Spring 的其它 Bean；而后者是配置一个类名。

.. code-block:: xml
    :linenos:

    <h:hasor startWithRef="testModuleA"/>
    或者
    <h:hasor startWith="net.hasor.test.spring.mod1.TestModuleA"/>

.. HINT::
    startWithRef 和 startWith 为了保证属性含义，两者只有一个生效。startWithRef 优先。

加载更多 Module
------------------------------------
使用 ``startWith`` 加载 Module 只能配置一个。如果有多个 Module 要加载就需要使用下面的办法。
使用 ``h:module`` 标签的好处是 Module 不需要有 @DimModule 的注解。

.. code-block:: xml
    :linenos:

    <h:hasor>
        <h:loadModule>
            <!-- 引用 Spring 容器中的 Module -->
            <h:module refBean="testModuleB"/>
            <!-- class 配置的类型也会先在 Spring 中定义这个 Bean 然后在引用 -->
            <h:module class="net.hasor.test.spring.mod1.TestModuleC"/>
        </h:loadModule>
    </h:hasor>

.. HINT::
    startWithRef 和 startWith 只能配置一个 Module，而 ``h:loadModule`` 标签的方式可以配置多个。除此之外两者并无任何差别。


传递属性给 Hasor
------------------------------------
Hasor 在启动的时候已经将 Spring Environment 中属性信息全部导入到 Hasor Environment 接口中。
这里是说除了自动导入的这些属性之外其它的属性文件如何进行导入。

下面演示了 ``h:property`` 标签和 ``refProperties`` 属性。两种导入方式。

.. code-block:: xml
    :linenos:

    <!-- placeholder 用来做  ${env1} 字符替换 -->
    <context:property-placeholder location="classpath:net_hasor_spring/env-1.properties"/>
    <!-- 加载属性文件，后面传递给 Hasor -->
    <util:properties id="customProperties" location="classpath:net_hasor_spring/env-2.properties"/>
    <!-- 创建 Hasor 容器 -->
    <h:hasor refProperties="customProperties">
        <h:property name="msg_a">${env1}</h:property>
        <h:property name="msg_b" value="ccc"/>
    </h:hasor>


引用Hasor的服务或Bean
------------------------------------
通过 ``h:bean`` 标签可以在 Spring 中定义一个 Bean，同时这个 Bean 交由 Hasor 创建。

.. code-block:: xml
    :linenos:

    <!-- 该 Bean 是通过 AppContext.getInstance('helloWord') 获取。 -->
    <h:bean id="hasorBean1" refID="helloWord"/>

    <!-- 该 Bean 是通过 AppContext.getInstance(HasorBean.class) 获取。 -->
    <h:bean id="hasorBean2" refType="net.hasor.test.spring.HasorBean"/>

    <!-- 该 Bean 是通过 AppContext.getInstance("abc",HasorBean.class) 获取。 -->
    <h:bean id="hasorBean3" refName="abc" refType="net.hasor.test.spring.HasorBean"/>


多Hasor环境
------------------------------------
Xml 方式的优越性在于，可以在一个 Spring Xml 配置文件中启动多个 Hasor。

.. code-block:: xml
    :linenos:

    <!-- 创建 Hasor 容器1 -->
    <h:hasor id="hasor_1" startWithRef="testModuleA"/>

    <!-- 创建 Hasor 容器2 -->
    <h:hasor id="hasor_2" startWith="net.hasor.test.spring.mod1.TestModuleD"/>


多环境下声明 Hasor Bean 需要指明 ``hasorID``

.. code-block:: xml
    :linenos:

    <h:bean id="aa1" refID="xxxxx" hasorID="hasor_1"/>
    <h:bean id="bb2" refID="xxxxx" hasorID="hasor_2"/>


关于Hasor-Web
------------------------------------
要想在基于 Xml 的 Spring 环境中启动 Hasor-Web，还需要在 web.xml 中配置对应的监听器和拦截器，详情请参考：Web 章节。
