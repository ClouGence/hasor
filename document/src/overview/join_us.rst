参与开发
------------------------------------
Git仓库，由于国内外网络访问速度因素。Hasor 的源码被同时托管在两个不同的仓库站点上。

    - 中国境内： `码云(https://gitee.com/zycgit/hasor) <https://gitee.com/zycgit/hasor>`__
    - 中国境外： `Github(https://github.com/zycgit/hasor) <https://github.com/zycgit/hasor>`__

通过 ``git clone`` 命令可以完整的得到 Hasor 所有源码。Hasor 是通过 Maven 工具来组织代码工程的，因此您还需要准备好 Maven 环境。

    - 源码编译：``mvn clean compile``
    - 源码打包：``mvn clean package``，打包会执行一些列 TestCase。如果想快速打包那么执行 ``mvn clean package -Dmaven.test.skip=true``
    - 编译本手册：``cd document && mvn clean site``
    - Mac 系统下建议在编辑手册时设置字体为 Osaka

**相关资源**
    - Issues：[https://gitee.com/zycgit/hasor/issues]
    - Hasor首页项目：[http://git.oschina.net/zycgit/hasor-website]
    - Sphinx：[http://www.sphinx-doc.org/en/master/]
    - QQ群：193943114
