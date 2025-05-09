import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.stage.DirectoryChooser
import java.io.File
import JFXApp3.*

object ScalaFXHelloWorld extends JFXApp3 {

  override def start(): Unit = {

    val label = new Label("No folder selected.")
    val button = new Button("Select Folder")

    button.onAction = _ => {
      val chooser = new DirectoryChooser {
        title = "Choose a folder"
      }

      val selectedDir: File = chooser.showDialog(stage.delegate)  // ScalaFX 21+ needs .delegate
      if (selectedDir != null) {
        label.text = s"Selected folder: ${selectedDir.getAbsolutePath}"
      } else {
        label.text = "No folder selected."
      }
    }

    stage = new PrimaryStage {
      title = "ScalaFX Folder Picker"
      width = 400
      height = 200
      scene = new Scene {
        root = new VBox(10) {
          padding = scalafx.geometry.Insets(20)
          children = Seq(button, label)
        }
      }
    }
  }
}
