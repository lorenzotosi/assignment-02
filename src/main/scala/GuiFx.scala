import GraphVisualization.stage
import com.brunomnsilva.smartgraph.graph.GraphEdgeList
import com.brunomnsilva.smartgraph.graphview.{SmartCircularSortedPlacementStrategy, SmartGraphPanel}
import javafx.scene.layout.AnchorPane
import lib.ClassDepsReport
import lib.ReactiveDependencyAnalyser.ReactiveDependencyAnalyser
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
    val graph = new GraphEdgeList[String, String]()

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

    val openButton = new Button("Sfoglia...") {
      onAction = _ => {
        val selectedDir = directoryChooser.showDialog(stage)
        if (selectedDir != null) {
          pathField.text = selectedDir.getAbsolutePath
        }
        var count = 0
        val analyser = ReactiveDependencyAnalyser()
        val scheduler = io.reactivex.rxjava3.schedulers.Schedulers.io()
        analyser.getClassPaths(selectedDir)
          .subscribeOn(scheduler)
          .subscribe(p => {
            val obj: ClassDepsReport = p

            val filename = obj.file.getName
            if !graph.vertices().contains(obj.file.getName) then
              graph.insertVertex(obj.file.getName)

            obj.map.foreach((x, y) => {
              if x.equals("Class or Interface") then
                y.foreach(c =>
                  if !graph.vertices().contains(c) then {
                    graph.insertVertex(c)
                    graph.insertEdge(obj.file.getName, c, count.toString)
                    count = count + 1
                  } else {
                    graph.insertEdge(obj.file.getName, c, count.toString)
                    count = count + 1
                  }
                )
            })

            graphView.setAutomaticLayout(true)

            // Aggiorna il layout del grafo
            graphView.update()
          })
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
    graphView.init()
  }
}