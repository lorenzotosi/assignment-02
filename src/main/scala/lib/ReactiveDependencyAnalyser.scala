package lib

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import io.reactivex.rxjava3.core.{Single, Observable}
import java.io.File
import java.util.concurrent.Callable
import scala.jdk.CollectionConverters._

object ReactiveDependencyAnalyser:
	// ... Resto delle definizioni rimane invariato ...

	trait Analyzer:
		def getClassDependencies(classSrcFile: File): Single[ClassDepsReport]
		def getPackageDependencies(packageSrcFolder: File): Observable[PackageDepsReport]
		def getProjectDependencies(projectSrcFolder: File): Observable[ProjectDepsReport]

	class ReactiveDependencyAnalyser extends Analyzer:
		private val scheduler = io.reactivex.rxjava3.schedulers.Schedulers.io()

		override def getClassDependencies(classSrcFile: File): Single[ClassDepsReport] =
			Single.fromCallable(() => {
				if !classSrcFile.isFile || !classSrcFile.getName.endsWith(".java") then
					throw new IllegalArgumentException("Il file non è un sorgente .java.")

				val x = new MyVoidVisitorAdapter()
				val cu = StaticJavaParser.parse(classSrcFile)
				x.visit(cu, null)
				ClassDepsReport(classSrcFile, x.getMap)
			}).subscribeOn(scheduler)

		override def getPackageDependencies(packageSrcFolder: File): Observable[PackageDepsReport] =
			if !packageSrcFolder.isDirectory then
				Observable.error(IllegalArgumentException("Il percorso specificato non è un package."))
			else
			Observable.defer(() => {
				val allFiles = packageSrcFolder.listFiles.toList
				val (directories, files) = allFiles.partition(_.isDirectory)
		
				val subPackagesObs = Observable.fromIterable(directories.asJava).flatMap(getPackageDependencies)
				val classesObs = Observable.fromIterable(files.asJava).flatMap(f =>
					getClassDependencies(f).toObservable
				)
		
				// Converti i Single in Observable
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
			}).subscribeOn(scheduler)

		override def getProjectDependencies(projectSrcFolder: File): Observable[ProjectDepsReport] =
			if !projectSrcFolder.isDirectory then
				Observable.error(IllegalArgumentException("Il percorso specificato non è un progetto"))
			else
			Observable.defer(() => {
				val packagesObs = Observable.fromIterable(
					projectSrcFolder.listFiles
						.filter(_.isDirectory)
						.toList  // Converti a List Scala
						.asJava  // Converti a Java Iterable
				).flatMap(getPackageDependencies)

				packagesObs
					.toList
					.map { packages =>
						ProjectDepsReport(
							projectSrcFolder,
							packages.asScala.toList
						)
					}
					.toObservable
			}).subscribeOn(scheduler)