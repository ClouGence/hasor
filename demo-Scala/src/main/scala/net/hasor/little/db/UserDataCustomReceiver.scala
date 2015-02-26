package net.hasor.little.db;
import net.hasor.core.{ Hasor, AppContext }
import net.hasor.test.utils.{ HasorUnit }
import org.more.logger.LoggerHelper
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver
import scala.collection.JavaConversions._
/**
 *
 */
class UserDataCustomReceiver(dbConfig: String, sqlQuery: String)
  extends Receiver[Map[String, Any]](StorageLevel.MEMORY_AND_DISK_2) {
  //
  private val userDao = new DataDao(dbConfig)
  Hasor.createAppContext("jdbc-config.xml", userDao)
  //
  /**启动接收器*/
  def onStart() = {
    new Thread("Socket Receiver") {
      override def run() { receive() }
    }.start()
  }
  //
  /**终止接收器*/
  def onStop() = {
    /**/
  }
  //
  //接收过程
  def receive() {
    val userList = this.userDao.queryList(sqlQuery)

    for (user <- userList)
      store(user)

    HasorUnit.printMapList(userList)
  }
}