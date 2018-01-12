package uk.co.rnsreader.announcements.businesswire

import fs2.{Strategy, Task}
import org.http4s.{Header, Headers, Request, Uri}
import org.http4s.client.Client
import org.joda.time.DateTime
import org.jsoup.nodes.Document
import uk.co.rnsreader.ContentMatcher.findBubbleRns
import uk.co.rnsreader.announcements.AnnouncementFilterer.AnnouncementFilter
import uk.co.rnsreader.announcements.{AnnouncementResult, BusinessWireRRSItem}

import scala.xml.{NodeSeq, XML}
import scalaz.{-\/, \/}

object ProcessBusinessWire {

  def process(baseUrl: BusinessWireBaseUrl)(filter: AnnouncementFilter)(cutoffDate: DateTime)(implicit client: Client, s: Strategy): Task[Vector[Throwable \/ AnnouncementResult]] = {
    for {
      items <- getFeed(baseUrl).flatMap(parseXmlToItems)
      x     <- Task.now(items.filter(_.date.isAfter(cutoffDate)))
      a     <- Task.parallelTraverse(x)(searchAnnouncementContent)
    } yield a
  }

  def getFeed(baseUrl: BusinessWireBaseUrl)(implicit client: Client): Task[NodeSeq] =
    client.expect[String](s"$baseUrl/rss/home/?rss=G1QFDERJXkJeEFpQWg==").map(e => XML.loadString(e) \\ "channel" \\ "item")


  def searchAnnouncementContent(item: BusinessWireRRSItem)(implicit client: Client) : Task[Throwable \/ AnnouncementResult] = {
    def getAnnouncementText(d: Document) = d.select("article.bw-release-main").text()

    val matchesInFeedItem = findBubbleRns(item.title).orElse(findBubbleRns(item.description))

    matchesInFeedItem match {
      case Some(e) => Task.now(\/.right(AnnouncementResult(item, e)))
      case None =>
        Uri.fromString(item.link.substring(0, item.link.lastIndexOf("/"))).fold(
          f => Task.now(-\/(f.getCause)),
          r => {
            val request = Request(uri = r, headers = Headers(Header("accept", "text/html"), Header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")))
            client.expect[String](req = request)
              .attemptFold(\/.left, \/.right)

              .map(_.map(e => findBubbleRns(e).getOrElse(List.empty)))
              .map(e => e.map(f => AnnouncementResult(item, f)))
          }
        )
    }
  }

  def parseXmlToItems(nodes: NodeSeq) : Task[List[BusinessWireRRSItem]] =
    Task.now(nodes.map(BusinessWireRRSItem.fromXmlNode).toList)
}
