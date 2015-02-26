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
import org.apache.spark.{ SparkConf, Logging }
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.streaming.receiver.Receiver

class UserDataScala {
  def main(args: Array[String]) {
    //-----------------------------------------------
    def oriVal() = 1
    def newVal(i: Int) = oriVal + 1

    def valA = newVal(10)
    def valB = newVal(20)
    //-----------------------------------------------
    println(sum(valA, valB))
  }

  def sum(a: Int, b: Int) = {
    a + b;
  }
}
