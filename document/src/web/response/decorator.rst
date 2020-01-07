母版页
------------------------------------
.. HINT::
    延伸阅读：Sitemesh 就是专注于母版页的一款框架。

母版页技术较为成熟，它的工作原理是一个典型的 ``装饰器模式`` 一个启用了装饰器的页面在渲染时，会先渲染目标页面到一个临时的缓冲区。
然后会再次渲染母版页，这个时候预先渲染的页面就会被安插到母版页的特定位置上。最后把合成的新页面一同返回给浏览器。

.. CAUTION::
    在 Hasor 中一次请求页面过程中，只会有一个母版页生效。因此不支持嵌套的布局模板。
    如果需要在布局模板中再次提炼公共布局模板，需要做的是将布局模板文件的内容模块化。而不是套用嵌套布局。


默认情况下 Hasor 的母版页能力是关闭的，可以通过配置文件或者环境打开这个功能。在开启这个功能时，最好指明母版页及一般页面的资源文件位置：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/main">
        <hasor.environmentVar>
            <!-- 启用母版页 -->
            <HASOR_RESTFUL_LAYOUT>true</HASOR_RESTFUL_LAYOUT>
            <!-- 母版页资源文件位置（可选，默认为：/layout） -->
            <HASOR_RESTFUL_LAYOUT_PATH>/layout</HASOR_RESTFUL_LAYOUT_PATH>
            <!-- 页面资源文件位置（可选，默认为：/templates） -->
            <HASOR_RESTFUL_LAYOUT_TEMPLATES>/templates</HASOR_RESTFUL_LAYOUT_TEMPLATES>
        </hasor.environmentVar>
    </config>


依照上面的默认配置 Web工程的目录结构大致会变成这样：

.. code-block:: none
    :linenos:

    webapp
        layout      母版页（HASOR_RESTFUL_LAYOUT_PATH 环境变量指定）
        templates   网站页面（HASOR_RESTFUL_LAYOUT_TEMPLATES 环境变量指定）
        control     页面模块（可选，存放页面中重复的模版）
        static      静态资源文件（可选，静态资源文件）
        WEB-INF     web.xml


**加页脚**

假定网站所有页面都统一加上一个页脚，首先创建母版页并将其保存到``/webapp/layout/default.html``

.. code-block:: html
    :linenos:

    <!DOCTYPE html>
    <html lang="cn">
        <head>
            <title>${rootData.pageTitle!}</title>
        </head>
        <body>
            ${content_placeholder!}
            <div>this is foot</div>
        </body>
    </html>


在这个母版页中含有的变量和含义是：

+-------------------------+--------------------------------------------------------------+
| 位置                    |                                                              |
+=========================+==============================================================+
| `content_placeholder`   | 表示的是用户实际访问的目标页面内容                           |
+-------------------------+--------------------------------------------------------------+
| `rootData`              | 是请求处理器用于保存数据的全局对象，其作用范围是 request     |
+-------------------------+--------------------------------------------------------------+


然后创建目标页并将其保存到``/webapp/templates/target.html``

.. code-block:: html
    :linenos:

    ${rootData.put('pageTitle','首页')}
    <p>this page form user</p>


最后启动 Web 容器，访问：http://localhost:8080/target.html 就会看到完整的结果：

.. code-block:: html
    :linenos:

    <!DOCTYPE html>
    <html lang="cn">
        <head>
            <title>首页</title>
        </head>
        <body>
            <p>this page form user</p>
            <div>this is foot</div>
        </body>
    </html>
