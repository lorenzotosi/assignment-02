package lib

import com.github.javaparser.{JavaParser, ParserConfiguration}
import io.reactivex.rxjava3.core.Observable

import java.io.File
import scala.jdk.CollectionConverters.*

object ReactiveDependencyAnalyser:

  class ReactiveDependencyAnalyser:

    val jp = new JavaParser()
    jp.getParserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_14)

    private def getClassDependencies(classSrcFile: File): ClassDepsReport =
      if !classSrcFile.isFile || !classSrcFile.getName.endsWith(".java") then
        throw new IllegalArgumentException("Il file non è un sorgente .java.")
      else
        val unit = jp.parse(classSrcFile).getResult orElseThrow(() => IllegalArgumentException("failed to parse file " + classSrcFile))
        val pkgName: String =
          try unit.getPackageDeclaration.get().getNameAsString
          catch case e: NoSuchElementException => throw IllegalArgumentException(s"No package declaration in [$classSrcFile]")
        val qualified: String = s"$pkgName.${classSrcFile.getName stripSuffix ".java"}"
        val quali = unit.getImports.asScala.toSet map(_.getNameAsString)
        ClassDepsReport(classSrcFile, quali)


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