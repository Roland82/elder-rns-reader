package uk.co.rnsreader

import dispatch.Future
import org.joda.time.DateTime
import org.jsoup.nodes.Document
import uk.co.rnsreader.ContentMatcher.findInterestingRns
import uk.co.rnsreader.pages.{Rns, TrustNetPage}

import scala.util.matching.Regex
import scalaz.{Kleisli, \/}
import uk.co.rnsreader.WebClient._

case class Result(rns: Rns, matches: String \/ Option[List[Regex.Match]])

object ProcessRns {

  def apply(baseUrl: String, date: DateTime): Future[List[Result]] = {

    //val rnsList = getRnsList(date) >==> TrustNetPage.parseRnsLists mapT (e => )


     // val rnsList = getRnsList(baseUrl, date).filter(rnsFilter)
//      println(s"There are ${rnsList.size} rns's to go through")
//      val rnsResults = for {
//        rns      <- rnsList
//        document <- List(rns.getRnsContent())
//        result   <- List(document.map(d => Result(rns, d.map(findInterestingRns))))
//      } yield result

      //Future.sequence(rnsResults)
    Future.successful(List())

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
