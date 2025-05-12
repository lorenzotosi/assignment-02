import GraphVisualization.stage
import com.brunomnsilva.smartgraph.graph.GraphEdgeList
import com.brunomnsilva.smartgraph.graphview.{SmartCircularSortedPlacementStrategy, SmartGraphPanel}
import javafx.scene.layout.AnchorPane
import scalafx.Includes.jfxPane2sfx
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.*
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.{Background, Priority, VBox}
import scalafx.scene.{Node, Scene}
import scalafx.stage.DirectoryChooser

object GuiFx extends JFXApp3 {

  override def start(): Unit = {

    // Crea il DirectoryChooser
    val directoryChooser = new DirectoryChooser {
      title = "Seleziona una cartella"
    }

    // Campo di testo per visualizzare il percorso selezionato
    val pathField = new TextField {
      promptText = "Percorso cartella selezionata"
      editable = false
    }

    pathField.setMinWidth(300)

    // Bottone per aprire il dialog
    val openButton = new Button("Sfoglia...") {
      onAction = _ => {
        val selectedDir = directoryChooser.showDialog(stage)
        if (selectedDir != null) {
          pathField.text = selectedDir.getAbsolutePath
        }
      }
    }

    // Pannello superiore con controlli
    val topPanel = new VBox {
      spacing = 10
      padding = Insets(10)
      children = Seq(openButton, pathField)
      style = "-fx-background-color: #FFE4B5;"
      maxWidth = 400
      maxHeight = 200
    }

    val graph = new GraphEdgeList[String, String]()
    // Add vertices and edges from the sample
    List("A", "B", "C", "D", "E", "F", "G").foreach(graph.insertVertex)
    List(
      ("A", "B", "1"), ("A", "C", "2"), ("A", "D", "3"),
      ("A", "E", "4"), ("A", "F", "5"), ("A", "G", "6")
    ).foreach { case (v1, v2, e) => graph.insertEdge(v1, v2, e) }

    List("H", "I", "J", "K", "L", "M", "N").foreach(graph.insertVertex)
    List(
      ("H", "I", "7"), ("H", "J", "8"), ("H", "K", "9"),
      ("H", "L", "10"), ("H", "M", "11"), ("H", "N", "12")
    ).foreach { case (v1, v2, e) => graph.insertEdge(v1, v2, e) }

    graph.insertEdge("A", "H", "0")
    val placementStrategy = new SmartCircularSortedPlacementStrategy()
    val graphView = new SmartGraphPanel[String, String](graph, placementStrategy)
    // Usa un AnchorPane per permettere il ridimensionamento
    val graphContainer = new AnchorPane {
      AnchorPane.setTopAnchor(graphView, 0.0)
      AnchorPane.setBottomAnchor(graphView, 0.0)
      AnchorPane.setLeftAnchor(graphView, 0.0)
      AnchorPane.setRightAnchor(graphView, 0.0)
    }
    graphContainer.setBackground(Background.fill(javafx.scene.paint.Color.RED))
    graphContainer.children = graphView
    graphContainer.setMinHeight(650)

    val graphPanel: Node = new VBox(graphContainer)

    // Layout principale
    val root = new VBox {
      spacing = 10
      children = Seq(topPanel, graphPanel)
      VBox.setVgrow(graphPanel, Priority.Always)
    }

    // Configure main stage
    stage = new JFXApp3.PrimaryStage {
      title = "ScalaFX Graph Visualization"
      scene = new Scene(root, 1024, 768)
    }

    stage.show()
    graphView.setAutomaticLayout(true)
    graphView.init()
  }
}