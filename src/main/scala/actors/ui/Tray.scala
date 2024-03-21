package actors.ui

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{Behavior, PostStop}

import java.awt.*
import javax.swing.{ImageIcon, SwingUtilities}

object Tray:
  private type Bhv = Behavior[NotUsed]

  private val tray = SystemTray.getSystemTray

  def apply(): Bhv = Behaviors.setup: ctx =>
    import ctx.*

    val trayIcon = new TrayIcon(createImage("/images/bulb.gif", "Kouse"))

    val initTray: Runnable = () =>
      tray.add(trayIcon)

      val popup = new PopupMenu()
      val exitItem = new MenuItem("Exit")
      exitItem.addActionListener: _ =>
        system.terminate()

      popup.add(exitItem)
      trayIcon.setPopupMenu(popup)

    SwingUtilities.invokeLater(initTray)

    Behaviors
      .receiveSignal:
        case (_, PostStop) =>
          tray.remove(trayIcon)
          Behaviors.same

  private def createImage(path: String, description: String): Image =
    Option(getClass.getResource(path)).map(url => ImageIcon(url, description)) match
      case Some(image) => image.getImage
      case _ => throw IllegalStateException("Application has no icon image")
