@main
def runDependencyAnalyser(): Unit = {
  // val gui = Gui.createGui()
  val gui = DepsGui.createGui()
  gui.pack()
  gui.centerOnScreen()
  gui.open()
}
