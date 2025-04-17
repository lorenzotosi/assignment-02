package lib

import scala.concurrent.{ExecutionContext, Future}
import java.io.File


object DependencyAnalyserLib:

  trait Report:
    def depsList: Set[String]

  trait Analyzer:
    def getClassDependencies(classSrcFile: File)(implicit ec: ExecutionContext): Future[ClassDepsReport]
    def getPackageDependencies(packageSrcFolder: File)(implicit ec: ExecutionContext): Future[PackageDepsReport]
    def getProjectDependencies(projectSrcFolder: File)(implicit ec: ExecutionContext): Future[ProjectDepsReport]

  class DependencyAnalyser extends Analyzer:
    override def getClassDependencies(classSrcFile: File)(implicit ec: ExecutionContext): Future[ClassDepsReport] =
      Future{ ClassDepsReport(classSrcFile)}

    override def getPackageDependencies(packageSrcFolder: File)(implicit ec: ExecutionContext): Future[PackageDepsReport] =
      Future {
        if (!packageSrcFolder.isDirectory)
          throw new IllegalArgumentException("Il percorso specificato non è una directory valida.")

        val classes = packageSrcFolder
          .listFiles((_, name) => name.endsWith(".java"))
          .toList
          .map(file => ClassDepsReport(file))

        PackageDepsReport(packageSrcFolder, classes)
      }
    
    override def getProjectDependencies(projectSrcFolder: File)(implicit ec: ExecutionContext): Future[ProjectDepsReport] =
      if (!projectSrcFolder.isDirectory)
        throw new IllegalArgumentException("Il percorso specificato non è una directory valida.")
      val packageFutures = projectSrcFolder
        .listFiles(_.isDirectory)
        .toList
        .map(pack => getPackageDependencies(pack))

      Future.sequence(packageFutures).map { packages =>
        ProjectDepsReport(projectSrcFolder, packages)
      }