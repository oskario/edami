import java.io.File
import java.nio.file.{Files, Paths}

import breeze.linalg._
import com.typesafe.scalalogging.LazyLogging

object Main extends App with LazyLogging {

  // TODO: input and pattern as command line parameters
  val input = "/home/oskar/workspace/edami/src/main/resources/"
  val pattern = ".*.jpg"

  if (Files.notExists(Paths.get(input))) {
    logger.error(s"File $input does not exist!")
  } else {
    val files = listFiles(input, pattern)
    logger.info(s"Found ${files.length} files")
    process(files)
  }

  def listFiles(directory: String, pattern: String): Seq[String] = {
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

  def process(inputFiles: Seq[String]): Unit = {
    val vectors = inputFiles.map(process)

    logger.info(s"Calculating histogram matrix...")
    //    val va = vectors.map(_.data): _*
    //    val matrix = Matrix[Int, Int](va(0), va(1))
    //    val matrix = DenseMatrix[Int, Int](768, inputFiles.length)
    val matrix = DenseMatrix[Int, Int]

    vectors.foreach { vector =>
      vector.data.
    }

    logger.info(s"Reducing the matrix...")
    val pca = PCA(matrix, 2)

    logger.info(s"Clustering...")
    val result = KMeans(pca, 5)

    // TODO: plotting doesn't work :(
    //    val f1 = Figure("data")
    //    val f2 = Figure("pca")
    //    f1.subplot(0) += hist(matrix)
    //    f1.subplot(0) += scatter(matrix(::, 0), matrix(::, 3), { _ => 0.1 })
    //    f2.subplot(0) += scatter(pcaRes(::, 0), pcaRes(::, 1), { _ => 0.1 })
  }

  private def process(inputFile: String): SparseVector[Int] = {
    logger.info(s"Loading $inputFile...")

    val image = Image.fromFile(inputFile)
    // image.show()
    val result = processImage(image)
    logger.info("Image processed!")
    result
  }

  private def processImage(image: Image): SparseVector[Int] = {
    val hueHistogram = image.histogramFor(_.color.h)
    val saturationHistogram = image.histogramFor(_.color.s)
    val valueHistogram = image.histogramFor(_.color.v)

    val result = SparseVector.zeros[Int](768)
    hueHistogram.foreach { case (hue, count) =>
      result.update(1 * (hue / 360 * 255).toInt, count)
    }
    saturationHistogram.foreach { case (saturation, count) =>
      result.update(2 * (1 + (saturation * 255).toInt), count)
    }
    valueHistogram.foreach { case (value, count) =>
      result.update(3 * value.toInt, count)
    }
    result
  }

  //  def printHistogram(histogram: Seq[(Double, Int)]): Unit = {
  //    println(histogram.map { x =>
  //      val p = (1 to x._2 / 100).map(_ => "#").mkString("")
  //      s"${x._1} -> $p"
  //    }.mkString("\n"))
  //  }
}