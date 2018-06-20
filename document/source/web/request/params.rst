获取请求参数
------------------------------------
通过 @ReqParam 注解，获取请求参数。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker,
                            @ReqParam("name") String userName,
                            @ReqParam("pwd") String pwd) {
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
                            @ReqParam("values") String[] vars) {
            ...
        }
    }


当然除了 @ReqParam 注解之外您还可以使用 WebController 类提供的工具方法获取 请求参数。

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
本节讲解 @CookieParam 注解，通过该注解获取 Cookie 数据。该注解的用法和 @ReqParam 一样。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker,
                            @CookieParam("name") String userName ,@CookieParam("pwd") String pwd) {
            ...
        }
    }


如果cookie中存储了一组相同的数据，那么可以使用下面这种方式获取这一组值。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker, @CookieParam("values") String[] vars) {
            ...
        }
    }


当然除了 @CookieParam 注解之外您还可以使用 WebController 类提供的工具方法获取 Cookie 值。

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
本节讲解 @HeaderParam 注解，通过该注解获取请求头数据。该注解的用法和 @ReqParam 一样。

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker, @HeaderParam("ajaxTo") boolean ajaxTo) {
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
使用方法和前面几种注解介绍的一样，不同的是需要使用 @QueryParam 注解，例如：

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker, @QueryParam("value") boolean ajaxTo) {
            ...
        }
    }


获取请求对象的属性
------------------------------------
最原始的办法是通过 httpRequest.getAttribute 获取，但是 Hasor 的 Web 框架提供了下面几种方式来获取。

通过 WebController 基类获取

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker) {
            Object var = this.getData("value");
        }
    }


通过 @AttributeParam 注解获取

.. code-block:: java
    :linenos:

    @MappingTo("/helloAcrion.do")
    public class HelloAcrion extends WebController {
        public void execute(RenderInvoker invoker, @AttributeParam("value") boolean value) {
            ...
        }
    }

