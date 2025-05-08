package lib

object ProjectTree:
  
  // enum per i tipi di nodo
  enum NodeType:
    case Class, Interface, Package, Module
  
  class Node:
    var name: String = ""
    var children: List[Node] = List()
    var parent: Option[Node] = None
    var nodeType: NodeType = NodeType.Class
    
    def this(name: String, nodeType: NodeType) =
      this()
      this.name = name
      this.nodeType = nodeType
      
    def addChild(child: Node): Unit =
      children = children :+ child
      child.parent = Some(this)
      
    def removeChild(child: Node): Unit =
      children = children.filterNot(_ == child)
      child.parent = None
      
    def getChildren: List[Node] = children
    def getParent: Option[Node] = parent
    def getName: String = name
    def getNodeType: NodeType = nodeType
    
  class ProjectTree:
    var root: Node = new Node("Root", NodeType.Module)
    
    def addNode(name: String, nodeType: NodeType, parent: Option[Node]): Node =
      val newNode = new Node(name, nodeType)
      parent match
        case Some(p) => p.addChild(newNode)
        case None => root.addChild(newNode)
      newNode
      
    def removeNode(node: Node): Unit =
      node.getParent match
        case Some(p) => p.removeChild(node)
        case None => root.removeChild(node)
      
    def getRoot: Node = root
    
