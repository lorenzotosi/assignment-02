package lib


class PackageDepsReport extends Report:
  override def depsList: Set[String] = ???

  override def addDep(dependency: String): Unit = ???