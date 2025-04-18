package lib

import lib.DependencyAnalyserLib.Report

import java.io.File


class PackageDepsReport(val packageName: String, private val classDepsList: List[ClassDepsReport]) extends Report:
  private val deps: Set[String] = classDepsList.flatMap(x => x.depsList).toSet

  override def depsList: Set[String] = deps

  def printReport(): Unit =
    println("Package: " + packageName)
    classDepsList.foreach(rep => rep.printReport())
    println("__End " + packageName + " Report__")
