package lib

import lib.DependencyAnalyserLib.Report
import java.io.File

class PackageDepsReport(
                         val packageName: File,
                         val subPackages: List[PackageDepsReport],
                         val classes: List[ClassDepsReport]
                       ) extends Report:

  override def depsList: List[String] =
    classes.flatMap(classReport =>
      classReport.map.flatMap { case (key, values) =>
        values.map(value => s"${classReport.file.getName}: $key: $value")
      }
    ) ++ subPackages.flatMap(_.depsList)

  override def printInformation(): Unit =
    println(s" Package name: ${packageName.getName}")
    subPackages.foreach(_.printInformation())
    classes.foreach(_.printInformation())