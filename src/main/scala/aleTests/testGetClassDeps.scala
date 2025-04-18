package aleTests

import lib.DependencyAnalyserLib.DependencyAnalyser
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.Await
import java.io.File

@main def runDependencyAnalysis(): Unit =
  val file = File("src/main/scala/aleTests/DummyClass.java") // Cambia questo path con il tuo file .java
  val analyser = DependencyAnalyser()
  println("TEST CLASS:")
  val classReport = analyser.getClassDependencies(file)

  classReport.onComplete(r =>
    r.get.printReport()
    println("CLASS DEPS = " +  r.get.depsList)
  )

  var i: Int = 0
  while (i < 200)
    i = i + 1
    Thread.sleep(1)

  val packageDir = new File("src/main/scala/aleTests/")
  println("TEST PACKAGE:")
  val packageReport = analyser.getPackageDependencies(packageDir)
  packageReport.onComplete(x =>
    x.get.printReport()
    println("PACKAGE DEPS = " +  x.get.depsList)
  )

  i = 0
  while (i < 200)
    i = i + 1
    Thread.sleep(1)

  val projectDir = new File("src/main/scala/")
  println("TEST PROJECT:")
  val projectReport = analyser.getProjectDependencies(projectDir)

  projectReport.onComplete(r =>
    r.get.printReport()
    println("PROJECT DEPS = " +  r.get.depsList)
  )
  i = 0
  while (i < 200)
    i = i + 1
    Thread.sleep(1)



