import lib.ClassDepsReport
import lib.ReactiveDependencyAnalyser.ReactiveDependencyAnalyser

import java.io.File

@main
def runDependencyAnalyser(): Unit = {
  // val gui = Gui.createGui()

  val file = File("src/main/scala/aleTests/")

  val x = ReactiveDependencyAnalyser()

  val y = x.getClassPaths(file).subscribe()

}
