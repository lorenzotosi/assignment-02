package lib

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.`type`.ClassOrInterfaceType
import io.vertx.core.{AbstractVerticle, CompositeFuture, Future}

import java.io.File
import scala.jdk.CollectionConverters.*


object DependencyAnalyserLib:

  trait Report:
    def depsList: Set[String]

  trait Analyzer extends AbstractVerticle:
    def getClassDependencies(classSrcFile: File): Future[ClassDepsReport]
    def getPackageDependencies(packageSrcFolder: File): Future[PackageDepsReport]
    def getProjectDependencies(projectSrcFolder: File): Future[ProjectDepsReport]

  class DependencyAnalyser extends Analyzer:
    override def getClassDependencies(classSrcFile: File): Future[ClassDepsReport] =
      this.getVertx.executeBlocking(() =>
        val depList = StaticJavaParser.parse(classSrcFile)
          .findAll(classOf[ClassOrInterfaceType])
          .toArray
          .map(_.toString)
          .toSet
        ClassDepsReport(classSrcFile, depList))

    override def getPackageDependencies(packageSrcFolder: File): Future[PackageDepsReport] =
      if (!packageSrcFolder.isDirectory)
        throw new IllegalArgumentException("Il percorso specificato non è una directory valida.")
      var classes: List[ClassDepsReport] = List()
      val classFutures = packageSrcFolder
        .listFiles(_.getName.endsWith(".java"))
        .toList
        .map(getClassDependencies)

      val javaList: java.util.List[Future[ClassDepsReport]] = classFutures.asJava
      Future.join(javaList).map { compositeFuture =>
        val classes = (0 until compositeFuture.size())
          .map(compositeFuture.resultAt[ClassDepsReport])
          .toList

        PackageDepsReport(packageSrcFolder, classes)
      }

    override def getProjectDependencies(projectSrcFolder: File): Future[ProjectDepsReport] =
      if (!projectSrcFolder.isDirectory)
        throw new IllegalArgumentException("Il percorso specificato non è una directory valida.")

      val packageFutures = projectSrcFolder
        .listFiles(_.isDirectory)
        .toList
        .map(getPackageDependencies)

      val javaList: java.util.List[Future[PackageDepsReport]] = packageFutures.asJava
      Future.join(javaList).map { compositeFuture =>
        val packages = (0 until compositeFuture.size())
          .map(compositeFuture.resultAt[PackageDepsReport])
          .toList

        ProjectDepsReport(projectSrcFolder, packages)
      }