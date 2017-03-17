&emsp;&emsp;前面我们已经介绍过配置文件的各种特性，也介绍了各种配置文件。那么本节讲一下 `静态配置文件` 和 `主配置文件` 的加载顺序。

&emsp;&emsp;在开讲之前我们先回顾一下，两种配置文件的特点：
* 主配置文件(hasor-config.xml)
  * 1.Hasor.createAppContext，所用到的配置文件是主配置文件
  * 2.主配置文件在启动 Hasor 时只能有一个
  * 3.项目中主配置文件的 `xmlns` 属性建议设置为：`http://project.hasor.net/hasor/schema/main`
* 静态配置文件(static-config.xml)
  * 1.classpath 目录下的 `static-config.xml` 文件被称为静态配置文件
  * 2.每个jar包中都可以携带一个静态配置文件，多个jar包可以共同提供多个
  * 3.静态配置文件中的 `xmlns` 属性建议设置为自定义的。

#### 加载顺序
&emsp;&emsp; &emsp;&emsp; 在读取 `static-config.xml` 过程中，Hasor 会按照 xmlns 对配置文件进行分类，以保证命名空间的隔离性。然后在读取 `hasor-config.xml(或其它名)` 主配置文件。在读取主配置文件时如果发生配置冲突，最后会使用主配置文件中的配置覆盖 `static-config.xml` 的默认配置。

#### 配置冲突
&emsp;&emsp;配置冲突指的是，`xmlns` 命名空间相同的情况下，出现相同的配置。

&emsp;&emsp;这里在强调一下：多个命名空间配置相同的节点，在读取配置时。 `http://project.hasor.net/hasor/schema/main` 命名空间下的配置享有优先权，其它命名空间的配置按照字符串排序顺序决定。