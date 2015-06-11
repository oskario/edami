import java.io.File
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JLabel, JOptionPane, JPanel}

object Image {

  def fromFile(filename: String): Image = Image(filename)

  def apply(filename: String): Image = Image(new File(filename))

  implicit def array2Color(array: Array[Float]): Color = {
    Color(array(2).toInt, array(1).toInt, array(0).toInt)
  }
}

final case class Image(file: File) {
  import Image._

  val image = ImageIO.read(file)
  val height = image.getHeight
  val width = image.getWidth

  val pixels: Seq[Pixel] = for {
    x <- 1 until width
    y <- 1 until height
  } yield {
    val colorArray = Array[Float](0, 0, 0)
    image.getRaster.getPixel(x, y, colorArray)
    Pixel(x, y, colorArray)
  }

  lazy val histogram = histogramFor(_.color.avg)

  def histogramFor[A](f: Pixel => A)(implicit ordering: Ordering[A]): Seq[(A, Int)] = {
    pixels.groupBy(f).mapValues(_.length).toSeq.sortBy(_._1)
  }

  def show(): Unit = {
    val panel = new JPanel()
    panel.add(new JLabel(new ImageIcon(image)))
    JOptionPane.showMessageDialog(null, panel, file.getName, JOptionPane.INFORMATION_MESSAGE)
  }
}

final case class Pixel(x: Int, y: Int, color: Color)
final case class Color(r: Int, g: Int, b: Int) {
  lazy val avg = (r + g + b) / 3
}

