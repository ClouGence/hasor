import javax.sql.DataSource
import net.hasor.core.{ ApiBinder, Module, Settings }
import net.hasor.db.jdbc.core.{ JdbcTemplate, JdbcTemplateProvider }
import net.hasor.db.transaction.interceptor.simple.SimpleTranInterceptorModule
import org.more.logger.LoggerHelper
import com.mchange.v2.c3p0.ComboPooledDataSource
/**
 *
 */
class DataDao(prex: String)
  extends Module {

  override def loadModule(apiBinder: ApiBinder) = {
    val settings = apiBinder.getEnvironment().getSettings()
    val dataSource = createDataSource(settings)
    //    //3.绑定DataSource接口实现
    //    apiBinder.bindType(DataSource.class).toInstance(dataSource)
    //    //4.绑定JdbcTemplate接口实现
    //    apiBinder.bindType(JdbcTemplate.class).toProvider(new JdbcTemplateProvider(dataSource))
    //    //5.启用默认事务拦截器
    //    apiBinder.installModule(new SimpleTranInterceptorModule(dataSource))
  }
  private def createDataSource(settings: Settings): DataSource = {
    val dataSource = new ComboPooledDataSource()
    //
    val driverString = settings.getString("demo-jdbc-" + prex + ".driver")
    val urlString = settings.getString("demo-jdbc-" + prex + ".url")
    val userString = settings.getString("demo-jdbc-" + prex + ".user")
    val pwdString = settings.getString("demo-jdbc-" + prex + ".password")
    val poolMaxSize: Integer = 200
    //
    LoggerHelper.logInfo("C3p0 Pool Info maxSize is ‘%s’ driver is ‘%s’ jdbcUrl is‘%s’", poolMaxSize, driverString, urlString)
    //
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
}