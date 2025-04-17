package aleTests

import scala.concurrent.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import lib.DependencyAnalyserLib.*

import java.io.File

@main def runDependencyAnalysis(): Unit =
  val file = File("src/main/scala/aleTests/DummyClass.java") // Cambia questo path con il tuo file .java
  val analyser = DependencyAnalyser()

  //val v = Vertx.vertx()
  //v.deployVerticle(analyser)

  println("Analizzando: " + file.getAbsolutePath)
  println("TEST 1:")
  //val futureReport = analyser.getClassDependencies(file)

  //val report = futureReport.onComplete(x =>
    //println("File: " + x.result().className)

    //println("Dipendenze trovate:")
    //x.result().depsList.foreach(dep => println("-" + dep))
    //v.close())

  val packageDir = new File("src/main/scala/aleTests/")
  val packageReport = analyser.getPackageDependencies(packageDir)
  //packageReport.onComplete(x => x.result().printClasses())
  var i: Int = 0

//  while (i<1000)
//    i = i+1
//    println(i)
//    Thread.sleep(1)

  val projectDir = new File("src/main/scala/")
  val projectReport = analyser.getProjectDependencies(projectDir)
  val resp = Await.result(projectReport, 10.seconds)
  resp.printClasses()
