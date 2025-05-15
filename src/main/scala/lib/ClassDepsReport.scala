package lib

import java.io.File

class ClassDepsReport(val file: File, val map: Set[String]) extends BasicReport:

  override def printInformation(pref: String = ""): Unit =
    println(pref + "  Nome file analizzato: " + file.getName)
    map.foreach(println)
    
  def getFile: File = file
    

