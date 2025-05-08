package lib

import java.io.File

class ClassDepsReport(val file: File, val map: Map[String, List[String]]) extends BasicReport:

  override def printInformation(pref: String = ""): Unit = {
    println(pref + "  Nome file analizzato: " + file.getName)
    map.foreach((z, y) =>
      println(pref + "   " + z + ":")
      if y.nonEmpty then y.foreach(x => println(pref + "    " + x)) else println(pref + "    -")
    )
  }

  def getStrings: String =
    file.getName + "\n" +
      map.map { case (key, values) =>
      s"$key -> ${if values.nonEmpty then values.mkString(", ") else "-"}"
    }.mkString("\n")
    
  def getFile: File = file
  def getMap: Map[String, List[String]] = map
    

