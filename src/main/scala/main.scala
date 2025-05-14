import io.vertx.core.Vertx
import lib.DependencyAnalyserLib.*

import java.io.File

@main def main(): Unit =
  val file = File("src/main/java/test/DummyClass.java")
  val analyser = DependencyAnalyser()

  val v = Vertx.vertx()
  v.deployVerticle(analyser)

  println("Analizzando: " + file.getAbsolutePath)
  println("TEST 1:")
  val futureReport = analyser.getClassDependencies(file)

  val report = futureReport.onComplete(x =>
    println("Dipendenze trovate:")
    x.result().printInformation()
  )

  Thread.sleep(2000)

  val packageDir = new File("src/main/java/test")
  println("Analizzando: " + packageDir.getAbsolutePath)
  println("TEST 2:")
  val packageReport = analyser.getPackageDependencies(packageDir)
  packageReport.onComplete(x =>
    x.result().printInformation())
  var i: Int = 0

  Thread.sleep(2000)

  val projectDir = new File("src")
  println("Analizzando: " + projectDir.getAbsolutePath)
  println("TEST 3:")
  val projectReport = analyser.getProjectDependencies(projectDir)
  projectReport.onComplete(
    x =>
      x.result().printInformation()
      v.close()
  )

