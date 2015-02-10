// https://github.com/apache/spark/blob/master/examples/src/main/scala/org/apache/spark/examples/streaming/ActorWordCount.scala
package first;

object Hello {
  def sort(xs: Array[Int]): Array[Int] =
    if (xs.length <= 1) xs
    else {
      val pivot = xs(xs.length / 2)
      Array.concat(
        sort(xs filter (pivot >)),
        xs filter (pivot ==),
        sort(xs filter (pivot <)))
    }

  def main(args: Array[String]) {
    println("Hello Wrod");
    //
    for (v <- args) {
      println(v);
    }
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


//val file = spark.textFile("hdfs://...")
//val counts = file.flatMap(line => line.split(" "))
//  .map(word => (word, 1))
//  .reduceByKey(_ + _)
//counts.saveAsTextFile("hdfs://...")
