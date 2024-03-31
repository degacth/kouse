package managers

import java.awt.event.InputEvent
import java.awt.image.BufferedImage
import java.awt.{GraphicsEnvironment, Rectangle, Robot}

object ScreenManager:
  private lazy val env = GraphicsEnvironment.getLocalGraphicsEnvironment
  private lazy val device = env.getDefaultScreenDevice
  private lazy val bounds = device.getDefaultConfiguration.getBounds
  private lazy val robot = Robot(device)

  val screenShot: Rectangle => BufferedImage = robot.createScreenCapture
  def fullScreenShot: BufferedImage = robot.createScreenCapture(fullScreenBounds)
  def fullScreenBounds: Rectangle = Rectangle(0, 0, bounds.width, bounds.height)
  def click(x: Int, y: Int): Unit =
    val keyMask = InputEvent.BUTTON1_DOWN_MASK
    robot.mouseMove(x, y)
    robot.delay(300)
    robot.mousePress(keyMask)
    robot.mouseRelease(keyMask)
