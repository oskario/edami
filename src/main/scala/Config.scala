
case class Config(
                   inputDir: String,
                   k: Int,
                   pattern: String = ".*.jpg",
                   minChangeInDispersion: Double = 0.01,
                   maxNumberOfIterations: Int = 20,
                   output: Option[String] = None
                   )
