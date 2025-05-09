import com.brunomnsilva.smartgraph.graph.GraphEdgeList
import com.brunomnsilva.smartgraph.graphview.{SmartCircularSortedPlacementStrategy, SmartGraphPanel}
import javafx.scene.Scene
import scalafx.application.JFXApp3
import scalafx.Includes.jfxScene2sfx


object GraphVisualization extends JFXApp3 {
  override def start(): Unit = {
    // Create graph instance
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

    // Create visualization components
    val placementStrategy = new SmartCircularSortedPlacementStrategy()
    val graphView = new SmartGraphPanel[String, String](graph, placementStrategy)

    // Configure main stage
    stage = new JFXApp3.PrimaryStage {
      title = "ScalaFX Graph Visualization"
      scene = new Scene(graphView, 1024, 768)
    }

    // Initialize graph visualization after stage is shown
    stage.show()
    graphView.init()
  }
}