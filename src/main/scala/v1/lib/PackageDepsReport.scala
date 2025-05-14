package v1.lib

import java.io.File
import v1.lib.DependencyAnalyserLib.*

class PackageDepsReport(
                         val packageName: File,
                         val subPackages: List[PackageDepsReport],
                         val classes: List[ClassDepsReport]
                       ) extends Report:

  override def getDependencies: List[String] =
    classes.flatMap(classReport =>
      classReport.dependencyMap.flatMap { case (key, values) =>
        values.map(value => s"${classReport.file.getName}: $key: $value")
      }
    ) ++ subPackages.flatMap(_.getDependencies)

  override def printInformation(pref: String = ""): Unit =
    println(pref + s" Package name: ${packageName.getName}")
    classes.foreach(_.printInformation(pref))
    subPackages.foreach(sub => {
      println(pref + "  Subpackage: " + sub.packageName.getName)
      sub.printInformation(pref + "    ")
    })