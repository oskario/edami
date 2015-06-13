package quality

/**
 * The implementation of b square inter-cluster statistics.
 */
class BSquareMeasurement[T <: WithMathFunctions[T]] extends QualityMeasurer[T] {
  override def getQuality(clusters: Seq[Seq[T]])(implicit meanFunction: Seq[T] => T): Double = {
    val leftSide: T = {
      val resultsToSum = clusters.map(cluster => meanFunction(cluster) * meanFunction(cluster) * cluster.size)
      resultsToSum.drop(1).foldLeft(resultsToSum.head)(_ + _)
    }
    val rightSide = meanFunction(clusters.flatMap(c => c)) * clusters.map(_.size).sum
    leftSide.distance(rightSide)
  }
}
