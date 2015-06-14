import java.io.File
import java.nio.file.{Files, Paths}

import algorithms.DBSCAN.Point
import algorithms.{DBSCAN, KMeans}
import com.typesafe.scalalogging.LazyLogging
import scopt._

import scala.util.Random

object Main extends App with LazyLogging {

  val parser = new OptionParser[Config]("edami") {
    head("edami", "0.1.0")
    opt[String]('i', "input").required().action { (x, c) =>
      c.copy(inputDir = x)
    }.text("input directory")
    opt[Int]('k', "clusters").required().action { (x, c) =>
      c.copy(k = x)
    }.text("number of clusters (k)")
    opt[String]('p', "pattern").action { (x, c) =>
      c.copy(pattern = x)
    }.text("input file pattern (e.g. '.*.jpg')")
    opt[Double]("min-dispersion").action { (x, c) =>
      c.copy(minChangeInDispersion = x)
    }.text("min change in dispersion")
    opt[Int]("max-iterations").action { (x, c) =>
      c.copy(maxNumberOfIterations = x)
    }.text("max number of iterations")
    opt[Int]("max-iterations").action { (x, c) =>
      c.copy(maxNumberOfIterations = x)
    }.text("max number of iterations")
    opt[String]('o', "output").action { (x, c) =>
      c.copy(output = Option(x))
    }.text("output directory")
    help("help").text("prints this usage text")
  }

  parser.parse(args.toSeq, Config("", 0)) match {
    case Some(parsed) =>
      implicit val config = parsed
      run(dbscan)
    case None =>
      // invalid args
  }

  def run(method: (Seq[(Image, Seq[Int])]) => Seq[Seq[(Image, Seq[Int])]])(implicit config: Config) = {
    if (Files.notExists(Paths.get(config.inputDir))) {
      logger.error(s"File ${config.inputDir} does not exist!")
    } else {
      val files = Random.shuffle(listFiles(config.inputDir, config.pattern)).take(20)
      logger.info(s"Found ${files.length} files")
      val clusters = process(files, method)

      config.output.foreach { o =>
        ImagesSaver.saveImageClusters(clusters, o)
      }
    }
  }

  private def process(inputFiles: Seq[String], cluster: (Seq[(Image, Seq[Int])]) => Seq[Seq[(Image, Seq[Int])]])(implicit config: Config): Seq[Seq[Image]] = {
    logger.info(s"Calculating histograms...")
    val data = inputFiles.map(process)

    logger.info(s"Clustering...")
    val result = cluster(data)

    logger.debug(s"Result:")
    result.zipWithIndex.map { case (xa, i) => logger.info(s"Cluster ${i+1}: ${xa.map(_._1.name).mkString(", ")}") }

    result.map(_.map(_._1))
  }

  private def process(inputFile: String): (Image, Seq[Int]) = {
    logger.debug(s"Loading $inputFile...")

    val image = Image.fromFile(inputFile)
    // image.show()
    val histogram = image.hsvHistogram
    logger.debug("Image processed!")
    (image, histogram)
  }

  private def listFiles(directory: String, pattern: String = ".*"): Seq[String] = {
    val d = new File(directory)
    if (d.exists && d.isDirectory) {
      d.listFiles
        .filter(f => f.isFile && f.getName.matches(pattern))
        .map(x => x.getAbsolutePath)
        .sorted
    } else {
      Seq()
    }
  }


  def distanceFunction: ((Image, Seq[Int]), (Image, Seq[Int])) => Double = { (a, b) =>
    val differences = a._2.zip(b._2).map { case (v1, v2) =>
      math.pow(v2 - v1, 2)
    }
    Math.sqrt(differences.sum)
  }

  private def kmeans(data: Seq[(Image, Seq[Int])])(implicit config: Config): Seq[Seq[(Image, Seq[Int])]] = {

    def meanFunction(dataToSum: Seq[(Image, Seq[Int])]): (Image, Seq[Int]) = if (dataToSum.isEmpty)
      (null, Seq.fill(768)(0))
    else
      (dataToSum.head._1, vectorMean(dataToSum.map(_._2)))

    def vectorMean(vectors: Seq[Seq[Int]]): Seq[Int] =
      for {
        i <- vectors.head.indices
      } yield vectors.map( v => v(i)).sum/vectors.size

    KMeans[(Image, Seq[Int])](data.toIndexedSeq, config.k, distanceFunction, config.minChangeInDispersion, config.maxNumberOfIterations, meanFunction)
  }

  private def dbscan(data: Seq[(Image, Seq[Int])])(implicit config: Config): Seq[Seq[(Image, Seq[Int])]] = {

    def getNeighbours(epsilon: Double, distance: ((Image, Seq[Int]), (Image, Seq[Int])) => Double)(point: Point[(Image, Seq[Int])], points: Seq[Point[(Image, Seq[Int])]]): Seq[Point[(Image, Seq[Int])]] = {
      points.filter(neighbour => distance(neighbour.value, point.value) < epsilon)
    }

    def isCorePoint(minPoints: Int)(point: Point[(Image, Seq[Int])], neighbours: Seq[Point[(Image, Seq[Int])]]): Boolean = {
      neighbours.size >= minPoints
    }

    DBSCAN[(Image, Seq[Int])](data, getNeighbours(10000, distanceFunction), isCorePoint(5))
  }
}