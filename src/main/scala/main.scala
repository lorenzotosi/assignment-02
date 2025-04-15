import io.vertx.core.*
import Try.*

@main
def main(): Unit =
  println("Hello world!")
  val x = TryVertxFactory()
  val v = Vertx.vertx()
  v.deployVerticle(x)

