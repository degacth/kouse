package actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import java.awt.*
import javax.swing.{ImageIcon, SwingUtilities}

object Tray:
  enum Command:
    case TODO

  import Command.*
  private type Bhv = Behavior[Command]

  private val tray = SystemTray.getSystemTray

  def apply(): Bhv = Behaviors.setup: ctx =>
    import ctx.*

    val initTray: Runnable = () =>
      val trayIcon = new TrayIcon(createImage("/images/bulb.gif", "Kouse"))
      tray.add(trayIcon)

      val popup = new PopupMenu()
      val exitItem = new MenuItem("Exit")
      exitItem.addActionListener: _ =>
        tray.remove(trayIcon)
        system.terminate()

      popup.add(exitItem)
      trayIcon.setPopupMenu(popup)

    SwingUtilities.invokeLater(initTray)
    Behaviors.ignore

  private def createImage(path: String, description: String): Image =
    Option(getClass.getResource(path)).map(url => ImageIcon(url, description)) match
      case Some(image) => image.getImage
      case _ => throw IllegalStateException("Application has no icon image")
