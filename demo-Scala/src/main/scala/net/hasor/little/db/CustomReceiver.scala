/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.little.db;
import java.io.{ InputStreamReader, BufferedReader, InputStream }
import java.net.Socket
import net.hasor.core.{ ApiBinder, Module, Settings }
import org.apache.spark.{ SparkConf, Logging }
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.streaming.receiver.Receiver
/**
 *
 */
object CustomReceiver {
  def main(args: Array[String]) {
    //
    //Spack Streaming Context
    val sparkConf = new SparkConf().setAppName("CustomReceiver")
    val ssc = new StreamingContext(sparkConf, Seconds(1))
    //
    //
    val lines = ssc.receiverStream(new CustomReceiver())
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
    wordCounts.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

class CustomReceiver()
  extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2)
  with Logging {

  def onStart() {
    new Thread("Socket Receiver") {
      override def run() { receive() }
    }.start()
  }

  def onStop() {
    /**/
  }

  private def receive() {
    //

    store(userInput)

    //    var socket: Socket = null
    //    var userInput: String = null
    //    try {
    //      logInfo("Connecting to " + host + ":" + port)
    //      socket = new Socket(host, port)
    //      logInfo("Connected to " + host + ":" + port)
    //      val reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))
    //      userInput = reader.readLine()
    //      while (!isStopped && userInput != null) {
    //        store(userInput)
    //        userInput = reader.readLine()
    //      }
    //      reader.close()
    //      socket.close()
    //      logInfo("Stopped receiving")
    //      restart("Trying to connect again")
    //    } catch {
    //      case e: java.net.ConnectException =>
    //        restart("Error connecting to " + host + ":" + port, e)
    //      case t: Throwable =>
    //        restart("Error receiving data", t)
    //    }
  }
}