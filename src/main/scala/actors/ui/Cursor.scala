package actors.ui

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import java.awt.event.InputEvent
import java.awt.{GraphicsEnvironment, MouseInfo, Robot}

object Cursor:
  enum Direction:
    case Left, Down, Up, Right

  enum Command:
    case Move(speed: Int, direction: Direction)
    case ClickLeft
    case ClickMiddle
    case ClickRight

  private type Bhv = Behavior[Command]

  def apply(): Bhv = Behaviors.setup: ctx =>
    import Direction.*
    import Command.*

    lazy val env = GraphicsEnvironment.getLocalGraphicsEnvironment
    lazy val device = env.getDefaultScreenDevice
    lazy val robot = Robot(device)

    Behaviors.receiveMessage:
      case Move(speed, direction) =>
        val location = MouseInfo.getPointerInfo.getLocation
        import location.{x, y}

        direction match
          case Left => robot.mouseMove(x - speed, y)
          case Down => robot.mouseMove(x, y + speed)
          case Up => robot.mouseMove(x, y - speed)
          case Right => robot.mouseMove(x + speed, y)
        Behaviors.same
      case ClickLeft =>
        val key = InputEvent.BUTTON1_DOWN_MASK
        robot.mousePress(key)
        robot.mouseRelease(key)
        Behaviors.same
