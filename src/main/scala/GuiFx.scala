import GraphVisualization.stage
import com.brunomnsilva.smartgraph.graph.GraphEdgeList
import com.brunomnsilva.smartgraph.graphview.{SmartCircularSortedPlacementStrategy, SmartGraphPanel}
import lib.ClassDepsReport
import lib.ReactiveDependencyAnalyser.ReactiveDependencyAnalyser
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.*
import scalafx.geometry.*
import scalafx.geometry.Pos.TopRight
import scalafx.scene.*
import scalafx.scene.control.*
import scalafx.scene.layout.*
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

    // Label Counter
    val classCounter = new Label()
    classCounter.setAccessibleText("Classi/Interfacce: 0")

    // print info
    val infoBox = new TextArea()

    pathField.setMinWidth(300)
    val graph = new GraphEdgeList[String, String]()

    val placementStrategy = new SmartCircularSortedPlacementStrategy()
    val graphView = new SmartGraphPanel[String, String](graph, placementStrategy)
    graphView.setMinHeight(650)
    graphView.setMinWidth(950)
    // Usa un AnchorPane per permettere il ridimensionamento
    val graphContainer = new ScrollPane() {
    }
    graphContainer.setBackground(Background.fill(javafx.scene.paint.Color.WHITE))
    graphContainer.setContent(graphView)
    graphContainer.setMinHeight(650)
    graphContainer.setMinWidth(950)
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
            if !graph.vertices().contains(filename) then
              graph.insertVertex(filename)

              obj.map.foreach { (x, y) =>
                y.foreach { c =>
                  try
                    if (!graph.vertices().contains(c)) {
                      println(c)
                      graph.insertVertex(c)
                    }
                    graph.insertEdge(filename, c, count.toString)
                    count += 1
                    graphView.setAutomaticLayout(true)
                    println(count)
                    // Aggiorna il layout del grafo
                    graphView.update()
                  catch {
                    case e: Exception =>
                      println(s"Errore durante l'inserimento del vertice o dell'arco: ${e.getMessage}")

                  }
                }
              }


          })
      }
    }

    // Pannello superiore Info
    val topRightPanel = new VBox() {
      alignmentInParent = TopRight
      children = Seq(classCounter, infoBox)
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