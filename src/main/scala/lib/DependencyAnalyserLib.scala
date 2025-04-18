package lib

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.`type`.ClassOrInterfaceType
import io.vertx.core.{AbstractVerticle, CompositeFuture, Future}

import java.io.File
import scala.jdk.CollectionConverters.*


object DependencyAnalyserLib:

  trait Report:
    def depsList: Set[String]
    def printInformation: Unit

  trait Analyzer extends AbstractVerticle:
    def getClassDependencies(classSrcFile: File): Future[ClassDepsReport]
    def getPackageDependencies(packageSrcFolder: File): Future[PackageDepsReport]
    def getProjectDependencies(projectSrcFolder: File): Future[ProjectDepsReport]

  class DependencyAnalyser extends Analyzer:
    override def getClassDependencies(classSrcFile: File): Future[ClassDepsReport] =
      this.getVertx.executeBlocking(() =>
        if !classSrcFile.isFile && !classSrcFile.getName.endsWith(".java") then
          throw new IllegalArgumentException("Il file non è un sorgente .java.")
        else
          val depList = StaticJavaParser.parse(classSrcFile)
            .findAll(classOf[ClassOrInterfaceType])
            .toArray
            .map(_.toString)
            .toSet
          ClassDepsReport(classSrcFile, depList))

    override def getPackageDependencies(packageSrcFolder: File): Future[PackageDepsReport] =
      this.getVertx.executeBlocking(() =>
        if !packageSrcFolder.isDirectory then
          throw new IllegalArgumentException("Il percorso specificato non è un package.")
        else
          packageSrcFolder.listFiles(f =>
            f.isFile && f.getName.endsWith(".java"))
      ).compose ( javaFiles =>
        val classFutures = javaFiles.map(getClassDependencies).toList
        Future.join(classFutures.asJava)
          .map(composite =>
            PackageDepsReport(
              packageSrcFolder,
              (0 until composite.size())
                .map(composite.resultAt[ClassDepsReport])
                .toList
            )
          )
      )

    override def getProjectDependencies(projectSrcFolder: File): Future[ProjectDepsReport] =
      this.getVertx.executeBlocking(() =>
        if !projectSrcFolder.isDirectory then
          throw new IllegalArgumentException("Il percorso specificato non è un progetto")
        else
          projectSrcFolder.listFiles(_.isDirectory)
      ).compose(folders =>
        val packages = folders.map(getPackageDependencies).toList
        Future.join(packages.asJava)
          .map(composite =>
            ProjectDepsReport(
              projectSrcFolder,
              (0 until composite.size())
                .map(composite.resultAt[PackageDepsReport])
                .toList)
          )
        )