package lib

import lib.DependencyAnalyserLib.Report

import java.io.File


class PackageDepsReport extends Report:
  private var deps: Set[String] = Set()
  private var name: String = ""
  private var classes: List[ClassDepsReport] = List()

  def this(name: String, classDepsList: List[ClassDepsReport]) =
    this()
    this.name = name
    classes = classDepsList
    deps = classes.flatMap(x => x.depsList).toSet

  override def depsList: Set[String] = deps

  def packageName: String = name

  def printReport(): Unit =
    println("Package: " + packageName)
    classes.foreach(rep => rep.printReport())
    println("__End " + packageName + " Report__")
