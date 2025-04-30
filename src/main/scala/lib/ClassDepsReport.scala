package lib

import lib.DependencyAnalyserLib.{BasicReport, Report}

import java.io.File

class ClassDepsReport(val file: File, val map: Map[String, List[String]]) extends BasicReport:
  
  override def printInformation(pref: String = ""): Unit = {
    println(pref + "  Nome file analizzato: " + file.getName)
    map.foreach((z, y) =>
      println(pref + "   " + z + ":")
      if y.nonEmpty then y.foreach(x => println(pref + "    " + x)) else println(pref + "    -")
    )
  }
