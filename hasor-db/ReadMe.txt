Hasor-DB

介绍：
	Hasor的数据库操作框架，该框架主要目的是为Hasor提供关系型数据库访问功能。最新版本：0.0.3。
	前身是Hasor-JDBC项目，该项目中包含了“Transaction”、“JDBC”、“DataSource”、“ORM”四个部分。
	这四个组建又互相成为一个体系。整个Hasor-DB，可以被独立使用。其中“Transaction”、“JDBC”两个重要部件
	参考了SpringJDB做的设计，可以说Hasor-DB是缩小版的SpringJDBC。拥有SpringJDBC绝大部分功能。


DataSource：
	  DS 是一个简易的数据源管理工具，它位于“net.hasor.db.datasource”软件包。开发者可以通过
	“DataSourceUtils”工具类静态的获取和释放数据库连接。DataSourceHelper是它的核心接口。
	它的工作机制是为每个线程根据数据源绑定唯一的数据库连接。它内部通过引用计数来保证在释放连接的真正时机。
	Transaction组件就是通过它管理数据库事务连接的。

JDBC：
	  JDBC操作封装，这套软件包可以独立使用。通过它可以简化针对JDBC接口的使用。该接口原型是SpringJDBC
	  你可以简单的理解为它就是轻量化的SpringJDBC框架。

Transaction：
	  Hasor提供的操作数据库事务的接口，提供了7个不同的事务隔离级别。其实现思想来源于Spring。

ORM(未完成)：
	  AR模式的数据库操作接口，基于JDBC。
