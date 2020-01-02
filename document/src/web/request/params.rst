获取请求参数
------------------------------------
通过 `@RequestParameter` 注解，获取请求参数。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker,
                            @RequestParameter("name") String userName,
                            @RequestParameter("pwd") String pwd) {
            ...
        }
    }

请求URL地址：`http://localhost:8080/helloAcrion.do?name=userA&pwd=123456`

如果页面上使用了 checkbox 来表示一组值。那么可以使用下面这种方式获取这一阻值。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker,
                            @RequestParameter("values") String[] vars) {
            ...
        }
    }


当然除了 `@RequestParameter` 注解之外您还可以使用 `WebController` 类提供的工具方法获取 请求参数。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker) {
            String var = this.getPara("name");
        }
    }


获取Cookie
------------------------------------
本节讲解 `@CookieParameter` 注解，通过该注解获取 Cookie 数据。该注解的用法和 `@RequestParameter` 一样。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker,
                            @CookieParameter("name") String userName ,@CookieParameter("pwd") String pwd) {
            ...
        }
    }


如果cookie中存储了一组相同的数据，那么可以使用下面这种方式获取这一组值。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker, @CookieParameter("values") String[] vars) {
            ...
        }
    }


当然除了 `@CookieParameter` 注解之外您还可以使用 `WebController` 类提供的工具方法获取 Cookie 值。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker) {
            String var = this.getCookie("values");
        }
    }


获取请求头信息
------------------------------------
获取请求头信息使用 `@HeaderParameter` 注解，其用法和前面几个一样。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker, @HeaderParameter("ajaxTo") boolean ajaxTo) {
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

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker, @QueryParameter("value") boolean ajaxTo) {
            ...
        }
    }


获取请求对象的属性
------------------------------------
最原始的办法是通过 httpRequest.getAttribute 获取，但是 Hasor 的 Web 框架提供了下面几种方式来获取。

通过 `WebController` 基类获取

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker) {
            Object var = this.getData("value");
        }
    }


通过 `@AttributeParameter` 注解获取

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker, @AttributeParameter("value") boolean value) {
            ...
        }
    }
