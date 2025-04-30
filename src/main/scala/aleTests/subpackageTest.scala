package aleTests

import io.vertx.core.Vertx
import lib.DependencyAnalyserLib._

import java.io.File

@main def testSubpackageDependencies(): Unit =
	val analyser = DependencyAnalyser()
	val vertx = Vertx.vertx()
	vertx.deployVerticle(analyser)

	// Path to parent package that contains subpackages
	val parentPackageDir = new File("src/main/scala/aleTests")

	println(s"Analyzing package with subpackages: ${parentPackageDir.getAbsolutePath}")

	val packageReport = analyser.getPackageDependencies(parentPackageDir)

	packageReport.onComplete(ar => {
		if (ar.succeeded()) {
			val result = ar.result()
			println("\n=== PACKAGE DEPENDENCY ANALYSIS RESULTS ===")
			println(s"Package: ${result.packageName.getPath}")
			println(s"Total classes analyzed: ${result.classes.size}")

			// Group classes by their parent directory to verify subpackage handling
			val classesByFolder = result.classes.groupBy(report =>
				report.file.getParentFile.getName
			)

			println("\n=== Classes analyzed by folder: ===")
			classesByFolder.foreach { case (folder, reports) =>
				println(s"Folder '$folder': ${reports.size} class(es)")
				reports.foreach(report => println(s"  - ${report.file.getName}"))
			}

			println("\n=== Dependencies found: ===")
			result.depsList.foreach(println)
		} else {
			println(s"Analysis failed: ${ar.cause().getMessage}")
		}

		vertx.close()
	})

	// Keep the main thread alive
	Thread.sleep(2000)