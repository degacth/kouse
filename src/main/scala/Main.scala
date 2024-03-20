import akka.actor.typed.ActorSystem

import scala.io.StdIn

@main def main(): Unit =
  val as = ActorSystem(actors.Application(), "app")
  StdIn.readLine("Press [Enter] to exit ...")
  as.terminate()
