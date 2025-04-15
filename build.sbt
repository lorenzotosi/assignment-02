ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.3"

lazy val root = (project in file("."))
  .settings(
    name := "assignment-02"
  )

libraryDependencies ++= Seq(
  "io.vertx" % "vertx-core" % "4.5.14",
  "io.vertx" % "vertx-web" % "4.5.14"
)

libraryDependencies += "com.github.javaparser" % "javaparser-core" % "3.26.4"
