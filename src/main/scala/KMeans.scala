import breeze.linalg.DenseMatrix

object KMeans {

  def apply[A](data: DenseMatrix[A],
               k: Int,
               distanceFun: (A, A) => Double,
               minChangeInDispersion: Double,
               maxIterations: Int,
               fixedSeedForRandom: Boolean = false) = kmeans(data, k, distanceFun, minChangeInDispersion, maxIterations, fixedSeedForRandom)

  // TODO: implement this method
  // hint: https://github.com/scalanlp/nak/blob/master/src/main/scala/nak/cluster/Kmeans.scala
  def kmeans[A](data: DenseMatrix[A],
             k: Int,
             distanceFun: (A, A) => Double,
             minChangeInDispersion: Double,
             maxIterations: Int,
             fixedSeedForRandom: Boolean = false) = ???
}
