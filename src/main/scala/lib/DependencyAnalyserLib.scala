package lib

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.`type`.ClassOrInterfaceType

import java.io.File
import java.util
import scala.concurrent.{Future, ExecutionContext}


object DependencyAnalyserLib

  trait Report:
    def depsList: util.Set[String]
    def addDep(dependency: String): Unit

  trait Analyzer:
    def getClassDependencies(classSrcFile: File)(implicit ec: ExecutionContext): Future[ClassDepsReport]
    def getPackageDependencies(packageSrcFolder: Any): PackageDepsReport
    def getProjectDependencies(projectSrcFolder: Any): ProjectDepsReport

  class DependencyAnalyser extends Analyzer:
    override def getClassDependencies(classSrcFile: File)(implicit ec: ExecutionContext): Future[ClassDepsReport] =
      Future (
        parseClass(classSrcFile)
    )

    override def getPackageDependencies(packageSrcFolder: Any): PackageDepsReport = ???

    override def getProjectDependencies(projectSrcFolder: Any): ProjectDepsReport = ???

    private def parseClass(file: File): ClassDepsReport =
      val cu = StaticJavaParser.parse(file)
      val usedTypes = ClassDepsReport(file.getName)

      cu.findAll(classOf[ClassOrInterfaceType]).forEach { tpe =>
        usedTypes.addDep(tpe.getNameAsString)
      }
      usedTypes

