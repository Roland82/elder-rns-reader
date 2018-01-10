package uk.co.rnsreader.announcements.businesswire

import fs2.{Strategy, Task}
import org.http4s.client.Client
import org.joda.time.DateTime
import org.jsoup.nodes.Document
import uk.co.rnsreader.ContentMatcher.findBubbleRns

import scala.util.matching.Regex
import scala.xml.{NodeSeq, XML}
import scalaz.\/

object ProcessBusinessWire {

  def apply(baseUrl: String, cutoffDate: DateTime)(implicit client: Client, s: Strategy): Task[Vector[(BusinessWireRRSItem, Throwable \/ List[Regex.Match])]] = {
    for {
      response <- getFeed(baseUrl)
      items <- parseXmlToItems(response)
      a <- Task.parallelTraverse(items.filter(_.date.isAfter(cutoffDate)))(searchAnnouncementContent)
    } yield a
  }

  def getFeed(baseUrl: String)(implicit client: Client): Task[NodeSeq] =
    client.expect[String](s"$baseUrl/rss/home/?rss=G1QFDERJXkJeEFpQWg==").map(e => XML.loadString(e) \\ "channel" \\ "item")


  def searchAnnouncementContent(item: BusinessWireRRSItem)(implicit client: Client) : Task[(BusinessWireRRSItem, Throwable \/ List[Regex.Match])] = {
    def getAnnouncementText(d: Document) = d.select("article.bw-release-main").text()
    val matchesInFeedItem = findBubbleRns(item.title)
      .orElse(findBubbleRns(item.description))

    matchesInFeedItem match {
      case Some(e) => Task.now((item, \/.right(e)))
      case None =>
        client.expect[String](item.link)
          .attemptFold(\/.left, \/.right)
          .map(_.map(e => findBubbleRns(e).getOrElse(List.empty)))
            .map(e => (item, e))
    }
  }

  def parseXmlToItems(nodes: NodeSeq) : Task[List[BusinessWireRRSItem]] =
    Task.now(nodes.map(BusinessWireRRSItem.fromXmlNode).toList)
}
