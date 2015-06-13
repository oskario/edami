import breeze.linalg.DenseVector
import breeze.stats.distributions.Gaussian
import com.typesafe.scalalogging.LazyLogging
import example.MyInt
import quality.{Quality, FSquareMeasurement}
import breeze.plot._
import breeze.linalg._

object ClusteringQualityExample extends LazyLogging {
  def run() = {
    // configuration
    val dataToBeClustered = IndexedSeq(1, 2, 9, 10)
    val numberOfClusters = Seq(1,2,3,4)
    val minChangeInDispersion = 0.01
    val maxNumberOfIterations = 20
    def distanceFunction: (Int, Int) => Double = (a, b) => math.abs(a - b)
    def meanFunction(dataToSum: Seq[Int]): Int = dataToSum.sum / dataToSum.size

    logger.info("Starting the KMeans Example..")
    logger.info("Data to be clustered: " + dataToBeClustered)

    // running the algorithm

    val qualities = numberOfClusters map { k =>
      logger.info(s"Clustering with $k clusters")
      def clusters = KMeans(dataToBeClustered, k, distanceFunction, minChangeInDispersion, maxNumberOfIterations, meanFunction)
      // printing the result
      logger.info("Clustering result: " + clusters)

      Quality.getClusteringQuality(clusters.map( (cluster) => cluster.map(new MyInt(_))))
    }

    Quality.plotQualities(numberOfClusters, qualities)
  }
}
