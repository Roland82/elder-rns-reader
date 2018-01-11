package uk.co.rnsreader.announcements

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.jsoup.nodes.Element

import scala.xml.Node
import scalaz.Maybe
import scalaz.Maybe.{Just, empty}

sealed trait NewsItem

case class RnsItem (
                    companyName: String,
                    ticker: Maybe[String],
                    title: String,
                    path: String,
                    source: String
                  ) extends NewsItem

object RnsItem {

  def fromJsoupElement(e: Element, baseUrl: String) = {
    val companyColumnText = e.select("td").get(3).text()
    val announcementText = e.select("td").get(4).text()
    val path = e.select("td").get(4).select("a").first().attr("href")
    val source = e.select("td.source").text()

    val (companyName, ticker) = parseCompanyDetails(companyColumnText)

    RnsItem(
      companyName,
      ticker,
      announcementText,
      path,
      source
    )
  }

  def parseCompanyDetails(text: String) : (String, Maybe[String]) = {
    val companyDataRegex = "(.*) \\(([A-Z0-9]+)\\)".r("company", "ticker")
    val textMatch = companyDataRegex.findFirstMatchIn(text)
    textMatch
      .map(e => (e.group("company"), Just(e.group("ticker"))))
      .getOrElse(Tuple2(text, empty[String]))
  }
}


case class BusinessWireRRSItem(title: String, description: String, link: String, date: DateTime) extends NewsItem

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