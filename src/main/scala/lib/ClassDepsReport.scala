package lib

import java.io.File

class ClassDepsReport(val file: File, val map: Set[String]) extends BasicReport:

  override def printInformation(pref: String = ""): Unit = {
    println(pref + "  Nome file analizzato: " + file.getName)
    map.foreach(println)
  }

//  def getStrings: String =
//    file.getName + "\n" +
//      map.map { case (key, values) =>
//      s"$key -> ${if values.nonEmpty then values.mkString(", ") else "-"}"
//    }.mkString("\n")
    
  def getFile: File = file
    

