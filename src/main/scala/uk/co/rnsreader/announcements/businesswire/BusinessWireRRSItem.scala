package uk.co.rnsreader.announcements.businesswire

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

import scala.xml.Node

case class BusinessWireRRSItem(title: String, description: String, link: String, date: DateTime)

object BusinessWireRRSItem {
  def fromXmlNode(node: Node) = {
    val date = (node \ "pubDate").head.text.filter(_ >= ' ').trim

    val parsedDate = DateTime.parse(date, DateTimeFormat.forPattern("E, dd MMM yyyy HH:mm:ss 'UT'"))
    BusinessWireRRSItem(
      (node \ "title").head.text.filter(_ >= ' ').trim,
      (node \ "description").head.text.filter(_ >= ' ').trim,
      (node \ "link").head.text.filter(_ >= ' ').trim,
      parsedDate
    )
  }
}
