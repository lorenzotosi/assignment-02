package lib

import lib.DependencyAnalyserLib.Report

import java.io.File


class ClassDepsReport(val className: String, private val classDepsList: Set[String]) extends Report:
    
  override def depsList: Set[String] = classDepsList

  def printReport(): Unit =
    println("Class: " + className)
    classDepsList.foreach(dep => println("|  - " + dep))
    println("___End " + className + " Report___")
