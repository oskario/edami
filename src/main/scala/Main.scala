import java.io.File
import java.nio.file.{Files, Paths}

import breeze.linalg._
import com.typesafe.scalalogging.LazyLogging

import scala.util.Random

object Main extends App with LazyLogging {

  // TODO: make this values app parameters
  val input = "/home/oskar/workspace/edami/src/main/resources/wang"
  val pattern = ".*.jpg"
  val minChangeInDispersion = 0.01
  val maxNumberOfIterations = 20
  val k = 10
  val output = Option("/tmp/output")

  def distanceFunction: ((Image, Seq[Int]), (Image, Seq[Int])) => Double = { (a, b) =>
    val differences = a._2.zip(b._2).map { case (v1, v2) =>
      v2 - v1
    }
    Math.sqrt(differences.sum)
  }
  def meanFunction(dataToSum: Seq[(Image, Seq[Int])]): (Image, Seq[Int]) = dataToSum(dataToSum.length / 2)

  if (Files.notExists(Paths.get(input))) {
    logger.error(s"File $input does not exist!")
  } else {
    val files = Random.shuffle(listFiles(input, pattern)).take(70)
    logger.info(s"Found ${files.length} files")
    val clusters = process(files)

    output.foreach { o =>
      ImagesSaver.saveImageClusters(clusters, o)
    }
  }

  def listFiles(directory: String, pattern: String = ".*"): Seq[String] = {
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

  def process(inputFiles: Seq[String]): Seq[Seq[Image]] = {
    logger.info(s"Calculating histograms...")
    val data = inputFiles.map(process).toIndexedSeq

    logger.info(s"Clustering...")
    val result = KMeans[(Image, Seq[Int])](data, k, distanceFunction, minChangeInDispersion, maxNumberOfIterations, meanFunction)

    logger.debug(s"Result:")
    result.zipWithIndex.map { case (xa, i) => logger.info(s"Cluster ${i+1}: ${xa.map(_._1.name).mkString(", ")}") }

    result.map(_.map(_._1))
  }

  private def process(inputFile: String): (Image, Seq[Int]) = {
    logger.debug(s"Loading $inputFile...")

    val image = Image.fromFile(inputFile)
    // image.show()
    val histogram = getHistogram(image)
    logger.debug("Image processed!")
    (image, histogram)
  }

  private def getHistogram(image: Image): Seq[Int] = {
    val hueHistogram = image.histogramFor(_.color.h)
    val saturationHistogram = image.histogramFor(_.color.s)
    val valueHistogram = image.histogramFor(_.color.v)

    val result = DenseVector.zeros[Int](768)
    hueHistogram.foreach { case (hue, count) =>
      result.update(1 * (hue / 360 * 255).toInt, count)
    }
    saturationHistogram.foreach { case (saturation, count) =>
      result.update(2 * (1 + (saturation * 255).toInt), count)
    }
    valueHistogram.foreach { case (value, count) =>
      result.update(3 * value.toInt, count)
    }
    result.toScalaVector().toSeq
  }
}