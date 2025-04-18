package lib

import lib.DependencyAnalyserLib.Report

class ProjectDepsReport(val projectName: String, private val packageDepsList: List[PackageDepsReport]) extends Report:
  private val deps: Set[String] = packageDepsList.flatMap(x => x.depsList).toSet

  override def depsList: Set[String] = deps

  def printReport(): Unit =
    println("Project: " + projectName)
    packageDepsList.foreach(rep => rep.printReport())
    println("_End " + projectName + " Report_")


