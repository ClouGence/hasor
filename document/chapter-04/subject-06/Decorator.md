&emsp;&emsp;装饰器是 Hasor 的 Web 开发框架中极具特色的一项功能。它的使用场景是这样的，当你很多页面都有着共同的布局结构时，就可以使用 Hasor 的装饰器。如果您接触过或者了解 Sitemesh 框架，应该对 Hasor 的装饰器不会台陌生。它们要做的事情是一样的，都是为了给项目提供 ‘母版页’技术。只不过 Hasor 的装饰器不像 Sitemesh 把装饰功能单独做成了一个框架。

&emsp;&emsp;它的工作原理是一个典型的实践 `装饰器模式` 一个启用了装饰器的站点在渲染其页面时，会先渲染目标页面到一个临时的缓冲区。然后会再次渲染布局模板文件，这个时候预先渲染的页面就会被安插到指定的位置上。

&emsp;&emsp;在 Hasor 中布局模板文件只会有一个生效， Hasor 不支持嵌套的布局模板。如果您要在布局模板中再次提炼公共布局模板，您需要做的是将布局模板文件的内容模块化。而不是套用嵌套布局。

&emsp;&emsp;默认情况下，Hasor 的装饰器是关闭的。如果您需要启用装饰器，那么需要您在 hasor-config.xml 中设置环境变量以启用它。
```xml
<!-- 启用站点文件布局 -->
<HASOR_RESTFUL_LAYOUT>true</HASOR_RESTFUL_LAYOUT>
```

&emsp;&emsp;启用了站点布局之后，您的整个 webapp 目录中的文件必须按照固定的目录格式进行编排。一个合法的站点目录应该是这个样子的：
```text
webapps/
    layout/     布局模板
    templates/  页面模板
    WEB-INF/    web.xml
```

&emsp;&emsp;所有访问网站的页面模板文件统一移动到 `templates` 目录下，另外 `layout` 目录用于保存模板页。

&emsp;&emsp;接下来，我们用一个例子来说明 Hasor 的装饰器如何使用。我们的例子中要求所有网页都统一加上一个页脚。

&emsp;&emsp;首先创建我们的模版页，用来确定页面最终的结构，同时我们把页脚加入进去。
```html
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
```

&emsp;&emsp;上面这个页面代码使用的是 Freemarker 语法编写，其中 `content_placeholder` 表示的是用户实际访问的目标页面 target。在模板页中您可以根据自己的实际需要选择输出在母版页中任何一个位置上。

&emsp;&emsp;`rootData`是请求处理器用于保存数据的全局对象，其作用范围是 request。我们通过 `Invoker` 接口写入的所有数据在 `rootData` 对象中都可以找到。通过它我们可以实现在 子页面中设置页面的 title ，然后在母版页中将 title 数据取出。下面这个就是具体的目标页：
```html
${rootData.put('pageTitle','首页')}
<style>
    .rightBorder {
        border-right: thick dashed #cfcfcf;
    }
</style>

this page form user
<div>this is foot</div>
```

&emsp;&emsp;我们可以看到具体的页面中，没有看到 body、html 这些元素。这是因为， Hasor 的装饰器在执行时仅仅做了一次 content_placeholder 的代入。

&emsp;&emsp;模板的文件名默认情况下统一为：default，您可以在 layout 目录中新建子目录来存放其它模板。

&emsp;&emsp;您实际访问的页面在进行模板页面渲染时会自动增加 `templates` 前缀以正确映射到站点布局中。