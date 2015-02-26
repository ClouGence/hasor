package net.hasor.test;

object Operator {
  //隐式转换
  implicit def intToRational(x: Int) = new Rational(x, 1)

  def main(args: Array[String]) {
    val a = new Rational(1, 3) // -- 1/3
    val b = new Rational(1, 3) // -- 1/3

    println(a + b)
    println(a - b)
    println(a * b)
    println(a / b)
    println(2 * a)
  }
  //
  //
  //
  class Rational(a: Int, b: Int) {
    require(b != 0) //分母不为零

    val g = gcd(a, b)
    val member = a / g
    val denominator = b / g

    //尾递归-求最大公约数
    private def gcd(a: Int, b: Int): Int =
      if (b == 0) a else gcd(b, a % b)

    //重写toString
    override def toString() =
      this.member + "/" + this.denominator

    //分数加法
    def +(that: Rational): Rational =
      new Rational(this.member * that.denominator + that.member * this.denominator,
        that.denominator * this.denominator)

    //分数减法
    def -(that: Rational): Rational =
      new Rational(this.member * that.denominator - that.member * this.denominator,
        that.denominator * this.denominator)

    //分数乘法
    def *(that: Rational): Rational =
      new Rational(this.member * that.member,
        that.denominator * this.denominator)

    //分数除法
    def /(that: Rational): Rational =
      new Rational(this.member * that.denominator,
        that.member * this.denominator)
  }
}