package lib

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.`type`.ClassOrInterfaceType

import java.io.File
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration


object DependencyAnalyserLib:

  trait Report:
    def depsList: Set[String]

  trait Analyzer:
    def getClassDependencies(classSrcFile: File)(implicit ec: ExecutionContext): Future[ClassDepsReport]
    def getPackageDependencies(packageSrcFolder: File)(implicit ec: ExecutionContext): Future[PackageDepsReport]
    def getProjectDependencies(projectSrcFolder: File)(implicit ec: ExecutionContext): Future[ProjectDepsReport]

  class DependencyAnalyser extends Analyzer:
    override def getClassDependencies(classSrcFile: File)(implicit ec: ExecutionContext): Future[ClassDepsReport] =
      Future {
        val depList = StaticJavaParser.parse(classSrcFile).findAll(classOf[ClassOrInterfaceType]).toArray.map(_.toString).toSet
        ClassDepsReport(classSrcFile.getName, depList)
      }

    override def getPackageDependencies(packageSrcFolder: File)(implicit ec: ExecutionContext): Future[PackageDepsReport] =
      Future {
        if (!packageSrcFolder.isDirectory)
        throw new IllegalArgumentException("Il percorso specificato non è una directory valida.")
        val classes = packageSrcFolder
          .listFiles(_.getName.endsWith(".java"))
          .toList
          .map(getClassDependencies)
          .map(Await.result(_, Duration.Inf))

        PackageDepsReport(packageSrcFolder.getName, classes)
      }

    override def getProjectDependencies(projectSrcFolder: File)(implicit ec: ExecutionContext): Future[ProjectDepsReport] =
      Future {
        if (!projectSrcFolder.isDirectory)
          throw new IllegalArgumentException("Il percorso specificato non è una directory valida.")

        val packages = projectSrcFolder
          .listFiles(_.isDirectory)
          .toList
          .map(getPackageDependencies)
          .map(Await.result(_, Duration.Inf))

        ProjectDepsReport(projectSrcFolder.getName, packages)
      }
