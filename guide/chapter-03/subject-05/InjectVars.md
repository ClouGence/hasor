&emsp;&emsp;在前面小节，我们看到 Hasor 支持注入配置文件的功能，这个小结在向您展示一下 Hasor 注入环境变量的能力。

&emsp;&emsp;环境变量，指的是操作系统层面设置的环境变量，例如：JAVA_HOME，还有当前登录用户的主目录：USER.HOME。

&emsp;&emsp;当然这些环境变量你也可以通过“System.getenv()” 或 “System.getProperties()” 自己拿到。只不过 Hasor 针对环境变量做了一些增强并加以管理。有关环境变量的细节在后面环境变量章节会有更加深入的讲解，现在我们来看一看，如何让 Hasor 把 JAVA_HOME 注入到我们的 Bean 中。
```java
public class DataBaseBean {
    @InjectSettings("${JAVA_HOME}")
    private String javaHome;
}
```

&emsp;&emsp;为了让大家更加直观的认识到 Hasor 的环境变量注入不只是简单的获取 JAVA_HOME 我们，还是以“注入配置”章节中的场景为例：
```java
public class DataBaseBean {
    private String jdbcDriver;
    private String jdbcURL;
    private String user;
    private String password;
    ...
}
```

&emsp;&emsp;在上面这个例子中，我们知道数据库的帐号、密码、数据库地址。对于我们来说比较敏感，一个不小心就容易泄露出去。更不要说放到配置文件里，那样会跟随git、svn版本软件永久保留下来。

&emsp;&emsp;为了保密，我们选择在应用程序启动的时候通过 “-D” 参数把用户名、密码传递给程序。然后让 Hasor 框架为我们把传入的敏感信息，注入到 DataBaseBean 类中。
```java
public class DataBaseBean {
    @InjectSettings("${db.user}")
    private String user;
    @InjectSettings("${db.pwd}")
    private String password;
    ...
}

public class TestMain {
    public static void main(String[] args) throws Throwable {
        AppContext appContext = Hasor.createAppContext();
        appContext.getInstance(DataBaseBean.class);
    }
}
```

&emsp;&emsp;最后我们要通过命令行的方式启动这个程序：
`java TestMain -Ddb.user=username -Ddb.pwd=password`