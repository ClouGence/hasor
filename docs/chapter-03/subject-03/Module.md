&emsp;&emsp;在设计软件系统时，通过划分模块可以让应用的结构更加清晰合理。模块还可以根据实际需要，设计成可更换的单元。因此模块化是一种处理复杂系统分解成为更好的可管理模式。它可以通过在不同组件设定不同的功能，把一个问题分解成多个小的独立、互相作用的组件，来处理复杂、大型的系统。

&emsp;&emsp;Hasor 是一个支持模块化的开发框架，它的模块化有着非常清晰的模块接口定义。实现一个 Hasor 模块很简单，只需要实现 `net.hasor.core.Module` 接口。即便是各种 Hasor 插件也都是通过 `Module` 接口提供的扩展。

&emsp;&emsp;所以说，无论您是在编写 Hasor 插件，还是使用 Hasor 进行模块化开发，都要用到 Module。这也是 Hasor “微内核 + 插件” 架构特定的一种体现。

&emsp;&emsp;下面我们就开始构建第一个 Module。首先我们编写自己的模块类，然后在配置文件中加以配置，接着启动我们的容器并加载这个配置文件就可以了。
```java
package net.test.hasor;
public class HelloModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        System.out.println("Hello Module");
    }
}
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor.modules>
        <module>net.test.hasor.HelloModule</module>
    </hasor.modules>
</config>
```
