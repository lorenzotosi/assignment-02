ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.3"

lazy val root = (project in file("."))
  .settings(
    name := "assignment-02"
  )

libraryDependencies ++= Seq(
  "io.reactivex.rxjava3" % "rxjava" % "3.1.10"
)

libraryDependencies += "com.github.javaparser" % "javaparser-core" % "3.26.4"

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"

libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32"

libraryDependencies ++= {
  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "21" classifier osName)
}

libraryDependencies += "com.brunomnsilva" % "smartgraph" % "2.0.0"

libraryDependencies += "com.github.javaparser" % "javaparser-symbol-solver-core" % "3.26.4"