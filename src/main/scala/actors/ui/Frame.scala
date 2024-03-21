package actors.ui

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import java.awt.{Color, Font, Graphics}
import java.awt.event.{KeyAdapter, KeyEvent}
import javax.swing.*


object Frame extends JFrame:
  enum Command:
    case Show
    case Hide

  import Command.*
  private type Bhv = Behavior[Command]

  private val handler = ScreenHandler()

  private val pane = new JPanel:
    override def paintComponent(g: Graphics): Unit =
      g.drawImage(handler.screenShot, 0, 0, null)

      val font = Font(Font.MONOSPACED, Font.PLAIN, 16)
      g.setFont(font)
      g.setColor(Color.RED)

      val ascent = g.getFontMetrics.getAscent / 2

      handler.fullScreenParts.foreach: part =>
        g.drawRect(part.left, part.top, part.w, part.h)
        g.drawString(part.text, part.left + part.w / 2 - ascent, part.top + part.h / 2 + ascent)

  def apply(): Bhv = Behaviors.setup: ctx =>

    val self = this
    add(pane)

    addKeyListener:
      new KeyAdapter:
        override def keyPressed(e: KeyEvent): Unit =
          ()
    //          getGraphicsConfiguration.getDevice.setFullScreenWindow(null)
    //          self.setSize(300, 200)
    //          self.setLocation(300, 200)

    setFocusable(true)
    setTitle("Kouse")
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
    setUndecorated(true)

    def deactivated: Bhv =
      SwingUtilities.invokeLater: () =>
        getGraphicsConfiguration.getDevice.setFullScreenWindow(null)
        setVisible(false)

      Behaviors.receiveMessage:
        case Show => activated
        case _ => Behaviors.unhandled

    def activated: Bhv =
      SwingUtilities.invokeLater: () =>
        getGraphicsConfiguration.getDevice.setFullScreenWindow(this)
        setVisible(true)

      Behaviors.receiveMessage:
        case Hide => deactivated
        case _ => Behaviors.unhandled

    Behaviors.receiveMessage:
        case Show => activated
        case _ => Behaviors.unhandled
