package lib

import lib.DependencyAnalyserLib.Report

import java.io.File

class ClassDepsReport(val file: File, val depList: Set[String]) extends Report:
  override def depsList: Set[String] = depList

  override def printInformation: Unit =
    println("  Class name: ".concat(file.getName))
    depList.foreach(d => println("   " + d))
