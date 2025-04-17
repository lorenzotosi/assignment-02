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
  println("File: " + report.className)

  println("Dipendenze trovate:")
  report.depsList.foreach(dep => println("-" + dep))

  println("TEST 2:")
  println("Dipendenze trovate:")
  val futureReport2 = analyser.getClassDependencies(file)

  var i: Int = 0
  futureReport2.onComplete {res => res.get.depsList.foreach(d => println("-" + d))} //non stampa perché termina prima il main duh
  println(i)

  while (i<100) //giusto per confermare la asincronità del metodo
    i = i+1
    println(i)
    Thread.sleep(1)