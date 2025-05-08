package lib

import com.github.javaparser.StaticJavaParser
import io.reactivex.rxjava3.core.Observable

import java.io.File

object ReactiveDependencyAnalyser:

  class ReactiveDependencyAnalyser:

    private def getClassDependencies(classSrcFile: File): ClassDepsReport =
      if !classSrcFile.isFile || !classSrcFile.getName.endsWith(".java") then
        throw new IllegalArgumentException("Il file non è un sorgente .java.")
      else
        // println(s"Analizzando la classe: ${classSrcFile.getAbsolutePath}")
        // println(Thread.currentThread().getName)
        val visitor = new MyVoidVisitorAdapter()
        StaticJavaParser.parse(classSrcFile).accept(visitor, null)
        ClassDepsReport(classSrcFile, visitor.getMap)


    def getClassPaths(path: File): Observable[ClassDepsReport] =
      if !path.isDirectory then
        Observable.error(IllegalArgumentException("Il percorso specificato non è una cartella."))
      else
        Observable.create(emitter => {
          def searchFiles(dir: File): Unit =
            val files = dir.listFiles
            if files != null then
              files.foreach(file =>
                if file.isDirectory then
                  searchFiles(file)
                else if file.getName.endsWith(".java") then
                  val x: ClassDepsReport = getClassDependencies(file)
                  emitter.onNext(x)
              )
          try
            searchFiles(path)
            emitter.onComplete()
          catch
            case ex: Exception => emitter.onError(ex)
          })