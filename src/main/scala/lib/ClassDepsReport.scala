package lib

import lib.DependencyAnalyserLib.Report

import java.io.File

class ClassDepsReport(val file: File, depList: Set[String]) extends Report:
  override def depsList: Set[String] = depList
  
  def className: String = file.getName
