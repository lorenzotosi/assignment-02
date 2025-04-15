import io.vertx.core.*

object Try:
  private class TryVertx extends AbstractVerticle:
    override def start(): Unit =
      val res: Future[Int] = this.getVertx.executeBlocking(() => {
        println("start blocking")
        Thread.sleep(1000)
        100
      })
  
      res.onComplete(c => {
        println(c.result())
        this.getVertx.close()
      })
  
  object TryVertxFactory:
    def apply(): AbstractVerticle = new TryVertx()