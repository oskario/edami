import java.io.File
import javax.imageio.ImageIO

/**
 * Object used to save images.
 */
object ImagesSaver {
  /**
   * Saves image clusters creating one directory per cluster in the outputDir.
   * @param imageClusters image clusters
   * @param outputDir output dir
   */
  def saveImageClusters(imageClusters: Seq[Seq[Image]], outputDir: String) = {
    var clusterNumber = 0
    imageClusters.foreach(cluster => {
      new File(s"$outputDir\\$clusterNumber").mkdir()
      cluster.foreach(image => {
        val outputfile = new File(s"$outputDir\\$clusterNumber\\${image.file.getName}")
        ImageIO.write(image.image, "jpg", outputfile)
      }
      )
      clusterNumber = clusterNumber + 1
    })
  }
}
