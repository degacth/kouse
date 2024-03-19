import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem

@main def main(): Unit =
  val as = ActorSystem(actors.Application(), "app")
