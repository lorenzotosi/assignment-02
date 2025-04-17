package lib

import lib.DependencyAnalyserLib.Report

import java.io.File

class ProjectDepsReport(val projectName: File, val packages: List[PackageDepsReport]) extends Report:

  override def depsList: Set[String] = packages.flatMap(x => x.depsList).toSet

  def printClasses(): Unit = {
    println("Project name: ".concat(projectName.getName))
    packages.foreach(c => {
      println("Package name: ".concat(c.packageName.getName))
      c.classes.foreach(c => {
        println("Class name: ".concat(c.className))
        c.depsList.foreach(println(_))
      })
    })
  }

