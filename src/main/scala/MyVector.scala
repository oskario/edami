import quality.WithMathFunctions

object MyVector {
  implicit def vectorMean(vectors: Seq[MyVector]): MyVector = {
    if (!vectors.isEmpty) {
      val result = for {
        i <- vectors.head.indices
      } yield vectors.map(v => v.values(i)).sum / vectors.size
      new MyVector(result)
    } else {
      MyVector.zeros
    }
  }

  val zeros = new MyVector(Seq.fill(768)(0))
}

class MyVector(val values: Seq[Int]) extends WithMathFunctions[MyVector] {

  val indices = values.indices

  override def +(other: MyVector): MyVector = new MyVector(values.zip(other.values).map { case (a, b) => a + b })

  override def distance(other: MyVector): Double = {
    val differences = values.zip(other.values).map { case (v1, v2) =>
      math.pow(v2 - v1, 2)
    }
    Math.sqrt(differences.sum)
  }

  override def *(other: Int): MyVector = new MyVector(values.map(_ * other))

  override def *(other: MyVector): MyVector = new MyVector(values.zip(other.values).map { case (a, b) => a * b })
}
