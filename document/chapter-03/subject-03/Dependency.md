&emsp;&emsp;前面我们简单介绍了一下 Hasor 模块化，并同时展现了一个简单的例子。接下来在本小节我们展示一下在同一个项目中进行模块化的实践。

&emsp;&emsp;现在假定我们在开发一个用户管理系统，系统根据功能大致分为：用户模块、权限模块、分类打标、登录认证。

&emsp;&emsp;上述模块划分是业务纬度，在实际开发中我们真正落实写代码时往往会有一个技术纬度的功能分类。例如上述的业务模块可能最终的技术模块会这样分：数据库读写、OAuth、SSO、Domain、User、Auth。它们分别对应的是：

- ORM --> 数据模型和数据库的映射，并提供数据库操作。
- OAuth --> 合作网站登录。
- SSO --> 单点登录方案。
- Auth --> 权限认证和查询接口。
- Domain --> 数据模型。
- User  --> 用户系统的业务逻辑。

&emsp;&emsp;接着使用 Hasor 的 Module 可以定义每一个模块，这里很简单无外乎就是定义了 几个类。例如：
```java
public class MyBatisModule implements Module { ... }
public class OAuthModule implements Module { ... }
public class SSOModule implements Module { ... }
...
```

&emsp;&emsp;现在我们已经可以用不同的模块类将不同的部分隔离开，接下来我们需要一个地方将它们整体配置到一起，并指定它们的顺序。

&emsp;&emsp;第一个方式，就是我们前面几次经常看到的方式，通过 xml 配置这些 Module。当然在这里您也可沿用这种方式。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor.modules>
        <module>net.myproject.db.MyBatisModule</module>
        <module>net.myproject.auth.OAuthModule</module>
        ...
    </hasor.modules>
</config>
```

&emsp;&emsp;第二种方式，就是我们现在要介绍的一种全新的形式来组合您的模块。这种方式比起xml 配置来说简单的很多，使用也很方便。首先找到我们项目的入口 Module，我们假定这个 Module 名字叫做 RootModule。那么接下来我们只要在 RootModule 中想下面这样把这几个 Module 安装进去就可以了。
```java
public class RootModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.installModule(new MyBatisModule());
        apiBinder.installModule(new OAuthModule());
        ...
    }
}
```

&emsp;&emsp;在第二种方式中我们看到一个 Module 可以被另外一个 Module 所安装。这就是 Hasor Module 提供的全新的一种方式来处理模块的依赖配置，这种方式会大大降低我们对配置文件的依赖程度，并且有助于模块配置的高内聚。
