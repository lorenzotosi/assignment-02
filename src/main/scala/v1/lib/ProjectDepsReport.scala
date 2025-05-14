package v1.lib

import java.io.File
import v1.lib.DependencyAnalyserLib.*

class ProjectDepsReport(val projectName: File, val packages: List[PackageDepsReport]) extends Report:

  override def getDependencies: List[String] = packages.flatMap(x => x.getDependencies)

  override def printInformation(pref: String = ""): Unit =
    println(pref + "Project name: ".concat(projectName.getName))
    packages.foreach(p => p.printInformation(pref))


