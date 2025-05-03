package lib

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import io.reactivex.rxjava3.core.{Observable, Single}
import java.io.File
import scala.jdk.CollectionConverters.*


object ReactiveDepAnalyser:

	class ReactiveDependencyAnalyser:

		def getClassDependencies(classSrcFile: File): Single[ClassDepsReport] =
			Single.create(emitter => {
				if !classSrcFile.isFile || !classSrcFile.getName.endsWith(".java") then
					emitter.onError(new IllegalArgumentException("Il file non è un sorgente .java."))
				else
					try
						val visitor = MyVoidVisitorAdapter()
						val cu: CompilationUnit = StaticJavaParser.parse(classSrcFile)
						visitor.visit(cu, null)
						val map: Map[String, List[String]] = visitor.getMap
						emitter.onSuccess(ClassDepsReport(classSrcFile, map))
					catch
						case ex: Exception =>
							emitter.onError(new RuntimeException(s"Errore durante l'analisi del file ${classSrcFile.getName}: ${ex.getMessage}"))
			})

		def getPackageDependencies(packageSrcFolder: File): Single[PackageDepsReport] =
			Single.create(emitter => {
				if !packageSrcFolder.isDirectory then
					emitter.onError(new IllegalArgumentException("Il percorso specificato non è un package."))
				else
					val files = packageSrcFolder.listFiles(f =>
						f.isDirectory || (f.isFile && f.getName.endsWith(".java"))
					)
					val (directories, javaFiles) = files.partition(_.isDirectory)

					val subPackages = Observable.fromIterable(directories.toSeq.asJava)
						.flatMapSingle(getPackageDependencies)
						.toList

					val classes = Observable.fromIterable(javaFiles.toSeq.asJava)
						.flatMapSingle(getClassDependencies)
						.toList

					subPackages.flatMap { subPackagesList =>
						classes.map { classesList =>
							PackageDepsReport(
								packageSrcFolder,
								subPackagesList.asScala.toList,
								classesList.asScala.toList
							)
						}
					}.subscribe(emitter.onSuccess, emitter.onError)
			})

		def getProjectDependencies(projectSrcFolder: File): Single[ProjectDepsReport] =
			Single.create(emitter => {
				if !projectSrcFolder.isDirectory then
					emitter.onError(new IllegalArgumentException("Il percorso specificato non è un progetto."))
				else
					val folders = projectSrcFolder.listFiles(_.isDirectory)
					Observable.fromIterable(folders.toSeq.asJava)
						.flatMapSingle(getPackageDependencies)
						.toList
						.map(packages => ProjectDepsReport(projectSrcFolder, packages.asScala.toList))
						.subscribe(emitter.onSuccess, emitter.onError)
			})