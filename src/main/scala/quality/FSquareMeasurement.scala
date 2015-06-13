package quality

/**
 * The implementation of f square inter-cluster statistics.
 */
class FSquareMeasurement[T <: WithMathFunctions[T]] extends QualityMeasurer[T] {
  override def getQuality(clusters: Seq[Seq[T]])(implicit meanFunction: Seq[T] => T): Double = {
    clusters.foldLeft(0.0) {
      (result, cluster) => result + cluster.foldLeft(0.0) {
        (innerResult, value) => innerResult + math.pow(value.distance(meanFunction(cluster)), 2)
      }
    }
  }
}