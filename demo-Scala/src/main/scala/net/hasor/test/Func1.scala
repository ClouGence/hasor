package net.hasor.test;

object Func1 {
  def main(args: Array[String]) {
    //-----------------------------------------------
    //
    val str = "AB" + "CD"
    println("ABCD==" + (str == "ABCD"))
    //-----------------------------------------------
    //
    val list1 = List("a1") :: List("b1") :: List("c1")
    println(list1)
    //-----------------------------------------------
    //
    val list2 = "a" :: "b" :: "c" :: Nil
    println(list2)
    //-----------------------------------------------
    //
    def oriVal() = 1
    def newVal(i: Int) = oriVal + 1
    def valA: Int = newVal(10)
    def valB: Int = newVal(20)
    //
    println(sum(valA, valB))
    //
    (1).unary_-
  }

  def sum(a: Int, b: Int): Int = {
    a + b;
  }
}