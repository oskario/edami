import java.io.File
import java.nio.file.{Paths, Files}
import javax.imageio.ImageIO

import com.typesafe.scalalogging.LazyLogging

/**
 * Object used to save images.
 */
object ImagesSaver extends LazyLogging {
  /**
   * Saves image clusters creating one directory per cluster in the outputDir.
   * @param imageClusters image clusters
   * @param outputDir output dir
   */
  def saveImageClusters(imageClusters: Seq[Seq[Image]], outputDir: String) = {
    logger.info(s"Saving ${imageClusters.length} clusters to $outputDir")
    val output = Paths.get(outputDir)
    Files.deleteIfExists(output)
    Files.createDirectory(output)

    imageClusters.zipWithIndex.foreach { case (cluster, i) =>
      val clusterNumber = i + 1
      val clusterDir = Paths.get(outputDir + File.separator + clusterNumber)
      Files.createDirectory(clusterDir)

      cluster.foreach { image =>
        val outputFilePath = outputDir + File.separator + clusterNumber + File.separator + image.file.getName
        val outputFile = new File(outputFilePath)
        ImageIO.write(image.image, "jpg", outputFile)
      }
    }
  }
}
