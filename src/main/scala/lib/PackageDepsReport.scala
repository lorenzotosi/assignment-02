package lib

import lib.DependencyAnalyserLib.Report
import java.io.File

class PackageDepsReport(val packageName: File, val classes: List[ClassDepsReport]) extends Report:

  override def depsList: Set[String] = classes.flatMap(x => x.depsList).toSet

  override def printInformation: Unit =
    println(" Package name: ".concat(packageName.getName))
    classes.foreach(c => c.printInformation)