package managers

import models.MarkedZone
import java.awt.Rectangle

object CharsManager:
  private val screenChars = List(
    "qwertyuiop",
    "asdfghjkl;",
    "zxcvbnm,./",
  ).map(_.split("").toList)

  private val remappedChars = Map(
    ";" -> ":",
    "," -> "<",
    "." -> ">",
    "/" -> "?",
  )

  private val zones = List(
    screenChars, screenChars.map(_.map(char => remappedChars.getOrElse(char, char.toUpperCase)))
  )

  def generateZones(bounds: Rectangle): IndexedSeq[MarkedZone] = zones.indices.flatMap: part =>
    import bounds.{height, width}
    val keysPartH = height / zones.size
    val screenPartW = width / zones.head.head.size
    val screenPartH = keysPartH / zones.head.size

    zones(part).zipWithIndex.flatMap: (row, i) =>
      val baseTopPosition = keysPartH * part
      row.zipWithIndex.map: (key, j) =>
        MarkedZone(key, Rectangle(
          screenPartW * j,
          baseTopPosition + screenPartH * i,
          screenPartW,
          screenPartH,
        ))

  def keyToZoneName(name: String, modified: Boolean): Option[String] =
    val keyName = name.toLowerCase match
      case char if char.length == 1 && char >= "a" && char <= "z" =>
        if modified then char.toUpperCase() else char
      case "semicolon" if modified => ":"
      case "semicolon" => ";"
      case "comma" if modified => "<"
      case "comma" => ","
      case "period" if modified => ">"
      case "period" => "."
      case "slash" if modified => "?"
      case "slash" => "/"
      case unknown => ""

    if keyName.isEmpty then None else Some(keyName)
    
  def findZone(name: String, zones: Option[Seq[MarkedZone]]): Option[MarkedZone] = zones.flatMap(_.find(_.text == name))
