package net.hasor.test;

object Loop1 {
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