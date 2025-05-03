package lib

import lib.Report
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

  override def printInformation(pref: String = ""): Unit =
    println(pref + s" Package name: ${packageName.getName}")
    classes.foreach(_.printInformation(pref))
    subPackages.foreach(sub => {
      println(pref + "  Subpackage: " + sub.packageName.getName)
      sub.printInformation(pref + "    ")
    })