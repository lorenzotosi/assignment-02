package lib

import lib.DependencyAnalyserLib.Report
import java.io.File

class PackageDepsReport(val packageName: File, val classes: List[ClassDepsReport]) extends Report:

  override def depsList: List[String] = classes.flatMap(classDeps =>
    classDeps.map.flatMap { case (key, values) =>
      values.map(value => s"${classDeps.file.getName}: $key: $value")
    })

  override def printInformation(): Unit =
    println(" Package name: ".concat(packageName.getName))
    classes.foreach(c => c.printInformation())