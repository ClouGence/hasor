使用“hconfig.properties”
------------------------------------
下面介绍 Hasor 的一个特殊的属性文件 “hconfig.properties”。这个属性文件名字是固定的不可更改。它可以存在于下面两个位置：

- `WORK_HOME` 环境变量所表示的目录中。
- `classpath` 目录。

如果 `WORK_HOME` 和 `classpath` 同时存在，那么 `WORK_HOME` 会优先生效。

作用：“hconfig.properties”的作用，是用来覆盖 `environmentVar` 中的配置。

例子工程：[http://git.oschina.net/zycgit/hasor-website]
