package lib

import java.util
import java.util.{HashSet, Set}

class ClassDepsReport extends Report:
  private val deps: util.Set[String] = new util.HashSet[String]

  override def depsList: util.Set[String] = deps

  override def addDep(dependency: String): Unit = deps.add(dependency)
