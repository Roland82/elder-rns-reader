package uk.co.rnsreader.announcements.rns

import cats.data.ReaderT
import fs2.{Strategy, Task}
import org.http4s
import org.http4s.client
import org.http4s.client.Client
import org.joda.time.DateTime
import org.jsoup.nodes.Document
import org.jsoup.Jsoup.parse
import uk.co.rnsreader.announcements.RnsItem

import scala.collection.JavaConverters._
import scalaz.\/

object TrustNetClient {

  def getRnsContent(baseUrl: TrustNetBaseUrl)(path: String)(implicit client: Client, strategy: Strategy) : Task[Throwable \/ String] = {
    println("Getting rns content for " + baseUrl + path)
    client.expect[String](baseUrl + path)
      .attemptFold(\/.left, \/.right)
      .map(e => e.map(parse))
      .map(_.map(_.select("#content").first().text()))
  }

  def getTrustNetPage(baseUrl: TrustNetBaseUrl)(d: DateTime)(implicit client: Client, strategy: Strategy) : Task[Throwable \/ List[RnsItem]] = {
    def mapDocumentToRnsList(d: Throwable \/ Document): Throwable \/ List[RnsItem] = {
      d.map(_.select("#announcementList tr")
        .asScala
        .toList
        .filter(_.select("a.annmt").size > 0)
        .map(RnsItem.fromJsoupElement))
    }

    val date = d.toString("yyyMMdd")
    client.expect[String](s"$baseUrl/Investments/LatestAnnouncements.aspx?date=$date&pno=1&limit=-1")
      .attemptFold(\/.left, \/.right)
      .map(e => e.map(parse))
      .map(mapDocumentToRnsList)
  }
}
