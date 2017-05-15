&emsp;&emsp;站点文件布局，在上一个小节里我们介绍了 Hasor 装饰器的使用。在本节讲解一下建议的站点文件布局方式。

&emsp;&emsp;我们以 Hasor 首页项目为例（http://git.oschina.net/zycgit/hasor-website），该项目的 webapp 目录下共有 5 个子目录：
```text
webapp
    control     页面模块
    layout      模板页
    static      静态资源文件
    templates   网站页面
    WEB-INF     web.xml
```

&emsp;&emsp;可以看到首页项目采用的站点目录布局方式里多了 control、static 两个目录。static 目录不难理解就是一个网站资源地址。而 control 值得单独说明一下。

&emsp;&emsp;control 存在的意义是帮助我们管理那些页面上的小模块，这些页面的小模块通常是以模板形式存在的。而这些小模块我们并不想让用户可以有机会访问到。因此这些不想被访问到的页面小模块统一放到control下面。接下来让我们看一下使用效果：

&emsp;&emsp;以 freemarker 为例，在任何一个页面或者模版页上使用下面代码引用 control。
```html
<#include "/control/head.htm"/>
```