import java.io.{IOException, File}
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._
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
    if (Files.exists(output)) deleteDirWithContents(output)
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

  def deleteDirWithContents(directory: Path) = {
    Files.walkFileTree(directory, new SimpleFileVisitor[Path]() {

      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        Files.delete(file)
        FileVisitResult.CONTINUE
      }

      override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
        Files.delete(dir)
        FileVisitResult.CONTINUE
      }
    })
  }
}
