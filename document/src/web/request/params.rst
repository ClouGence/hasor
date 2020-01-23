获取请求参数
------------------------------------
通过 `@RequestParameter` 注解，获取请求参数：

.. code-block:: java
    :linenos:

    @MappingTo("/helloAction.do")
    public class HelloAction {
        public void execute(@RequestParameter("name") String userName,
                            @RequestParameter("pwd") String pwd) {
            ...
        }
    }

请求URL地址：`http://localhost:8080/helloAction.do?name=userA&pwd=123456`


.. HINT::
    如果页面上使用了 checkbox 来表示一组值。那么可以使用 `@RequestParameter("values") String[] vars` 获取。


获取Cookie
------------------------------------
通过 `@CookieParameter` 注解获取 Cookie 数据，该注解的用法和 `@RequestParameter` 一样：

.. code-block:: java
    :linenos:

    @MappingTo("/helloAction.do")
    public class HelloAction {
        public void execute(@CookieParameter("name") String userName,
                            @CookieParameter("pwd") String pwd) {
            ...
        }
    }


.. HINT::
    如果cookie中存储了一组相同的数据，那么可以使用 `@CookieParameter("values") String[] vars` 获取。


获取请求头
------------------------------------
获取请求头信息使用 `@HeaderParameter` 注解：

.. code-block:: java
    :linenos:

    @MappingTo("/helloAction.do")
    public class HelloAction {
        public void execute(@HeaderParameter("ajaxTo") boolean ajaxTo) {
            ...
        }
    }

.. code-block:: js
    :linenos:

    $.ajax({
        beforeSend: function (request) {
            request.setRequestHeader("ajaxTo", "true");
        },
        url: "/helloAcrion.do",
        data: formData,
        dataType: 'json',
        async: true,
        success: function (result) {
            ...
        },
        error: function (result) {
            ...
        }
    });


获取URL中?部分的参数
------------------------------------
需要使用 `@QueryParameter` 注解，例如：

.. code-block:: java
    :linenos:

    @MappingTo("/helloAction.do")
    public class HelloAction {
        public void execute(@QueryParameter("value") boolean ajaxTo) {
            ...
        }
    }

请求URL地址：`http://localhost:8080/helloAction.do?value=true`


获取Attribute
------------------------------------
最原始的办法是通过 httpRequest.getAttribute 获取，但 Hasor 提供了 `@AttributeParameter` 注解

.. code-block:: java
    :linenos:

    @MappingTo("/helloAction.do")
    public class HelloAction {
        public void execute(@AttributeParameter("value") boolean value) {
            ...
        }
    }


请求参数组
------------------------------------
当一个请求递交了大量参数时，为了减少编写参数列表可以使用 `@ParameterGroup`

.. code-block:: java
    :linenos:

    @MappingTo("/helloAction.do")
    public class HelloAction {
        public void execute(@ParameterGroup() UserInfo userInfo) {
            ...
        }
    }
    public class UserInfo {
        @RequestParameter("param_1")
        private String param_1;
        @RequestParameter("param_2")
        private String param_2;
        @RequestParameter("param_3")
        private String param_3;
        @RequestParameter("param_4")
        private String param_4;
        @RequestParameter("param_5")
        private String param_5;
        ...
    }


请求参数自动类型转换
------------------------------------
Hasor Web 框架可以帮助你进行简单的类型转换。例如：

.. code-block:: java
    :linenos:

    @MappingTo("/helloAction.do")
    public class HelloAction {
        public void execute(@RequestParameter("name") String name,
                            @RequestParameter("age") int age) {
            ...
        }
    }


可以转换的类型有：

- 基础类型：``byte``、``short``、``int``、``long``、``float``、``double``、``boolean``、``String``
- 大数类型：``BigInteger``、``BigDecimal``
- 时间日期：``java.util.Date``、``java.util.Calendar``、``java.sql.Date``、``java.sql.Time``、``java.sql.Timestamp``
- 其它类型：``Enum``、``File``、``URL``、``URI``

类型转换是使用的 net.hasor.utils.convert.ConverterUtils 工具，因此设置时间格式需要通过下面这段代码来配置 ``ConverterUtils`` 工具。

整个程序启动时执行一次就可以。

.. code-block:: java
    :linenos:

    DateConverter converter = new DateConverter();
    converter.setPatterns(new String[] { "yyyy-MM-dd", "hh:mm:ss", "yyyy-MM-dd hh:mm:ss" });
    ConverterUtils.register(converter, Date.class);
