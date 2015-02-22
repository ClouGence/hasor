// https://github.com/apache/spark/blob/master/examples/src/main/scala/org/apache/spark/examples/streaming/ActorWordCount.scala
package net.hasor.little.db;

object Func1 {
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
//
//
//尾递归 - 阶加
object Loop1 {
  //
  def main(args: Array[String]): Unit = {
    def num = 50000
    //
    println("Loop1:" + funCollect(num))
  }

  def funCollect(a: Long): Long = {
    def funCollect$(a: Long, tmp: Long): Long = {
      if (a == 0) tmp else funCollect$(a - 1, a + tmp)
    }
    //
    funCollect$(a, 1)
  }

}

// @tailrec  尾递归
//def factorial(n: Int): Int = {
//  @tailrec
//  def loop(acc: Int, n: Int): Int =
//    if (n == 0) acc else loop(n * acc, n - 1)
//
//  loop(1, n)
//}
object Simple {
  implicit def wrapper(s: java.lang.String) = new { def wrap = "--" + s + "--" }
}


//val file = spark.textFile("hdfs://...")
//val counts = file.flatMap(line => line.split(" "))
//  .map(word => (word, 1))
//  .reduceByKey(_ + _)
//counts.saveAsTextFile("hdfs://...")
