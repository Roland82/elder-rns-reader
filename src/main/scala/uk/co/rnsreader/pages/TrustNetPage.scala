package uk.co.rnsreader.pages

import dispatch.Future
import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConverters._
import scalaz.{Kleisli, ReaderT, Maybe, \/}
import scalaz.Maybe.{Just, empty}
import scalaz.Kleisli._

object TrustNetPage {
  def parseRnsLists(e: String \/ Document): Future[String \/ List[Rns]] = {

      Future.successful(
        e.map(_.select("#announcementList tr")
        .asScala
        .toList
        .filter(_.select("a.annmt").size > 0)
        .map(e => Rns.fromJsoupElement(e))))
  }

  def parseRnsList(e: String \/ Document): String \/ List[Rns] = {
    e.map {
      _.select("#announcementList tr")
        .asScala
        .toList
        .filter(_.select("a.annmt").size > 0)
        .map(e => Rns.fromJsoupElement(e))
    }
  }

  def getRnsContent(document: Document): String = {
    document.select("#content").first().text()
  }
}

case class Rns(
                companyName: String,
                ticker: Maybe[String],
                announcementTitle: String,
                link: String,
                source: String
              )

object Rns {

  def fromJsoupElement(e: Element) = {
    val companyColumnText = e.select("td").get(3).text()
    val announcementText = e.select("td").get(4).text()
    val link = e.select("td").get(4).select("a").first().attr("href")
    val source = e.select("td.source").text()

    val (companyName, ticker) = parseCompanyDetails(companyColumnText)

    Rns(
      companyName,
      ticker,
      announcementText,
      link,
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