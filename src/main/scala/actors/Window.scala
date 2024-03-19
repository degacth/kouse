package actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object Window:
  enum Command:
    case ToggleActivate

  def apply(): Behavior[Command] = Behaviors.setup: ctx =>
    ???
