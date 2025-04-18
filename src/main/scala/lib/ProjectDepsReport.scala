package lib

import lib.DependencyAnalyserLib.Report

class ProjectDepsReport extends Report:
  private var deps: Set[String] = Set()
  private var name: String = ""
  private var packages: List[PackageDepsReport] = List()

  def this(name: String, packageDepsList: List[PackageDepsReport]) =
    this()
    this.name = name
    packages = packageDepsList
    deps = packages.flatMap(x => x.depsList).toSet

  override def depsList: Set[String] = deps

  def projectName: String = name

  def printReport(): Unit =
    println("Project: " + projectName)
    packages.foreach(rep => rep.printReport())
    println("_End " + projectName + " Report_")


