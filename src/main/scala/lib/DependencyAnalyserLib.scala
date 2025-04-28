package lib

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.`type`.ClassOrInterfaceType
import com.github.javaparser.ast.body.{FieldDeclaration, VariableDeclarator}
import com.github.javaparser.ast.expr.SimpleName
import io.vertx.core.{AbstractVerticle, CompositeFuture, Future}

import java.io.File
import scala.jdk.CollectionConverters.*


object DependencyAnalyserLib:

  trait Report:
    def depsList: Set[String]

    def printInformation(): Unit

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
//          val depList = StaticJavaParser.parse(classSrcFile)
//                      .findAll(classOf[ClassOrInterfaceType])
//                      .toArray
//                      .map(_.toString)
//                      .toSet

          val cu = StaticJavaParser.parse(classSrcFile)

          val x = MyVoidVisitorAdapter()

          x.visit(cu, null)

          val map: Map[String, Set[String]] = x.getMap
            //Map("Class or Interface" -> depList,
            //"Variables" -> varSet,
//            "Packages" -> packageSet,
//          "Fields" -> fieldSet)

          ClassDepsReport(classSrcFile, Set(), map), false)

    override def getPackageDependencies(packageSrcFolder: File): Future[PackageDepsReport] =
      this.getVertx.executeBlocking(() =>
          if !packageSrcFolder.isDirectory then
            throw new IllegalArgumentException("Il percorso specificato non è un package.")
          else
            packageSrcFolder.listFiles(f =>
              f.isFile && f.getName.endsWith(".java")), false)
        .compose(javaFiles =>
          Future.join(javaFiles.map(getClassDependencies).toList.asJava)
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
            projectSrcFolder.listFiles(_.isDirectory), false)
        .compose(folders =>
          Future.join(folders.map(getPackageDependencies).toList.asJava)
            .map(composite =>
              ProjectDepsReport(
                projectSrcFolder,
                (0 until composite.size())
                  .map(composite.resultAt[PackageDepsReport])
                  .toList)
            )
        )