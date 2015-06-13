package quality

/**
 * Trait that contains several mathematical functions.
 */
trait WithMathFunctions[T] {
  def +(other: T): T
  def *(other: Int): T
  def *(other: T): T
  def distance(other: T):Double
}
