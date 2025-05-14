package v1.lib

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import io.vertx.core.{AbstractVerticle, CompositeFuture, Future}

import java.io.File
import scala.jdk.CollectionConverters.*


object DependencyAnalyserLib:

  trait BasicReport:
    def printInformation(pref: String): Unit

  trait Report extends BasicReport:
    def getDependencies: List[String]

  trait Analyzer extends AbstractVerticle:
    def getClassDependencies(classSrcFile: File): Future[ClassDepsReport]

    def getPackageDependencies(packageSrcFolder: File): Future[PackageDepsReport]

    def getProjectDependencies(projectSrcFolder: File): Future[ProjectDepsReport]

  class DependencyAnalyser extends Analyzer:

    override def getClassDependencies(classSrcFile: File): Future[ClassDepsReport] =
      val x = MyVoidVisitorAdapter()
      this.getVertx.executeBlocking(() =>
        if !classSrcFile.isFile && !classSrcFile.getName.endsWith(".java") then
          throw new IllegalArgumentException("Il file non è un sorgente .java.")
        else
          try {
            val cu: CompilationUnit = StaticJavaParser.parse(classSrcFile)
            x.visit(cu, null)
          } catch {
            case ex: Exception =>
              println(s"Errore durante l'analisi del file ${classSrcFile.getName}: ${ex.getMessage}")
          }
          val map: Map[String, List[String]] = x.getMap
          ClassDepsReport(classSrcFile, map), false)

    override def getPackageDependencies(packageSrcFolder: File): Future[PackageDepsReport] =
      this.getVertx.executeBlocking(() =>
          if !packageSrcFolder.isDirectory then
            throw new IllegalArgumentException("Il percorso specificato non è un package.")
          else
            packageSrcFolder.listFiles(f =>
              f.isDirectory || (f.isFile && f.getName.endsWith(".java"))), false)
        .compose(javaFiles =>
          val (directories, files) = javaFiles.partition(_.isDirectory)
          val subPackagesFutures = directories.map(getPackageDependencies)
          val classFutures = files.map(getClassDependencies)

          Future.join(subPackagesFutures.toList.asJava).compose { subPackagesComposite =>
            Future.join(classFutures.toList.asJava).map { classesComposite =>
              val subPackages = (0 until subPackagesComposite.size())
                .map(subPackagesComposite.resultAt[PackageDepsReport])
                .toList
              val classes = (0 until classesComposite.size())
                .map(classesComposite.resultAt[ClassDepsReport])
                .toList
              PackageDepsReport(packageSrcFolder, subPackages, classes)
            }
          }
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