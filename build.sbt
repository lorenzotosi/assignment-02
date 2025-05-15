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

libraryDependencies += "com.github.javaparser" % "javaparser-symbol-solver-core" % "3.26.4"

libraryDependencies ++= Seq(
  "io.vertx" % "vertx-core" % "4.5.14",
  "io.vertx" % "vertx-web" % "4.5.14"
)