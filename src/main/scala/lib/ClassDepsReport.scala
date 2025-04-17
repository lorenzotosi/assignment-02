package lib

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.`type`.ClassOrInterfaceType
import lib.DependencyAnalyserLib.Report

import java.io.File

class ClassDepsReport(val file: File) extends Report:
  override def depsList: Set[String] =
    StaticJavaParser.parse(file).findAll(classOf[ClassOrInterfaceType]).toArray.map(_.toString).toSet

  def className: String = file.getName
