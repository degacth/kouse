package actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object Application:
  def apply(): Behavior[Nothing] = Behaviors.setup: ctx =>
    import ctx.*

    val window = spawn(Window(), "window")
    spawn(GlobalKeysListener(window, Window.Command.ToggleActivate), "global-key-listener")

    Behaviors.ignore
