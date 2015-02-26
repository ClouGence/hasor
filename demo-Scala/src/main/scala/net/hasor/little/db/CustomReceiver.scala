package net.hasor.little.db;
import java.io.{ InputStreamReader, BufferedReader, InputStream }
import java.net.Socket
import net.hasor.core.{ ApiBinder, Module, Settings }
import org.apache.spark.{ SparkConf, Logging }
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{ Seconds, StreamingContext }
/**
 *
 */
object CustomReceiver {
  def main(args: Array[String]) {
    //接收器
    val receiver = new UserDataCustomReceiver("mysql", "select * from TB_User");
    receiver.receive()
    //
  }
}