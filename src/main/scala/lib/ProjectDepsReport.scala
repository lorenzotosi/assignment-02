package lib

import lib.DependencyAnalyserLib.Report

import java.io.File

class ProjectDepsReport(val projectName: File, val packages: List[PackageDepsReport]) extends Report:

  override def depsList: List[String] = packages.flatMap(x => x.depsList)

  override def printInformation(pref: String = ""): Unit =
    println(pref + "Project name: ".concat(projectName.getName))
    packages.foreach(p => p.printInformation(pref))


