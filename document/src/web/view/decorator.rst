母版页
------------------------------------
母版页是 Hasor 的 Web 开发框架中极具特色的一项功能，也叫它页面装饰器。

它的使用场景是这样的，当你很多页面都有着共同的布局结构时，就可以使用 Hasor 的装饰器。如果您接触过或者了解 Sitemesh 框架，应该对 Hasor 的装饰器不会台陌生。它们要做的事情是一样的，都是为了给项目提供 ‘母版页’技术。

它的工作原理是一个典型的实践 `装饰器模式` 一个启用了装饰器的站点在渲染其页面时，会先渲染目标页面到一个临时的缓冲区。然后会再次渲染布局模板文件，这个时候预先渲染的页面就会被安插到指定的位置上。

在 Hasor 中布局模板文件只会有一个生效， Hasor 不支持嵌套的布局模板。如果您要在布局模板中再次提炼公共布局模板，您需要做的是将布局模板文件的内容模块化。而不是套用嵌套布局。

默认情况下，Hasor 的装饰器是关闭的。如果您需要启用装饰器，那么需要您在配置文件中设置环境变量以启用它。

.. code-block:: xml
    :linenos:

    <!-- 启用站点文件布局 -->
    <HASOR_RESTFUL_LAYOUT>true</HASOR_RESTFUL_LAYOUT>


启用了站点布局之后，您的整个 webapp 目录中的文件必须按照固定的目录格式进行编排。一个合法的站点目录应该是这个样子的：

.. code-block:: none
    :linenos:

    webapps/
        layout/     布局模板
        templates/  页面模板
        WEB-INF/    web.xml


**母版页**
我们用一个例子来说明 Hasor 的装饰器如何使用。我们的例子中要求所有网页都统一加上一个页脚。

首先创建我们的模版页，用来确定页面最终的结构，同时我们把页脚加入进去。这样每一个页面在执行之后都会统一加上一个页脚。

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


面这个页面代码使用的是 Freemarker 语法编写，其中

- `content_placeholder` 表示的是用户实际访问的目标页面
- `rootData` 是请求处理器用于保存数据的全局对象，其作用范围是 request。

我们通过 `Invoker` 接口写入的所有数据在 `rootData` 对象中都可以找到。通过它我们可以实现在 子页面中设置页面的 title ，然后在母版页中将 title 数据取出。

上模板的文件名默认情况下统一为：default，因此保存上面这个母版页代码到 layout 下文件名为 default.htm


**目标页**
下面这个就是具体的目标页，可以看到具体的页面中，没有看到 body、html 这些元素。这是因为 Hasor 的装饰器在执行时会把 `目标页` 执行结果当做字符串传给母版页，在母版页中通过 content_placeholder 变量进行替换。

.. code-block:: html
    :linenos:

    ${rootData.put('pageTitle','首页')}
    <p>this page form user</p>


**多套母版页**
有时候我们一批页面中大部分相同或者相似，但仍然有少数几个页面是另一类的。这时候就可以通过 多套母版页 来实现少数页面的母版化。

首先在 `layout` 目录中创建一个新的目录，同时创建一个新的母版页用来承载少数页面的定制化。母版文件名为 default.htm

然后在 `templates` 目录中创建同样的目录结构，然后把另一类的页面放到这个目录中。


**文件布局**
站点文件布局，在上一个小节里我们介绍了 Hasor 装饰器的使用。在本节讲解一下建议的站点文件布局方式。

我们以 Hasor 首页项目为例（http://git.oschina.net/zycgit/hasor-website），该项目的 webapp 目录下共有 5 个子目录：

.. code-block:: none
    :linenos:

    webapp
        control     页面模块
        layout      模板页
        static      静态资源文件
        templates   网站页面
        WEB-INF     web.xml


可以看到首页项目采用的站点目录布局方式里多了 control、static 两个目录。static 目录不难理解就是一个网站资源地址。而 control 值得单独说明一下。

control 存在的意义是帮助我们管理那些页面上的小模块，这些页面的小模块通常是以模板形式存在的。而这些小模块我们并不想让用户可以有机会访问到。因此这些不想被访问到的页面小模块统一放到control下面。接下来让我们看一下使用效果：

以 freemarker 为例，在任何一个页面或者模版页上使用下面代码引用 control

.. code-block:: none
    :linenos:

    <#include "/control/head.htm"/>
