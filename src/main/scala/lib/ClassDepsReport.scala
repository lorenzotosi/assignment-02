package lib

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.`type`.ClassOrInterfaceType
import lib.DependencyAnalyserLib.Report

import java.io.File

class ClassDepsReport extends Report:
  private var deplist: Set[String] = Set()
  private var fileName: String = ""

  def this(file: File) = {
    this()
    deplist = StaticJavaParser.parse(file).findAll(classOf[ClassOrInterfaceType]).toArray.map(_.toString).toSet
    fileName = file.getName
  }

  override def depsList: Set[String] = deplist


  def className: String = fileName
