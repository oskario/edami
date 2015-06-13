package quality

/**
 * Interface for measurement of clustering quality.
 */
trait QualityMeasurer[T <: WithMathFunctions[T]] {
  def getQuality(clusters: Seq[Seq[T]])(implicit meanFunction: Seq[T] => T): Double
}
