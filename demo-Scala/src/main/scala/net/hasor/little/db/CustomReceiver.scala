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
    //local[*] -> 本地多核运行
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("CustomReceiver")
    val ssc = new StreamingContext(sparkConf, Seconds(1))
    //
    doWork(ssc);
    //
    ssc.start(); // 开始
    ssc.awaitTermination(); // 计算完毕退出
  }
  //
  def doWork(ssc: StreamingContext) = {
    val receiver = new UserDataCustomReceiver("mysql", "select * from TB_User");
    val users = ssc.receiverStream(receiver)
    //
    val words = users.flatMap(user => user.get("email").split("@"))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
    wordCounts.print()
    //    val emails = users.map(user => (user.get("email"), user));
    //    val pairs = emails.map(word => (word, 1));
    //    val wordCounts = pairs.reduceByKey(_ + _);
  }
}