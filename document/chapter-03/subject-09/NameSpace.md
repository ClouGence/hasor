&emsp;&emsp;配置文件命名空间，主要应用场景是模块化下的 `static-config.xml` 配置文件。我们先看一个实际的例子，下面这些 xml 片段来自于 Hasor 的实际项目中。

&emsp;&emsp;来自 Hasor 核心项目包。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns:root="http://project.hasor.net/hasor/schema/main" xmlns="http://project.hasor.net/hasor/schema/hasor-core">
    <!-- http://project.hasor.net/hasor/schema/main命名空间默认配置 -->
    <root:config>
        <root:hasor.loadPackages>${HASOR_LOAD_PACKAGES}</root:hasor.loadPackages>
    </root:config>
    <hasor>
        <loadPackages>net.hasor.*</loadPackages>
        ....
    </hasor>
</config>
```

&emsp;&emsp;来自 RSF RPC项目。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/hasor-rsf">
    <hasor>
        <loadPackages>net.hasor.rsf.*</loadPackages>
        ...
    </hasor>
</config>
```

&emsp;&emsp;来自 RSF 注册中心项目。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/hasor-registry">
    <hasor>
        <loadPackages>net.hasor.rsf.registry.*</loadPackages>
        ...
    </hasor>
</config>
```
&emsp;&emsp;从上面配置文件中可以看出，每个项目都有自己的 `loadPackages` 每个配置文件之间最重要的不同点是它们都使用了一个全新的 `xmlns` 空间。对于第一个 xml 文件显得可能比较复杂，它是在一个 xml 中配置了两个命名空间的数据。

&emsp;&emsp;Hasor 的配置文件就是通过这种方式来隔离不同的模块，下面我们通过一个例子来展示一下配置文件模块化的终极作用。

&emsp;&emsp;首先我们有两个 `static-config.xml` 配置文件，它们分别位于jarA、jarB。两个 jar 包中。不巧的是两个项目彼此不知道对方的存在，它们都定义了相同的配置：
```xml
------static-config.xml（jarA）
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://mode1.myProject.net">
  <appSettings>
    <serverLocal url="www.126.com" />
  </appSettings>
</config>

------static-config.xml（jarB）
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://mode2.myProject.net">
  <appSettings>
    <serverLocal url="www.souhu.com" />
  </appSettings>
</config>
```

&emsp;&emsp;这时如果根据我们同时加载两个配置文件，那么因为配置节节点冲突，我们在读取配置时候肯定会导致一个配置文件的内容丢失。但是如果是这样就不会有问题：
```java
Settings mod1 = settings.getSettings("http://mode1.myProject.net");
Settings mod2 = settings.getSettings("http://mode2.myProject.net");
System.out.println(mod1.getString("appSettings.serverLocal.url"));
System.out.println(mod2.getString("appSettings.serverLocal.url"));
```

&emsp;&emsp;下面这个配置文件，等同于上面两个配置文件。懂得 xml 的同学肯定一眼就看出来了，其实下面这个配置文件只不过是把上面两个配置文件的内容合并到了一起。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns:mod1="http://mode1.myProject.net" xmlns:mod2="http://mode2.myProject.net" xmlns="http://project.hasor.net/hasor/schema/main">
  <!-- mode1 配置 -->
  <mod1:config>
    <mod1:appSettings>
      <mod1:serverLocal mod1:url="www.126.com" />
    </mod1:appSettings>
  </mod1:config>
  <!-- mode2 配置 -->
  <mod2:config>
    <mod2:appSettings>
      <mod2:serverLocal mod2:url="www.souhu.com" />
    </mod2:appSettings>
  </mod2:config>
</config>
```

&emsp;&emsp;使用 Hasor 解析配置文件，使用上面一个配置文件或者上面两个配置文件效果是一样的。这个就是 Hasor 对配置文件命名空间的支持。下一个章节我们专门讲解配置文件的加载顺序，以及利用加载顺序解决配置项目冲突。

&emsp;&emsp;多个命名空间配置相同的节点，在读取配置时 `http://project.hasor.net/hasor/schema/main` 命名空间下的配置享有优先权。其它命名空间的配置按照字符串排序顺序决定。