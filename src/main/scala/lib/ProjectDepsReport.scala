package lib

import lib.DependencyAnalyserLib.Report

import java.io.File

class ProjectDepsReport(val projectName: File, val packages: List[PackageDepsReport]) extends Report:

  override def depsList: Set[String] = packages.flatMap(x => x.depsList).toSet

  override def printInformation: Unit = 
    println("Project name: ".concat(projectName.getName))
    packages.foreach(p => p.printInformation)
  

