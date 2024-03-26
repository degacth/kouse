package managers

import models.MarkedZone

import java.awt.image.BufferedImage
import java.awt.{Color, Font, Graphics}

object CanvasManager:
  def drawBackground(canvas: Graphics, img: BufferedImage): Unit =
    canvas.drawImage(img, 0, 0, null)
    
  def drawGrid(canvas: Graphics, zones: Seq[MarkedZone]): Unit =
    val font = Font(Font.MONOSPACED, Font.PLAIN, 16)
    canvas.setFont(font)
    canvas.setColor(Color.RED)

    val ascent = canvas.getFontMetrics.getAscent / 2

    zones.foreach: zone =>
      import zone.bounds.*
      canvas.drawRect(x, y, width, height)
      canvas.drawString(zone.text, x + width / 2 - ascent, y + height / 2 + ascent)
