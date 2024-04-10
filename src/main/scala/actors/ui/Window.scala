package actors.ui

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import java.awt.SystemTray

object Window:
  enum Command:
    case ToggleActivate

  import Command.*

  private type Bhv = Behavior[Command]

  def apply(): Bhv = Behaviors.setup: ctx =>
    import ctx.*

    val frame = spawn(Frame(), "frame")
    if SystemTray.isSupported then spawn(Tray(), "tray")

    def activated: Bhv =
      frame ! Frame.Command.Show

      Behaviors.receiveMessage:
        case ToggleActivate => deactivated

    def deactivated: Bhv =
      frame ! Frame.Command.Hide

      Behaviors.receiveMessage:
        case ToggleActivate => activated

    Behaviors.receiveMessage:
      case ToggleActivate => activated
