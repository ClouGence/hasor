package net.hasor.little.db

;

import java.io.OutputStreamWriter
import com.alibaba.fastjson.JSON
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream}
import org.apache.spark.streaming.{Time, StreamingContext, Seconds}
import StreamingContext._
import net.hasor.core.{Hasor, AppContext, ApiBinder, StartModule, Settings}
import java.net.{ServerSocket, InetSocketAddress, Socket}
import net.hasor.core.{ApiBinder, Module, Settings}
import org.apache.spark.{SparkConf, Logging}
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
 *
 */
object CustomReceiver {
  def localAddress = "localhost"

  def localProt = 6000

  def main(args: Array[String]) {
    //local[*] -> 本地多核运行
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("CustomReceiver")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    //
    inDataThread()

    val userReceiver = ssc.receiverStream(new UserDataCustomReceiver(localAddress, localProt))
    val emails = userReceiver.flatMap(user => (user.split("@"))    .map( (_,1)) )   .reduceByKey(_ + _)
    emails.print()
    //
    ssc.start(); // 开始
    ssc.awaitTermination(); // 计算完毕退出
  }

  def inDataThread() = {
    new Thread("in data") {
      override def run() = {
        val app = Hasor.createAppContext("jdbc-config.xml", new DataDao("mysql"))
        val dao = app.getInstance(classOf[DataDao])
        val userDataList = dao.queryList("select * from TB_User")
        val server = new ServerSocket()
        server.bind(new InetSocketAddress(localAddress, localProt))
        val socket: Socket = server.accept();
        val outWrite = new OutputStreamWriter(socket.getOutputStream(), "utf-8");
        for (index <- 0 to userDataList.size() - 1) {
          val userData = userDataList.get(index)
          val email =userData.get("email");
          //          outWrite.write(JsonBridge.toJson(userData) + "\n")
          outWrite.write(email + "\n")
          outWrite.flush()
          Thread.sleep(100)
        }
        //socket.close()
        println("--end--")
      }
    }.start()
  }


  def doWork(ssc:StreamingContext) = {

    //    outDataRegister(emails)
  }
  //
  def outDataRegister(email: DStream[_]) = {
    def foreachFunc = (rdd: RDD[_], time: Time) => {
      val first10 = rdd.take(10)
      println("-------------------------------------------")
      println("Time: " + time)
      println("-------------------------------------------")
      first10.foreach(println)
    }
    email.foreachRDD(foreachFunc)
  }
}