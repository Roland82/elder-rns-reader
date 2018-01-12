package uk.co.rnsreader.announcements.businesswire

import fs2.{Strategy, Task}
import org.http4s.client.Client
import org.joda.time.DateTime
import org.jsoup.nodes.Document
import uk.co.rnsreader.ContentMatcher.findBubbleRns
import uk.co.rnsreader.announcements.{AnnouncementProcessor, AnnouncementResult, BusinessWireRRSItem}

import scala.xml.{NodeSeq, XML}
import scalaz.\/

object ProcessBusinessWire extends AnnouncementProcessor {

  def process(baseUrl: String)(date: DateTime)(implicit client: Client, s: Strategy): Task[Vector[Throwable \/ AnnouncementResult]] = {
    for {
      items <- getFeed(baseUrl).flatMap(parseXmlToItems)
      x     <- Task.now(items.filter(_.date.isAfter(date)))
      a     <- Task.parallelTraverse(x)(searchAnnouncementContent)
    } yield a
  }

  def getFeed(baseUrl: String)(implicit client: Client): Task[NodeSeq] =
    client.expect[String](s"$baseUrl/rss/home/?rss=G1QFDERJXkJeEFpQWg==").map(e => XML.loadString(e) \\ "channel" \\ "item")


  def searchAnnouncementContent(item: BusinessWireRRSItem)(implicit client: Client) : Task[Throwable \/ AnnouncementResult] = {
    def getAnnouncementText(d: Document) = d.select("article.bw-release-main").text()
    val matchesInFeedItem = findBubbleRns(item.title).orElse(findBubbleRns(item.description))

    matchesInFeedItem match {
      case Some(e) => Task.now(\/.right(AnnouncementResult(item, e)))
      case None =>
        client.expect[String](item.link)
          .attemptFold(\/.left, \/.right)

          .map(_.map(e => findBubbleRns(e).getOrElse(List.empty)))
          .map(e => e.map(f =>  AnnouncementResult(item, f)))
    }
  }

  def parseXmlToItems(nodes: NodeSeq) : Task[List[BusinessWireRRSItem]] =
    Task.now(nodes.map(BusinessWireRRSItem.fromXmlNode).toList)
}
