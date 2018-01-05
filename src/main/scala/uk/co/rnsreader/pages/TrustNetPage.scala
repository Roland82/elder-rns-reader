package uk.co.rnsreader.pages

import dispatch.{Future, Http, as, url}
import org.asynchttpclient.Response
import org.joda.time.DateTime
import org.jsoup.Jsoup.parse
import org.jsoup.nodes.Element

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scalaz.{-\/, Maybe, \/, \/-}
import scalaz.Maybe.{Just, empty}
import scala.concurrent.ExecutionContext.Implicits.global

object TrustNetPage {
  def getRnsList(baseUrl: String, d: DateTime): List[Rns] = {
    val date = d.toString("yyyMMdd")
    val startUrl = s"$baseUrl/Investments/LatestAnnouncements.aspx?date=$date&pno=1&limit=-1"
    val svc = url(startUrl)
    val body = Http.default(svc OK as.String).map(parse)

    val document = Await.result(body, Duration(10000, concurrent.duration.MILLISECONDS))

    document.select("#announcementList tr")
      .asScala
      .toList
      .filter(_.select("a.annmt").size > 0)
      .map(e => Rns.fromJsoupElement(e, baseUrl))
  }
}

case class Rns(
                companyName: String,
                ticker: Maybe[String],
                announcementTitle: String,
                link: String,
                source: String
              ) {

  def getRnsContent(): Future[String \/ String] = {
    val svc = url(link)
    val body = Http.default(svc OK as.String)

    val handler = (r: Response) => {
      if (r.getStatusCode >= 200 && r.getStatusCode < 300) {
        \/-(parse(r.getResponseBody).select("#content").first().text())
      } else {
        -\/(s"Failed to get RNS content at $companyName $announcementTitle ${r.getUri.toString}. Status code ${r.getStatusCode}")
      }
    }
    Http.default(svc > handler)

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




