package actors

import actors.ui.Window
import akka.NotUsed
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object Application:
  def apply(): Behavior[NotUsed] = Behaviors.setup: ctx =>
    import ctx.*

    val window = spawn(Window(), "window")
    spawn(GlobalKeysListener(window, Window.Command.ToggleActivate), "global-key-listener")

    Behaviors.ignore
