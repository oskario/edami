import algorithms.KMeans
import com.typesafe.scalalogging.LazyLogging
import example.MyInt
import quality.Quality

object ClusteringQualityExample extends LazyLogging {
  def run() = {
    // configuration
    val dataToBeClustered = IndexedSeq(1, 2, 9, 10)
    val numberOfClusters = Seq(1, 2, 3, 4, 5, 6, 7, 8)
    val minChangeInDispersion = 0.01
    val maxNumberOfIterations = 20
    def distanceFunction: (Int, Int) => Double = (a, b) => math.abs(a - b)
    def meanFunction(dataToSum: Seq[Int]): Int = if (dataToSum.nonEmpty)
      dataToSum.sum / dataToSum.size
    else
      0

    logger.info("Starting the algorithms.KMeans Example..")
    logger.info("Data to be clustered: " + dataToBeClustered)

    // running the algorithm

    val qualities = numberOfClusters map { k =>
      logger.info(s"Clustering with $k clusters")
      def clusters = KMeans(dataToBeClustered, k, distanceFunction, minChangeInDispersion, maxNumberOfIterations, meanFunction)
      // printing the result
      logger.info("Clustering result: " + clusters)

      Quality.getClusteringQuality(clusters.map((cluster) => cluster.map(new MyInt(_))))
    }
  }
}
