import breeze.linalg.DenseMatrix

import scala.collection.immutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * Companion object to KMeans class used to run KMeans algorithm.
 */
object KMeans {

  /**
   * Function that implements the kmeans algorithm.
   * @param data input data that will be clustered
   * @param k number of clusters
   * @param distanceFun distance function
   * @param minChangeInDispersion if the dispersion change between two iterations is lower that this value, the process is stopped
   * @param maxIterations maximal number of iterations
   * @param meanFunction mean function used to calculate new centroids by given clusters
   * @tparam A the type of input data
   * @return clusters represented as lists of elements
   */
  def apply[A](data: IndexedSeq[A],
               k: Int,
               distanceFun: (A, A) => Double,
               minChangeInDispersion: Double,
               maxIterations: Int,
                meanFunction: Seq[A] => A): Seq[Seq[A]] = {
    val kmeans = new KMeans(data, k, distanceFun, minChangeInDispersion, maxIterations, meanFunction)
    kmeans.run
  }
}

/**
 * A class used to run KMeans algorithm. More comment can be found in KMeans object.
 */
class KMeans[A](data: IndexedSeq[A],
                k: Int,
                distanceFun: (A, A) => Double,
                minChangeInDispersion: Double,
                maxIterations: Int,
                meanFunction: Seq[A] => A) {
                
  // TODO: Change to time seed after all tests. 
  lazy val random = new Random(17)

  /**
   * Runs the algorithm.
   * @return clusters of elements (as list of lists)
   */
  def run: Seq[Seq[A]] = {
    val bestCentroids = findBestCentroids()
    val (_, finalMemberships) = getDispersionWithClusterMemberships(bestCentroids)
    getClustersByMemberships(finalMemberships)
  }

  /**
   * Gets clusters of elements by membership array.
   * @param memberships cluster memberships 
   * @return the clusters of element
   */
  def getClustersByMemberships(memberships: immutable.IndexedSeq[Int]): Seq[Seq[A]] = {
    val clusters =  IndexedSeq.fill(k)(new ListBuffer[A])
    memberships.zipWithIndex.map { case (clusterId, index)  =>
      clusters(clusterId) += data(index)
    }
    clusters.map(_.toSeq)
  }

  /**
   * Finds best centroids by dispersion property.
   * @return best centroids
   */
  def findBestCentroids(): IndexedSeq[A] = {
    findBestCentroidsStartingFrom(getInitialCentroids)
  }

  /**
   * Finds best centroids starting from given list.
   * @param initialCentroids initialCentroids
   * @return best centroids found with given parameters
   */
  def findBestCentroidsStartingFrom(initialCentroids: IndexedSeq[A]): IndexedSeq[A] = {
    var currentCentroids = initialCentroids
    var i = 0
    var lastDispersion = Double.PositiveInfinity
    var dispersionDiff = Double.PositiveInfinity
    while (i < maxIterations && dispersionDiff > minChangeInDispersion) {
      val (dispersion, clusterMemberships) = getDispersionWithClusterMemberships(currentCentroids)
      currentCentroids = getCentroidsByClusterMemberships(clusterMemberships)
      dispersionDiff = math.abs(lastDispersion - dispersion)
      lastDispersion = dispersion
      i += 1
    }
    currentCentroids
  }

  /**
   * Gets dispersion value and memberships array based on given centroids.
   * @param centroids centroids
   * @return pair of dispersion and memberships array
   */
  def getDispersionWithClusterMemberships(centroids: IndexedSeq[A]) = {
    val (squaredDistances, clusterMemberships) = data.par.map { point =>
      val distances = centroids.map(c => distanceFun(c, point))
      val (shortestDistance, closestCentroid) = distances.zipWithIndex.min
      (shortestDistance * shortestDistance, closestCentroid)
    }.toIndexedSeq.unzip
    (squaredDistances.sum, clusterMemberships)
  }

  /**
   * Gets centroids by given cluster memberships using the mean function.
   * @param clusterMemberships cluster memberships
   * @return new centroids based on given cluster memberships
   */
  private def getCentroidsByClusterMemberships(clusterMemberships: IndexedSeq[Int]): IndexedSeq[A] = {
    val clusters = IndexedSeq.fill(k)(new ListBuffer[A])
    val counts = Array.fill(k)(0)
    var index = 0
    while (index < data.length) {
      val clusterId = clusterMemberships(index)
      if (clusterId > -1) {
        clusters(clusterId) += data(index)
        counts(clusterId) += 1
      }
      index += 1
    }
    (for (centroid <- clusters) yield {
      meanFunction(centroid)
    }).toIndexedSeq
  }

  /**
   * Gets initial centroid.
   */
  private def getInitialCentroids =
    random.shuffle(data).take(k)
}
