package lib

object ProjectTree:
  enum NodeType:
    case Class, Interface, Package, Module
  
  class Node(val name: String = "",
             val nodeType: NodeType = NodeType.Class,
             val children: List[Node] = List(),
             val parent: Option[Node] = None):
      
    def addChild(child: Node): Node = Node(name, nodeType, children :+ child, Some(this))

    def removeChild(child: Node): Node = Node(name, nodeType, children.filterNot(_ == child), None)
      
    def getChildren: List[Node] = children
    def getParent: Option[Node] = parent
    def getName: String = name
    def getNodeType: NodeType = nodeType
    
  class ProjectTree:
    val root: Node = new Node("Root", NodeType.Module)
    
    def addNode(name: String, nodeType: NodeType, parent: Option[Node]): Node =
      val newNode = new Node(name, nodeType)
      parent match
        case Some(p) => p.addChild(newNode)
        case None => root.addChild(newNode)
      newNode
    
