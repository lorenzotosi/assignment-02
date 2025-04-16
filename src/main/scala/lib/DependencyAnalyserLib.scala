package lib


object DependencyAnalyserLib
  trait Analyzer:
    def getClassDependencies(classSrcFile: Any): ClassDepsReport
    def getPackageDependencies(packageSrcFolder: Any): PackageDepsReport
    def getProjectDependencies(projectSrcFolder: Any): ProjectDepsReport

