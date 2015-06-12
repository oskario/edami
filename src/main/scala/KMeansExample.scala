import com.typesafe.scalalogging.LazyLogging

/**
 * Example that shows how the implementation of KMeans algorithm works.
 */
object KMeansExample extends LazyLogging {
  def run() = {
    // configuration
    val dataToBeClustered = Vector(1, 2, 9, 10)
    val numberOfClusters = 2
    val minChangeInDispersion = 0.01
    val maxNumberOfIterations = 20
    def distanceFunction: (Int, Int) => Double = (a, b) => math.abs(a - b)
    def meanFunction(dataToSum: Seq[Int]): Int = dataToSum.sum / dataToSum.size

    logger.info("Starting the KMeans Example..")
    logger.info("Data to be clustered: " + dataToBeClustered)

    // running the algorithm
    def clusters = KMeans(dataToBeClustered, numberOfClusters, distanceFunction, minChangeInDispersion, maxNumberOfIterations, meanFunction)

    // printing the result
    println("Clustering result: " + clusters)
  }
}
