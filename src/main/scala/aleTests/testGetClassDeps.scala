package aleTests

import io.vertx.core.Vertx
import lib.DependencyAnalyserLib.*

import java.io.File

@main def runDependencyAnalysis(): Unit =
  val file = File("src/main/scala/aleTests/DummyClass.java") // Cambia questo path con il tuo file .java
  val analyser = DependencyAnalyser()

  val v = Vertx.vertx()
  v.deployVerticle(analyser)

  //println("Analizzando: " + file.getAbsolutePath)
  //println("TEST 1:")
  //val futureReport = analyser.getClassDependencies(file)

//  val report = futureReport.onComplete(x =>
//    println("Dipendenze trovate:")
//    x.result().printInformation()
//  )

//  val packageDir = new File("src/main/scala/aleTests")
//  println("Analizzando: " + packageDir.getAbsolutePath)
//  println("TEST 2:")
//  val packageReport = analyser.getPackageDependencies(packageDir)
//  packageReport.onComplete(x =>
//    x.result().printInformation())
  //var i: Int = 0

  val projectDir = new File("src/main/scala")
  println("Analizzando: " + projectDir.getAbsolutePath)
  println("TEST 3:")
  val projectReport = analyser.getProjectDependencies(projectDir)
  projectReport.onComplete(
    x =>
      x.result().printInformation()
      v.close()
  )
//
//  while (i<1000)
//    i = i+1
//    println(s"Eseguito dal thread: ${Thread.currentThread().getName}" + i)
//    Thread.sleep(1)
