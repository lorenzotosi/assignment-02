import lib.ClassDepsReport
import lib.ReactiveDependencyAnalyser.ReactiveDependencyAnalyser

import java.io.File

@main
def runDependencyAnalyser(): Unit = {
  val file = File("src/main/scala/aleTests/")

  val x = ReactiveDependencyAnalyser()

  val scheduler = io.reactivex.rxjava3.schedulers.Schedulers.io()

  val y = x.getClassPaths(file).subscribeOn(scheduler).subscribe(_.printInformation())
  var i = 0
  while (i < 1000) {
    println(i + " " + Thread.currentThread().getName)
    i = i + 1
    Thread.sleep(100)
  }
}
