package lib

import java.util

class PackageDepsReport extends Report:
  override def depsList: util.Set[String] = ???

  override def addDep(dependency: String): Unit = ???