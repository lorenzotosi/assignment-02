package lib

import lib.DependencyAnalyserLib.Report

import java.io.File

class ClassDepsReport(val file: File, val depList: Set[String], val map: Map[String, Set[String]]) extends Report:
  override def depsList: Set[String] = depList

  override def printInformation(): Unit =
    map.foreach((z, y) =>
      println(z + ":")
      y.foreach(x => println(x))
    )
