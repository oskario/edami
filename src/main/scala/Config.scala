
case class Config(
                   inputDir: String,
                   k: Int,
                   clusteringType: String,
                   pattern: String = ".*.jpg",
                   minChangeInDispersion: Double = 0.01,
                   maxNumberOfIterations: Int = 20,
                   output: Option[String] = None,
                   epsilon: Int = 10000,
                   minNeighbours: Int = 5
                   )
