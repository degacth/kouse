package actors.ui

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import java.awt.event.{KeyAdapter, KeyEvent}
import javax.swing.{JFrame, WindowConstants}

object Frame:
  enum Command:
    case Show
    case Hide

  import Command.*

  private type Bhv = Behavior[Command]

  private val speeds = Map(
    "A" -> 2,
    "S" -> 3,
    "D" -> 5,
    "F" -> 8,
    "G" -> 13,
    "Q" -> 21,
    "W" -> 34,
    "E" -> 55,
    "R" -> 89,
  )

  def apply(): Bhv = Behaviors.setup: ctx =>
    val frame = JFrame()
    var speed = 10

    import ctx.*
    import frame.*

    setFocusable(true)
    setTitle("Kouse")
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
    setUndecorated(true)
    setAlwaysOnTop(true)
    setLocation(0, 0)
    setSize(1, 1)

    val cursor = spawn(Cursor(), "cursor")

    addKeyListener:
      new KeyAdapter:
        override def keyPressed(e: KeyEvent): Unit =
          import Cursor.Command.*
          import Cursor.Direction.*

          KeyEvent.getKeyText(e.getKeyCode) match
            case "H" => cursor ! Move(speed, Left)
            case "J" => cursor ! Move(speed, Down)
            case "K" => cursor ! Move(speed, Up)
            case "L" => cursor ! Move(speed, Right)
            case m if speeds.keys.exists(_ == m) =>
              speed = speeds(m)
              if e.isShiftDown then speed *= 10
            case "I" =>
              self ! Hide
              cursor ! ClickLeft
            case _ =>

    def activated: Bhv =
      log.info("frame activated")
      setVisible(true)

      Behaviors.receiveMessage:
        case Hide => deactivated
        case _ => Behaviors.unhandled

    def deactivated: Bhv =
      log.info("frame deactivated")
      setVisible(false)

      Behaviors.receiveMessage:
        case Show => activated
        case _ => Behaviors.unhandled

    deactivated
