package v2



@main
def main(): Unit = {
  val gui = Gui.createGui()
    gui.pack()
    gui.centerOnScreen()
    gui.open()
}
