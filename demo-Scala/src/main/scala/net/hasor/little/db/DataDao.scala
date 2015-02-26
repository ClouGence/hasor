package net.hasor.little.db
import javax.sql.DataSource
import java.util.{ List, Map }
import net.hasor.core.{ Hasor, AppContext, ApiBinder, StartModule, Settings }
import net.hasor.db.jdbc.core.{ JdbcTemplate, JdbcTemplateProvider }
import net.hasor.db.transaction.interceptor.simple.SimpleTranInterceptorModule
import org.more.logger.LoggerHelper
import com.mchange.v2.c3p0.ComboPooledDataSource
/**
 *
 */
class DataDao(prex: String)
  extends StartModule {

  override def loadModule(apiBinder: ApiBinder) = {
    //1.创建 DataSource
    val dataSource = createDataSource(apiBinder.getEnvironment().getSettings())
    //2.绑定DataSource接口实现
    apiBinder.bindType(classOf[DataSource]).toInstance(dataSource)
    //3.绑定JdbcTemplate接口实现
    apiBinder.bindType(classOf[JdbcTemplate]).toProvider(new JdbcTemplateProvider(dataSource))
    //4.启用默认事务拦截器
    apiBinder.installModule(new SimpleTranInterceptorModule(dataSource))
  }
  private def createDataSource(settings: Settings): DataSource = {
    val driverString = settings.getString("demo-jdbc-" + prex + ".driver")
    val urlString = settings.getString("demo-jdbc-" + prex + ".url")
    val userString = settings.getString("demo-jdbc-" + prex + ".user")
    val pwdString = settings.getString("demo-jdbc-" + prex + ".password")
    val poolMaxSize: Integer = 200
    //
    LoggerHelper.logInfo("C3p0 Pool Info maxSize is ‘%s’ driver is ‘%s’ jdbcUrl is‘%s’", poolMaxSize, driverString, urlString)
    //
    val dataSource = new ComboPooledDataSource()
    dataSource.setDriverClass(driverString)
    dataSource.setJdbcUrl(urlString)
    dataSource.setUser(userString)
    dataSource.setPassword(pwdString)
    dataSource.setMaxPoolSize(200)
    dataSource.setInitialPoolSize(1)
    //dataSource.setAutomaticTestTable("DB_TEST_ATest001");
    dataSource.setIdleConnectionTestPeriod(18000)
    dataSource.setCheckoutTimeout(3000)
    dataSource.setTestConnectionOnCheckin(true)
    dataSource.setAcquireRetryDelay(1000)
    dataSource.setAcquireRetryAttempts(30)
    dataSource.setAcquireIncrement(1)
    dataSource.setMaxIdleTime(25000)
    //
    dataSource
  }
  //
  //
  //

  var jdbcTemplate: JdbcTemplate = null

  override def onStart(appContext: AppContext) = {
    this.jdbcTemplate = appContext.getInstance(classOf[JdbcTemplate])
  }

  def queryList(sqlQuery: String): List[Map[String, Object]] = {
    this.jdbcTemplate.queryForList(sqlQuery)
  }
}