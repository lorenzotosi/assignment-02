package lib

import com.github.javaparser.ParserConfiguration.LanguageLevel.*
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.{ClassLoaderTypeSolver, CombinedTypeSolver, JavaParserTypeSolver, ReflectionTypeSolver}
import com.github.javaparser.{JavaParser, ParserConfiguration}
import io.reactivex.rxjava3.core.Observable

import java.io.File

object ReactiveDependencyAnalyser:

  class ReactiveDependencyAnalyser:

    val js = new JavaParser()

    private def getClassDependencies(classSrcFile: File, parserConfig: ParserConfiguration): ClassDepsReport =
      if !classSrcFile.isFile || !classSrcFile.getName.endsWith(".java") then
        throw new IllegalArgumentException("Il file non è un sorgente .java.")
      else
        val visitor = new MyVoidVisitorAdapter()
        js.parse(classSrcFile).getResult.get().accept(visitor, null)
        ClassDepsReport(classSrcFile, visitor.getSet)


    def getClassPaths(path: File): Observable[ClassDepsReport] =
      if !path.isDirectory then
        Observable.error(IllegalArgumentException("Il percorso specificato non è una cartella."))
      else

        val reflectionSolver = new ReflectionTypeSolver()
        val sourceSolver = new JavaParserTypeSolver(path.getAbsolutePath + "/src/main/java/")
        val classLoaderSolver= new ClassLoaderTypeSolver(getClass.getClassLoader)

        val combinedSolver = new CombinedTypeSolver(reflectionSolver, sourceSolver, classLoaderSolver)

        val symbolSolver = new JavaSymbolSolver(combinedSolver)
        val parserConfig: ParserConfiguration = new ParserConfiguration().setSymbolResolver(symbolSolver)

        js.getParserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21)
        js.getParserConfiguration.setSymbolResolver(symbolSolver)

        Observable.create(emitter =>
          def searchFiles(dir: File): Unit =
            val files = dir.listFiles
            if files != null then
              files.foreach(file =>
                if file.isDirectory then
                  searchFiles(file)
                else if file.getName.endsWith(".java") then
                  val x: ClassDepsReport = getClassDependencies(file, parserConfig)
                  emitter.onNext(x)
              )
          try
            searchFiles(path)
            emitter.onComplete()
          catch
            case ex: Exception =>
              println(ex.getMessage)
              emitter.onError(ex)
          )