package lib

import lib.DependencyAnalyserLib.{BasicReport, Report}

import java.io.File

class ClassDepsReport(val file: File, val map: Map[String, List[String]]) extends BasicReport:
  
  override def printInformation(): Unit = {
    println("  Nome file analizzato: " + file.getName)
    map.foreach((z, y) =>
      println("   " + z + ":")
      if y.nonEmpty then y.foreach(x => println("    " + x)) else println("    -")
    )
  }
