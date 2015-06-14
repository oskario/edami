package algorithms

import algorithms.DBSCAN._

import scala.collection.mutable.{ListBuffer, Set => MutableSet}

object DBSCAN {

  def apply[A](
                data: Seq[A],
                getNeighbours: (Point[A], Seq[Point[A]]) => Seq[Point[A]],
                isCorePoint: (Point[A], Seq[Point[A]]) => Boolean): Seq[Seq[A]] = {
    val gdbscan = new DBSCAN(getNeighbours, isCorePoint)
    gdbscan.cluster(data).map(_.points.map(_.value))
  }

  /** Single point in the cluster */
  case class Point[T](row: Int)(val value: T)

  /** The cluster itself */
  case class Cluster[T](id: Long) {
    private var _points = ListBuffer[Point[T]]()

    def add(p: Point[T]) {
      _points += p
    }

    def points: Seq[Point[T]] = Seq(_points: _*)
  }
}

/**
 * A class used to run DBSCAN algorithm
 * @param getNeighbours lists all point's neighbours
 * @param isCorePoint determine if a given points is a core point among its neighbours
 */
class DBSCAN[T](
                  getNeighbours: (Point[T], Seq[Point[T]]) => Seq[Point[T]],
                  isCorePoint: (Point[T], Seq[Point[T]]) => Boolean) {

  /**
   * Performs the clustering.
   *
   * @param data data to be clustered
   */
  def cluster(data: Seq[T]): Seq[Cluster[T]] = {

    val visited = MutableSet[Point[T]]()
    val clustered = MutableSet[Point[T]]()

    val points = for (row <- 0 until data.length) yield Point(row)(data(row))

    points.collect {
      case point@Point(row) if !(visited contains point) =>
        val neighbours = getNeighbours(point, points.filterNot(_.row == point.row))
        if (isCorePoint(point, neighbours)) {
          visited add point
          val cluster = Cluster[T](row)
          expand(point, neighbours, cluster)(points, visited, clustered)
          Some(cluster)
        } else {
          None
        }
    }.flatten
  }

  private def expand(point: Point[T], neighbours: Seq[Point[T]], cluster: Cluster[T])(implicit points: Seq[Point[T]], visited: MutableSet[Point[T]], clustered: MutableSet[Point[T]]) {
    cluster.add(point)
    clustered.add(point)

    neighbours.foldLeft(neighbours) {
      case (neighbourhood, neighbour@Point(row)) =>
        val newNeighbours = if (!(visited contains neighbour)) {
          visited.add(neighbour)
          getNeighbours(neighbour, points.filterNot(_.row == neighbour.row))
        } else {
          Nil
        }

        if (!(clustered contains neighbour)) {
          cluster.add(neighbour)
          clustered.add(neighbour)
        }

        if (isCorePoint(neighbour, neighbourhood)) neighbourhood ++ newNeighbours
        else neighbourhood
    }
  }
}