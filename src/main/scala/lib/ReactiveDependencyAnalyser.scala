package lib

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import io.reactivex.rxjava3.core.{Single, Observable}
import java.io.File
import scala.jdk.CollectionConverters._

object ReactiveDependencyAnalyser:

	trait Analyzer:
		def getClassDependencies(classSrcFile: File): Single[ClassDepsReport]
		def getPackageDependencies(packageSrcFolder: File): Observable[PackageDepsReport]
		def getProjectDependencies(projectSrcFolder: File): Observable[ProjectDepsReport]

	class ReactiveDependencyAnalyser extends Analyzer:
		private val scheduler = io.reactivex.rxjava3.schedulers.Schedulers.io()

		override def getClassDependencies(classSrcFile: File): Single[ClassDepsReport] = {
			println(s"Analizzando la classe: ${classSrcFile.getAbsolutePath}")
			Single.fromCallable { () =>
				if !classSrcFile.isFile || !classSrcFile.getName.endsWith(".java") then
					throw new IllegalArgumentException("Il file non è un sorgente .java.")

				val visitor = new MyVoidVisitorAdapter()
				StaticJavaParser.parse(classSrcFile).accept(visitor, null)
				ClassDepsReport(classSrcFile, visitor.getMap)
			}.subscribeOn(scheduler)
		}

		override def getPackageDependencies(packageSrcFolder: File): Observable[PackageDepsReport] =
			if !packageSrcFolder.isDirectory then
				Observable.error(IllegalArgumentException("Il percorso specificato non è un package."))
			else
				Observable.defer { () =>
					val (directories, files) = packageSrcFolder.listFiles.toList.partition(_.isDirectory)

					val subPackagesObs = Observable.fromIterable(directories.asJava)
						.flatMap(getPackageDependencies)

					val classesObs = Observable.fromIterable(files.asJava)
						.filter(_.getName.endsWith(".java"))
						.flatMapSingle(getClassDependencies)

					Observable.zip(
						subPackagesObs.toList.toObservable,
						classesObs.toList.toObservable,
						(subPackages: java.util.List[PackageDepsReport], classes: java.util.List[ClassDepsReport]) =>
							PackageDepsReport(
								packageSrcFolder,
								subPackages.asScala.toList,
								classes.asScala.toList
							)
					)
				}.subscribeOn(scheduler)

		override def getProjectDependencies(projectSrcFolder: File): Observable[ProjectDepsReport] =
			if !projectSrcFolder.isDirectory then
				Observable.error(IllegalArgumentException("Il percorso specificato non è un progetto"))
			else
				Observable.defer { () =>
					Observable.fromIterable(projectSrcFolder.listFiles.toList.filter(_.isDirectory).asJava)
						.flatMap(getPackageDependencies)
						.toList
						.map(packages => ProjectDepsReport(projectSrcFolder, packages.asScala.toList))
						.toObservable
				}.subscribeOn(scheduler)