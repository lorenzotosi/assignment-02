import lib.ReactiveDependencyAnalyser.ReactiveDependencyAnalyser

import scala.swing.*
import scala.swing.event.*
import java.io.File

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
    val graphPanel = new ScrollPane(new Label("Dependency Graph will appear here...")) {
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
        // startButton should be disabled until end of analysis
        statusBox.text = ""
        val x = ReactiveDependencyAnalyser()

        val scheduler = io.reactivex.rxjava3.schedulers.Schedulers.io()

        val y = x.getClassPaths(folderChooser.selectedFile).subscribeOn(scheduler)
          .subscribe(p => statusBox.append(p.getStrings + "\n \n"))
        graphPanel.contents = statusBox
    }
  }
