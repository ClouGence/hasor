&emsp;&emsp; 在上面我们把数据库的连接信息，通过代码形式写死在程序中，实际情况下我们一般会将它扔进配置文件，对于一些轻量框架而言解析配置文件的事情需要开发者自己开发。但是在 Hasor 中您不必关心这些细节。

&emsp;&emsp; 首先，新建一个xml文件，并命名为 `hasor-config.xml` 您需要把它放置在 classpath 的跟路径下，文件内容如下：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">

</config>
```

&emsp;&emsp;接下来，我们在 xml 中创建一个自己应用的节点，然后把数据库配置信息放进去。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <myApp>
        <jdbcSettings>
            <jdbcDriver>com.mysql.jdbc.Driver</jdbcDriver>
            <jdbcURL>jdbc:mysql://127.0.0.1:3306/test</jdbcURL>
            <userName>sa</userName>
            <userPassword></userPassword>
        </jdbcSettings>
    </myApp>
</config>
```

Tips:Hasor 配置有包扫描功能，当遇到需要扫描包中类时候 Hasor 会根据预先配置的范围进行扫描，为了尽量缩短扫描时间，我们一般会重新配置 Hasor 对包扫描的范围，这就需要修改一下 Hasor 的系统配置。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor.loadPackages>net.demo.hasor.*</hasor.loadPackages>
</config>
```

&emsp;&emsp;最后一个环节读取这些配置，并替换之前写死在代码里的那些数据库配置信息。下面是读取配置文件的样例代码：
```java
AppContext appContext = Hasor.createAppContext(new Module() {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        Settings settings = apiBinder.getEnvironment().getSettings();
        String driverStr = settings.getString("myApp.jdbcSettings.jdbcDriver");
        String urlStr = settings.getString("myApp.jdbcSettings.jdbcURL"));
        String userStr = settings.getString("myApp.jdbcSettings.userName");
        String pwdStr = settings.getString("myApp.jdbcSettings.userPassword");
        ......
    }
}
```