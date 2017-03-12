&emsp;&emsp;前面讲了很多有关 Bean 的依赖注入。现在我们来介绍一下 Hasor 的一个特殊依赖注入功能。我们先来举例一个场景，假定我们有一个类用来封装数据库连接信息。它的样子应该类似这样的：
```java
public class DataBaseBean {
    private String jdbcDriver;
    private String jdbcURL;
    private String user;
    private String password;
    ...
}
```

&emsp;&emsp;通常我们的数据配置会保存在 “hasor-config.xml” 的配置文件里，例如这样：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <jdbcSettings>
        <jdbcDriver>com.mysql.jdbc.Driver</jdbcDriver>
        <jdbcURL>jdbc:mysql://127.0.0.1:3306/test</jdbcURL>
        <userName>sa</userName>
        <userPassword></userPassword>
    </jdbcSettings>
</config>
```

&emsp;&emsp;接下来摆在我们面前的第一个问题就是，如何把配置文件里的配置读取到配置文件中。当然您可以想下面这样通过 Setting 配置文件读取接口进行读取。
```java
public class DataBaseBean {
    @Inject
    private Settings settings;//依赖注入 Settings 接口对象。
    
    public void setupConfig(){
        jdbcDriver = settings.getString("jdbcSettings.jdbcDriver");
        jdbcURL = settings.getString("jdbcSettings.jdbcURL");
        user = settings.getString("jdbcSettings.user");
        password = settings.getString("jdbcSettings.password");
    }
}
```

&emsp;&emsp;上面这样的代码虽然可以满足需求，但是 Hasor 为您提供了更加便捷的方式，您可以直接通过依赖注入，把配置文件中的配置信息直接注入到你的字段上。同样的例子，我们改一下就变得无比简洁：
```java
public class DataBaseBean {
    @InjectSettings("jdbcSettings.jdbcDriver")
    private String jdbcDriver;
    @InjectSettings("jdbcSettings.jdbcURL")
    private String jdbcURL;
    @InjectSettings("jdbcSettings.user")
    private String user;
    @InjectSettings("jdbcSettings.password")
    private String password;
    ...
}
```

&emsp;&emsp;下面我们在演示一下 @InjectSettings 更强悍的功能，在注入的时自动转换类型。
```java
public class TestBean {
    @InjectSettings("userInfo.myAge")
    private int myAge;
}
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <userInfo>
        <myAge>31</myAge>
    </userInfo>
</config>
```


