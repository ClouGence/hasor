1.数据的测试请先启动好 MySQL数据库。并把数据库连接配置到jdbc-config.xml配置文件中。
	<!-- MySQL -->
	<demo-jdbc-mysql>
		<driver>com.mysql.jdbc.Driver</driver>
		<url>jdbc:mysql://mysql.hasor.net/r4lwh9g5081zz0pg?createDatabaseIfNotExist=true&amp;useUnicode=true&amp;characterEncoding=utf8</url>
		<user>title_account</user>
		<password>title_account</password>
	</demo-jdbc-mysql>
2.按照实际情况进行配置

3.在数据库中创建如下表
	TB_User，见  TB_User.sql