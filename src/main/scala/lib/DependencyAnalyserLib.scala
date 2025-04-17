package lib

import io.vertx.core.{AbstractVerticle, Future}

import java.io.File


object DependencyAnalyserLib:

  trait Report:
    def depsList: Set[String]

  trait Analyzer extends AbstractVerticle:
    def getClassDependencies(classSrcFile: File): Future[ClassDepsReport]
    def getPackageDependencies(packageSrcFolder: File): Future[PackageDepsReport]
    def getProjectDependencies(projectSrcFolder: File): Future[ProjectDepsReport]

  class DependencyAnalyser extends Analyzer:
    override def getClassDependencies(classSrcFile: File): Future[ClassDepsReport] =
      this.getVertx.executeBlocking(() => ClassDepsReport(classSrcFile))

    override def getPackageDependencies(packageSrcFolder: File): Future[PackageDepsReport] =
      if (!packageSrcFolder.isDirectory)
        throw new IllegalArgumentException("Il percorso specificato non è una directory valida.")
      var classes: List[ClassDepsReport] = List()
      this.getVertx.executeBlocking(() =>
        packageSrcFolder
          .listFiles((_, name) => name.endsWith(".java")).toList.foreach(
            file => classes = ClassDepsReport(file) :: classes
          )
        PackageDepsReport(packageSrcFolder, classes)
      )

    override def getProjectDependencies(projectSrcFolder: File): Future[ProjectDepsReport] = ???