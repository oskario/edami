import breeze.linalg._

// TODO: Check if the following implementation is correct
// (taken from: https://gist.github.com/tyrcho/5884241)
// alternatively take a look at this:
// https://github.com/apache/spark/blob/3c0156899dc1ec1f7dfe6d7c8af47fa6dc7d00bf/mllib/src/main/scala/org/apache/spark/mllib/feature/PCA.scala
object PCA {

  def apply(data: DenseMatrix[Int], components: Int) = pca(data, components)

  def pca(data: DenseMatrix[Double], components: Int) = {
    val d = zeroMean(data)
    val dSvd = svd(d.t)
    val model = dSvd.rightVectors(0 until components, ::)
    val filter = model.t * model
    filter * d
  }

  private def mean(v: Vector[Double]) = v.valuesIterator.sum / v.size

  private def zeroMean(m: DenseMatrix[Double]) = {
    val copy = m.copy
    for (c <- 0 until m.cols) {
      val col = copy(::, c)
      val colMean = mean(col)
      col -= colMean
    }
    copy
  }
}