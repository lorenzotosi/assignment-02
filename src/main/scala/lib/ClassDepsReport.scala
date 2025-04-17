package lib


class ClassDepsReport extends Report:
  private var deps: Set[String] = Set()
  private var name: String = ""

  def this(className: String) =  
    this()
    name = className
    
  override def depsList: Set[String] = deps
  override def addDep(dependency: String): Unit = deps = deps + dependency
  
  def className: String = name
