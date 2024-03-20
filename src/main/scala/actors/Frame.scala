package actors

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.Behaviors

import java.awt.Graphics
import java.awt.event.{KeyAdapter, KeyEvent}
import javax.swing.*

object Frame extends JFrame:
  enum Command:
    case Show
    case Hide

  import Command.*
  private type Bhv = Behavior[Command]

  private val pane = new JPanel:
    override def paintComponent(g: Graphics): Unit = super.paintComponent(g)

  def apply(): Bhv =
    add(pane)

    addKeyListener:
      new KeyAdapter:
        override def keyPressed(e: KeyEvent): Unit = super.keyPressed(e)

    setFocusable(true)
    setTitle("Kouse")
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
    setUndecorated(true)

    def deactivated: Bhv =
      getGraphicsConfiguration.getDevice.setFullScreenWindow(null)
      setVisible(false)
      Behaviors.receiveMessage:
        case Show => activated

    def activated: Bhv =
      getGraphicsConfiguration.getDevice.setFullScreenWindow(this)
      setVisible(true)

      Behaviors.receiveMessage:
        case Hide => deactivated

    Behaviors.receiveMessage:
      case Show => activated
