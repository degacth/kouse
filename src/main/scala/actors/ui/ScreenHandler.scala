package actors.ui

import models.ScreenPart

import java.awt.image.BufferedImage
import java.awt.{GraphicsEnvironment, Robot}

class ScreenHandler:
  private val zonesKeys = List(
    "qwertyuiop",
    "asdfghjkl;",
    "zxcvbnm,./",
  ).map(_.split("").toList)

  private val remappedKeys = Map(
    ";" -> ":",
    "," -> "<",
    "." -> ">",
    "/" -> "?",
  )

  private val env = GraphicsEnvironment.getLocalGraphicsEnvironment
  private val device = env.getDefaultScreenDevice
  private val bounds = device.getDefaultConfiguration.getBounds
  private val robot = Robot(device)

  private val zones = List(
    zonesKeys, zonesKeys.map(_.map(char => remappedKeys.getOrElse(char, char.toUpperCase)))
  )

  val fullScreenParts: IndexedSeq[ScreenPart] = zones.indices.flatMap: part =>
    import bounds.{height, width}
    val keysPartH = height / zones.size
    val screenPartW = width / zones.head.head.size
    val screenPartH = keysPartH / zones.head.size

    zones(part).zipWithIndex.flatMap: (row, i) =>
      val baseTopPosition = keysPartH * part
      row.zipWithIndex.map: (key, j) =>
        ScreenPart(
          text = key,
          top = baseTopPosition + screenPartH * i,
          left = screenPartW * j,
          w = screenPartW,
          h = screenPartH,
        )

  def screenShot: BufferedImage = robot.createScreenCapture(bounds)
