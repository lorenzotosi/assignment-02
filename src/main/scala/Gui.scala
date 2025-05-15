import lib.ClassDepsReport
import lib.ProjectTree.*
import lib.ReactiveDependencyAnalyser.ReactiveDependencyAnalyser

import java.io.File
import javax.swing.tree.{DefaultMutableTreeNode, DefaultTreeModel, TreePath}
import javax.swing.{JScrollPane, JTree}
import scala.jdk.CollectionConverters.*
import scala.swing.*
import scala.swing.event.*


object Gui:

  case class TreeNodeInfo(name: String, nodeType: NodeType):
    override def toString: String = name


  class CustomTreeCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer {
    override def getTreeCellRendererComponent(
                                               tree: JTree,
                                               value: Any,
                                               sel: Boolean,
                                               expanded: Boolean,
                                               leaf: Boolean,
                                               row: Int,
                                               hasFocus: Boolean
                                             ): java.awt.Component = {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
      value match {
        case node: DefaultMutableTreeNode =>
          node.getUserObject match {
            case TreeNodeInfo(_, NodeType.Package) =>
              if (expanded) setIcon(getOpenIcon)
              else setIcon(getClosedIcon)
            case TreeNodeInfo(_, NodeType.Class) =>
              setIcon(getLeafIcon)
            case TreeNodeInfo(_, NodeType.Interface) =>
              setIcon(getLeafIcon)
            case _ =>
          }
        case _ =>
      }
      this
    }
  }

  def createGui(): Frame = new MainFrame {
    title = "Dependency Analyser"
    
    val folderChooser = new FileChooser(new File("."))
    folderChooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly

    val folderLabel = new Label("Source Folder: ")
    val folderField: TextField = new TextField {
      columns = 30
      editable = false
    }
    val selectFolderButton = new Button("Browse")

    val startButton = new Button("Start Analysis")
    startButton.enabled = false
    val warningLabel = new Label("Select project folder first")

    val classCountLabel = new Label("Classes/Interfaces: 0")
    val dependencyCountLabel = new Label("Dependencies: 0")
    val statusBox: TextArea = new TextArea {
      editable = false
      rows = 10
      lineWrap = true
      wordWrap = true
    }
    
    val graphPanel: ScrollPane = new ScrollPane() {
      preferredSize = new Dimension(600, 400)
    }

    val outputInfo: TextArea = new TextArea() {
      editable = false
    }


    val outputContainer: ScrollPane = new ScrollPane(outputInfo) {
      preferredSize = new Dimension(600, 200)
    }

    contents = new BorderPanel {
      layout(new BoxPanel(Orientation.Vertical) {
        contents += new FlowPanel(folderLabel, folderField, selectFolderButton)
        contents += new FlowPanel(warningLabel)
        contents += new FlowPanel(startButton)
        contents += new FlowPanel(outputContainer)
        contents += new FlowPanel(classCountLabel, dependencyCountLabel)
        contents += graphPanel
        border = Swing.EmptyBorder(10, 10, 10, 10)
      }) = BorderPanel.Position.Center
    }

    listenTo(selectFolderButton, startButton)
    reactions += {
      case ButtonClicked(`selectFolderButton`) =>
        val result = folderChooser.showOpenDialog(null)
        if (result == FileChooser.Result.Approve)
          folderField.text = folderChooser.selectedFile.getAbsolutePath
          startButton.enabled = true
          warningLabel.text = ""

      case ButtonClicked(`startButton`) =>
        statusBox.text = ""
        outputInfo.text = ""
        var classCount = 0
        var depsCount = 0
        val analyser = ReactiveDependencyAnalyser()
        val scheduler = io.reactivex.rxjava3.schedulers.Schedulers.io()

        outputInfo.append("Analyzing " + folderField.text + "...\n")
        
        val projectTree = new ProjectTree()
        val rootTreeNode = new DefaultMutableTreeNode("Root")
        val treeModel: DefaultTreeModel = new DefaultTreeModel(rootTreeNode)
        val jTree: JTree = new JTree(treeModel)
        jTree.setCellRenderer(new CustomTreeCellRenderer())
        graphPanel.contents = Component.wrap(new JScrollPane(jTree))
        val subscription = analyser.getClassPaths(folderChooser.selectedFile)
          .subscribeOn(scheduler)
          .subscribe(
            p => Swing.onEDT {
              val obj: ClassDepsReport = p
              val file = p.getFile
              val projectRoot = folderChooser.selectedFile

              val projectRootPath = projectRoot.toPath.toAbsolutePath
              val fileParentPath = file.getParentFile.toPath.toAbsolutePath
              val relativePath = projectRootPath.relativize(fileParentPath)
              val packageParts = relativePath.iterator.asScala.toList.map(_.toString)

              val currentParentNode = packageParts.foldLeft(projectTree.root) { (parentNode, part) =>
                parentNode.getChildren.find(n => n.getName == part && n.getNodeType == NodeType.Package)
                  .getOrElse(projectTree.addNode(part, NodeType.Package, Some(parentNode)))
              }

              // Add class node
              val className = file.getName
              outputInfo.append("  Found " + className + "\n")
              classCount += 1
              classCountLabel.text = "Classes/Interfaces: " + classCount

              val node = projectTree.addNode(className, NodeType.Class, Some(currentParentNode))

              def findOrCreateTreeNode(parent: DefaultMutableTreeNode, name: String): DefaultMutableTreeNode = {
                (0 until parent.getChildCount).collectFirst {
                  case i if parent.getChildAt(i).asInstanceOf[DefaultMutableTreeNode].getUserObject == name =>
                    parent.getChildAt(i).asInstanceOf[DefaultMutableTreeNode]
                }.getOrElse {
                  val newNode = new DefaultMutableTreeNode(name)
                  parent.add(newNode)
                  treeModel.nodesWereInserted(parent, Array(parent.getChildCount - 1))
                  newNode
                }
              }

              val treeParent = packageParts.foldLeft(rootTreeNode) { (treeParent, part) =>
                findOrCreateTreeNode(treeParent, part)
              }
              val classNode = new DefaultMutableTreeNode(TreeNodeInfo(className, NodeType.Class))
              treeParent.add(classNode)
              treeModel.nodesWereInserted(treeParent, Array(treeParent.getChildCount - 1))

              var tmp = depsCount
              obj.map.foreach(v => 
                  val childNode = new DefaultMutableTreeNode(v)
                  classNode.add(childNode)
                  treeModel.nodesWereInserted(classNode, Array(classNode.getChildCount - 1))
                  depsCount += 1
                  dependencyCountLabel.text = "Dependencies: " + depsCount
                )
              outputInfo.append("    With " + (depsCount - tmp) + " Dependencies\n")
              tmp = depsCount

              val pathNodes = treeParent.getPath.map(_.asInstanceOf[Object])
              val path = new TreePath(pathNodes)
              jTree.expandPath(path)
              jTree.scrollPathToVisible(path)
            },
            e => Swing.onEDT(statusBox.append(s"Error: ${e.getMessage}\n")),
            () => Swing.onEDT(statusBox.append("Analysis completed.\n"))
          )
    }
  }