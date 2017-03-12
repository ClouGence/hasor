&emsp;&emsp;这个小节，我们讲一讲如何在一个多工程的系统中实践 Hasor 模块化。

&emsp;&emsp;当项目的规模大到一定量的时候，我们通常会按照不同功能把项目拆分成若干部分。然后每个部分单独放到一个工程中。例如 Hasor 的首页项目就拆分为多个工程，如下：
```
website-domain       // 模型定义
  ^      ^
  | website-client   // RPC服务接口
  |      ^
website-core         // 服务类和业务逻辑
  ^  ^   ^
  |  | website-login // OAuth
  |  |   ^
  | website-web      // 处理Web请求和响应
website-test         // 各类单元测试
```

&emsp;&emsp;Hasor 在拆分多个工程时通常你不需要做什么特别的事，只要在不同的工程里写自己的 Module 就可以了，最后在统一把 Module 汇总一下万事大吉。没错 Hasor 在的多工程的项目中模块化实践的确就是这么干的。

&emsp;&emsp;对于多工程项目 Hasor 还支持把 hasor-config.xml 配置文件，拆分到每个工程里去。具体的做法是在每个项目中创建一个“static-config.xml”名字的文件，然后配置文件的内容加入下面这样的xml配置：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
   ...
</config>
```

&emsp;&emsp;static-config.xml 配置文件有别于 hasor-config.xml，它有几个限制：
- 1.static-config.xml 必须叫这个文件名，不能更换成其它的名字。
- 2.其次它必须放到 classpath 的根目录下。
- 3.如果 static-config.xml 配置的内容和其它 static-config.xml 冲突，那么会产生覆盖问题，因此要注意配置隔离。
- 4.如果 hasor-config.xml 中如果出现 static-config.xml 配置冲突，hasor-config.xml，享有优先覆盖权利。
- 更多有关配置文件相关的特性，请参阅配置文件章节。
