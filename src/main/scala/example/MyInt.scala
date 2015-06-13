package example

import quality.WithMathFunctions

object MyInt {
  def apply(int: Int) = new MyInt(int)
  implicit def intToMyInt(int: Int): MyInt = new MyInt(int)
  implicit def meanFunction(elements: Seq[MyInt]): MyInt = elements.map(_.value).sum / elements.size
}

class MyInt(val value: Int) extends WithMathFunctions[MyInt] {
  override def distance(other: MyInt): Double = math.abs(value - other.value)
  override def +(other: MyInt): MyInt = value + other.value
  override def *(other: Int): MyInt = value * other
  override def *(other: MyInt): MyInt = value * other.value
}

