import java.nio.file.{Files, Paths}

import com.typesafe.scalalogging.LazyLogging

object Main extends App with LazyLogging {

  val projectPath = getClass.getResource("").getPath
  val input = projectPath + "sample.jpg"

  if (Files.notExists(Paths.get(input))) {
    logger.error(s"File $input does not exist!")
  } else {
    process(input)
  }

  def process(inputFile: String): Unit = {
    logger.info(s"Loading $inputFile...")
    val image = Image.fromFile(inputFile)

    image.show()

    // print histogram
    println(image.histogram.map { x =>
      val p = (1 to x._2/100).map(_ => "#").mkString("")
      s"${x._1} -> $p"
    }.mkString("\n"))
  }
}