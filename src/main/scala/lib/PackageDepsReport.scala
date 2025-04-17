package lib

import lib.DependencyAnalyserLib.Report

class PackageDepsReport(val packageName: String, val classes: List[ClassDepsReport]) extends Report:

  override def depsList: Set[String] = classes.flatMap(x => x.depsList).toSet

  def printClasses(): Unit = {
    classes.foreach(c => {
      println("Class name: ".concat(c.className))
      c.depsList.foreach(println(_))
    })
  }