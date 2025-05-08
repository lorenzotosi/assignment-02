import lib.ReactiveDependencyAnalyser.ReactiveDependencyAnalyser

import scala.swing.*
import scala.swing.event.*
import java.io.File
import javax.swing.{JTree, JScrollPane}
import javax.swing.tree.{DefaultTreeModel, DefaultMutableTreeNode, TreePath}
import lib.ProjectTree.*
import scala.jdk.CollectionConverters.*


object Gui:

  def createGui(): Frame = new MainFrame {
    title = "Dependency Analyser"

    // Selettore per cartella root del progetto
    val folderChooser = new FileChooser(new File("."))
    folderChooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly

    val folderLabel = new Label("Source Folder: ")
    val folderField = new TextField {
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

    // Placeholder grafico per il grafo delle dipendenze
    var treeModel: DefaultTreeModel = _
    var jTree: JTree = _
    val graphPanel = new ScrollPane() {
      preferredSize = new Dimension(600, 400)
    }

    // Layout
    contents = new BorderPanel {
      layout(new BoxPanel(Orientation.Vertical) {
        contents += new FlowPanel(folderLabel, folderField, selectFolderButton)
        contents += new FlowPanel(warningLabel)
        contents += new FlowPanel(startButton)
        contents += new FlowPanel(classCountLabel, dependencyCountLabel)
        contents += graphPanel
        border = Swing.EmptyBorder(10, 10, 10, 10)
      }) = BorderPanel.Position.Center
    }

    // Reazioni GUI
    listenTo(selectFolderButton, startButton)
    reactions += {
      case ButtonClicked(`selectFolderButton`) =>
        val result = folderChooser.showOpenDialog(null)
        if (result == FileChooser.Result.Approve) {
          folderField.text = folderChooser.selectedFile.getAbsolutePath
          startButton.enabled = true
          warningLabel.text = ""
        }

      case ButtonClicked(`startButton`) =>
        statusBox.text = ""
        val analyser = ReactiveDependencyAnalyser()
        val scheduler = io.reactivex.rxjava3.schedulers.Schedulers.io()

        // Initialize ProjectTree and JTree
        val projectTree = new ProjectTree()
        val rootTreeNode = new DefaultMutableTreeNode("Root")
        treeModel = new DefaultTreeModel(rootTreeNode)
        jTree = new JTree(treeModel)
        graphPanel.contents = Component.wrap(new JScrollPane(jTree))

        val subscription = analyser.getClassPaths(folderChooser.selectedFile)
          .subscribeOn(scheduler)
          .subscribe(
            p => Swing.onEDT {
              val file = p.getFile
              val projectRoot = folderChooser.selectedFile

              // Calculate package structure from file path
              val projectRootPath = projectRoot.toPath.toAbsolutePath
              val fileParentPath = file.getParentFile.toPath.toAbsolutePath
              val relativePath = projectRootPath.relativize(fileParentPath)
              val packageParts = relativePath.iterator.asScala.toList.map(_.toString)

              // Update ProjectTree
              var currentParentNode = projectTree.getRoot
              packageParts.foreach { part =>
                val existingChild = currentParentNode.getChildren.find { n =>
                  n.getName == part && n.getNodeType == NodeType.Package
                }
                existingChild match {
                  case Some(child) => currentParentNode = child
                  case None =>
                    val newNode = projectTree.addNode(part, NodeType.Package, Some(currentParentNode))
                    currentParentNode = newNode
                }
              }

              // Add class node
              val className = file.getName.stripSuffix(".java")
              projectTree.addNode(className, NodeType.Class, Some(currentParentNode))

              // Update JTree
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

              var treeParent = rootTreeNode
              packageParts.foreach { part =>
                treeParent = findOrCreateTreeNode(treeParent, part)
              }
              val classNode = new DefaultMutableTreeNode(className)
              treeParent.add(classNode)
              treeModel.nodesWereInserted(treeParent, Array(treeParent.getChildCount - 1))

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