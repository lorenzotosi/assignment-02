package aleTests

import lib.DependencyAnalyser
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import scala.concurrent.Await
import java.io.File

@main def runDependencyAnalysis(): Unit =
  val file = File("src/main/scala/aleTests/DummyClass.java") // Cambia questo path con il tuo file .java
  val analyser = DependencyAnalyser()

  println("Analizzando: " + file.getAbsolutePath)
  println("TEST 1:")
  val futureReport = analyser.getClassDependencies(file)
  val report = Await.result(futureReport, 10.seconds)

  println("Dipendenze trovate:")
  report.depsList.forEach(dep => println("-" + dep))

  println("TEST 2:")
  println("Dipendenze trovate:")
  val futureReport2 = analyser.getClassDependencies(file)
  futureReport2.onComplete {res => res.get.depsList.forEach(d => println("-" + d))} //non stampa perché termina prima il main duh