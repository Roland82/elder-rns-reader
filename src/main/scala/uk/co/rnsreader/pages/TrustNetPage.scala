package uk.co.rnsreader.pages

import dispatch.{Future, Http, as, url}
import org.asynchttpclient.Response
import org.http4s.client.Client
import org.joda.time.DateTime
import org.jsoup.Jsoup.parse
import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scalaz.{-\/, Maybe, \/, \/-}
import scalaz.Maybe.{Just, empty}
import scala.concurrent.ExecutionContext.Implicits.global
import fs2.Task
import org.http4s
import org.http4s.client
// import fs2.Task

import fs2.Strategy
// import fs2.Strategy

import fs2.interop.cats._
// import fs2.interop.cats._

import cats._
// import cats._

import cats.implicits._
// import cats.implicits._

import org.http4s.Uri

object TrustNetPage {
  def getRnsList(baseUrl: String, d: DateTime)(implicit client: Client): Task[List[Rns]] = {

    def mapDocumentToRnsList(d: Document): List[Rns] = {
      d.select("#announcementList tr")
        .asScala
        .toList
        .filter(_.select("a.annmt").size > 0)
        .map(e => Rns.fromJsoupElement(e, baseUrl))
    }

    val date = d.toString("yyyMMdd")
    client.expect[String](s"$baseUrl/Investments/LatestAnnouncements.aspx?date=$date&pno=1&limit=-1")
      .map(parse)
      .map(mapDocumentToRnsList)
  }
}

case class Rns(
                companyName: String,
                ticker: Maybe[String],
                announcementTitle: String,
                link: String,
                source: String
              ) {

  def getRnsContent()(implicit client: Client, strategy: Strategy): Task[(Rns, String)] = {
    client.expect[String](link)
      .map(parse)
        .map(e => (this, e.select("#content").first().text()))
  }
}


object Rns {

  def fromJsoupElement(e: Element, baseUrl: String) = {
    val companyColumnText = e.select("td").get(3).text()
    val announcementText = e.select("td").get(4).text()
    val link = e.select("td").get(4).select("a").first().attr("href")
    val source = e.select("td.source").text()

    val (companyName, ticker) = parseCompanyDetails(companyColumnText)

    Rns(
      companyName,
      ticker,
      announcementText,
      s"$baseUrl$link",
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




