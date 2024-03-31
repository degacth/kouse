package actors.ui

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import managers.{CanvasManager, CharsManager, ScreenManager}
import models.MarkedZone

import java.awt.event.{KeyAdapter, KeyEvent}
import java.awt.image.BufferedImage
import java.awt.{Graphics, Rectangle}
import javax.swing.{JFrame, JPanel, SwingUtilities, WindowConstants}

object Frame:
  enum Command:
    case Show
    case Hide
    case SelectZone(name: String)

  import Command.*
  private type Bhv = Behavior[Command]

  def apply(window: ActorRef[Window.Command]): Bhv = Behaviors.setup: ctx =>
    var zones = Option.empty[Seq[MarkedZone]]
    var screenBg = Option.empty[BufferedImage]
    val frame = JFrame()
    val panel = new JPanel:
      override def paintComponent(canvas: Graphics): Unit = zones.foreach: zs =>
        val bounds = getBounds

        screenBg.foreach(CanvasManager.drawBackground(canvas, _))
        CanvasManager.drawGrid(canvas, zs)

        canvas.drawRect(0, 0, bounds.width - 1, bounds.height - 1)

    import ctx.*
    import frame.*

    import SwingUtilities.invokeLater
    val fullScreen = getGraphicsConfiguration.getDevice.setFullScreenWindow(_)

    add(panel)

    setFocusable(true)
    setTitle("Kouse")
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
    setUndecorated(true)
    setAlwaysOnTop(true)

    addKeyListener:
      new KeyAdapter:
        override def keyPressed(e: KeyEvent): Unit =
          CharsManager.keyToZoneName(KeyEvent.getKeyText(e.getKeyCode), e.isShiftDown).foreach: name =>
            self ! SelectZone(name)

    def activated: Bhv =
      log.info("activated")

      screenBg = Option(ScreenManager.fullScreenShot)
      invokeLater: () =>
        fullScreen(frame)
        setVisible(true)
        zones = Option(CharsManager.generateZones(getBounds))

      Behaviors.receiveMessage:
        case Hide => deactivated
        case SelectZone(name) =>
          nextSelect(name)
        case _ => Behaviors.unhandled

    def nextSelect(name: String): Bhv =
      log.info("frame next select")
      val zone = zones.flatMap(_.find(_.text == name))

      invokeLater: () =>
        zone.foreach: z =>
          fullScreen(null)
          setLocation(z.bounds.x, z.bounds.y)
          setSize(z.bounds.width, z.bounds.height)

          val screenLocation = getLocationOnScreen
          val screenBounds = getBounds
          screenBg = Option(ScreenManager.screenShot(Rectangle(screenLocation.x, screenLocation.y, screenBounds.width, screenBounds.height)))
          zones = Option(CharsManager.generateZones(getBounds))

      Behaviors.receiveMessage:
        case SelectZone(name) =>
          val location = getLocationOnScreen
          setVisible(false)
          val zone = CharsManager.findZone(name, zones)
          zone.foreach: z =>
            ScreenManager.click(location.x + z.bounds.x + z.bounds.width / 2, location.y + z.bounds.y + z.bounds.height / 2)

          window ! Window.Command.ToggleActivate
          Behaviors.same
        case Hide => deactivated
        case _ => Behaviors.unhandled

    def deactivated: Bhv =
      log.info("frame deactivated")
      zones = Option.empty
      invokeLater: () =>
        fullScreen(null)
        setVisible(false)

      Behaviors.receiveMessage:
        case Show => activated
        case SelectZone(_) => Behaviors.same
        case _ => Behaviors.unhandled

    deactivated
