import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, ScrollPane}
import scalafx.scene.layout.VBox
import scalafx.stage.DirectoryChooser

import java.io.File
import JFXApp3.*
import com.brunomnsilva.smartgraph.graph.{Graph, GraphEdgeList}
import com.brunomnsilva.smartgraph.graphview.{SmartCircularSortedPlacementStrategy, SmartGraphPanel, SmartPlacementStrategy}
import scalafx.geometry.Insets
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

object ScalaFXHelloWorld extends JFXApp3 {

  override def start(): Unit = {
    val label = new Label("No folder selected.")
    val button = new Button("Select Folder")

    // Crea il grafo
    val graph: Graph[String, String] = new GraphEdgeList()

    graph.insertVertex("A")

    // Crea la vista del grafo
    val placementStrategy: SmartPlacementStrategy = new SmartCircularSortedPlacementStrategy()
    val graphView = new SmartGraphPanel(graph, placementStrategy)
    graphView.setAutomaticLayout(true)

    button.onAction = _ => {
      val chooser = new DirectoryChooser {
        title = "Choose a folder"
      }

      val selectedDir: File = chooser.showDialog(stage.delegate)
      if (selectedDir != null) {
        label.text = s"Selected folder: ${selectedDir.getAbsolutePath}"
      } else {
        label.text = "No folder selected."
      }
    }

//    stage = new PrimaryStage {
//      title = "ScalaFX Folder Picker with Graph"
//      width = 800
//      height = 600
//      scene = new Scene {
//        content = new VBox(10) {
//          padding = scalafx.geometry.Insets(20)
//          children = Seq(button, label)
//          }
//        }
//      }
//    }
    stage = new JFXApp3.PrimaryStage {
      title = "ScalaFX Hello World"
      scene = new Scene {
        fill = Color.rgb(38, 38, 38)
        content = new VBox {
          padding = Insets(50, 80, 50, 80)
          children = Seq(
            new Text {
              text = "Scala"
              style = "-fx-font: normal bold 100pt sans-serif"
            },
            new Text {
              text = "FX"
              style = "-fx-font: italic bold 100pt sans-serif"
            }
          )
        }
      }
    }

    // Inizializza e mostra il grafo
    //graphView.init()
  }
}