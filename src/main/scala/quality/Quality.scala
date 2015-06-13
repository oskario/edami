package quality

import breeze.linalg.DenseVector
import breeze.plot._
import breeze.linalg._

object Quality {
  def getClusteringQuality[T <: WithMathFunctions[T]](clusters: Seq[Seq[T]])(implicit meanFunction: Seq[T] => T): Quality = {
    val f2measure = new FSquareMeasurement[T].getQuality(clusters)
    val b2measure = new BSquareMeasurement[T].getQuality(clusters)
    Quality(f2measure, b2measure)
  }
}

case class Quality(f2: Double, b2: Double) {
  val f2Divb2 = f2 / b2
}
