import breeze.linalg.DenseVector
import breeze.plot._
import breeze.linalg._
import quality.Quality

/**
 * Object used to create plots.
 */
object Plotter {
  def plotQualities(numberOfClusters: Seq[Int], qualities: Seq[Quality]) = {
    val f = Figure("Clustering quality for different k")

    val f2plot = f.subplot(3, 1, 0)
    f2plot += plot(DenseVector(numberOfClusters.map(_.toDouble).toArray), DenseVector(qualities.map(_.f2).toArray))
    f2plot.xlabel = "K"
    f2plot.ylabel = "F^2"
    f2plot.title = "F^2"

    val b2plot = f.subplot(3, 1, 1)
    b2plot += plot(DenseVector(numberOfClusters.map(_.toDouble).toArray), DenseVector(qualities.map(_.b2).toArray))
    b2plot.xlabel = "K"
    b2plot.ylabel = "B^2"
    b2plot.title = "B^2"

    val f2divb2plot = f.subplot(3, 1, 2)
    f2divb2plot += plot(DenseVector(numberOfClusters.map(_.toDouble).toArray), DenseVector(qualities.map(_.f2Divb2).toArray))
    f2divb2plot.xlabel = "K"
    f2divb2plot.ylabel = "F^2/B^2"
    f2divb2plot.title = "F^2/B^2"
  }
}
