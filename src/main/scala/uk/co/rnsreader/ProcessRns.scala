package uk.co.rnsreader

import fs2.{Strategy, Task}
import org.http4s.client.Client
import org.joda.time.DateTime
import uk.co.rnsreader.ContentMatcher.findInterestingRns
import uk.co.rnsreader.pages.Rns
import uk.co.rnsreader.pages.TrustNetPage.getRnsList

import scala.util.matching.Regex
import scalaz.\/

case class Result(rns: Rns, matches: String \/ Option[List[Regex.Match]])

object ProcessRns {

  def apply(baseUrl: String, date: DateTime)(implicit client: Client, strategy: Strategy): Task[List[Result]] = {
    val rnsList = getRnsList(baseUrl, date).map(e => e.filter(rnsFilter))

    for {
      rns      <- rnsList
      document <- Task.parallelTraverse(rns)(e => e.getRnsContent())
    } yield document.toList.map(d => Result(d._1, \/.right(findInterestingRns(d._2))))
  }

  def rnsFilter(rns: Rns) : Boolean =
    rns.announcementTitle.trim.toLowerCase != "transaction in own shares" &&
      rns.announcementTitle.trim.toLowerCase != "total voting rights" &&
      rns.announcementTitle.trim.toLowerCase != "holding(s) in company" &&
      rns.announcementTitle.trim.toLowerCase != "director/pdmr shareholding" &&
      rns.announcementTitle.trim.toLowerCase != "block listing interim Review" &&
      rns.announcementTitle.trim.toLowerCase != "price monitoring extension" &&
      rns.announcementTitle.trim.toLowerCase != "second price monitoring extn" &&
      rns.announcementTitle.trim.toLowerCase != "pdmr shareholding" &&
      rns.announcementTitle.trim.toLowerCase != "dividend declaration" &&
      rns.announcementTitle.trim.toLowerCase != "grant of options" &&
      !rns.announcementTitle.trim.toLowerCase.startsWith("form 8.5")
}
