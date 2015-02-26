// https://github.com/apache/spark/blob/master/examples/src/main/scala/org/apache/spark/examples/streaming/ActorWordCount.scala
package net.hasor.little.db;

object Simple {
  implicit def wrapper(s: java.lang.String) = new { def wrap = "--" + s + "--" }
}


//val file = spark.textFile("hdfs://...")
//val counts = file.flatMap(line => line.split(" "))
//  .map(word => (word, 1))
//  .reduceByKey(_ + _)
//counts.saveAsTextFile("hdfs://...")
