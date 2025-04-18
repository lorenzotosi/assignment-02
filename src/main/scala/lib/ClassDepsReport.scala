package lib

import lib.DependencyAnalyserLib.Report

import java.io.File


class ClassDepsReport extends Report:
  private var deps: Set[String] = Set()
  private var name: String = ""

  def this(name: String, deplist: Set[String]) =
    this()
    this.name = name
    deps = deplist
    
  override def depsList: Set[String] = deps
  
  def className: String = name

  def printReport(): Unit =
    println("Class: " + className)
    deps.foreach(dep => println("|  - " + dep))
    println("___End " + className + " Report___")
