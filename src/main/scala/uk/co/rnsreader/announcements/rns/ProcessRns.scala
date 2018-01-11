package uk.co.rnsreader.announcements.rns

import fs2.{Strategy, Task}
import org.http4s.client.Client
import org.joda.time.DateTime
import uk.co.rnsreader.ContentMatcher.findInterestingRns
import uk.co.rnsreader.announcements.{AnnouncementResult, RnsItem}
import uk.co.rnsreader.announcements.rns.TrustNetClient.{getRnsContent, _}

import scalaz.{\/, \/-}

object ProcessRns {

  def apply(baseUrl: String)(date: DateTime)(implicit client: Client, strategy: Strategy): Task[Vector[Throwable \/ AnnouncementResult]] = {
    val rnsTask = getTrustNetPage(baseUrl)(date).map(e => e.map(_.filter(rnsFilter)))

    def callEndpoints(rnsItems: List[RnsItem]): Task[Vector[Throwable \/ String]] = {
      Task.parallelTraverse(rnsItems)(rns => getRnsContent(baseUrl)(rns.path))
    }



    rnsTask.flatMap { rnsList => {
      val task = rnsList.map(callEndpoints)
      task match {
        case \/-(result) => result result.map(generateResult)
        case e => Task.now(e)
        }
      }

    }

    for {
      rnsList    <- rnsTask
      rnsDetails <- rnsList.map(callEndpoints)

    } yield rnsDetails.map(d => d.map(generateResult))
  }

  def generateResult(a: (RnsItem, Throwable \/ String)): Throwable \/ AnnouncementResult = {
    val (rnsItem, rnsContentCallResult) = a
    rnsContentCallResult.map(findInterestingRns).map(e => AnnouncementResult(rnsItem, e.getOrElse(List.empty)))
  }


  def rnsFilter(rns: RnsItem) : Boolean =
    rns.title.trim.toLowerCase != "transaction in own shares" &&
      rns.title.trim.toLowerCase != "total voting rights" &&
      rns.title.trim.toLowerCase != "holding(s) in company" &&
      rns.title.trim.toLowerCase != "director/pdmr shareholding" &&
      rns.title.trim.toLowerCase != "block listing interim Review" &&
      rns.title.trim.toLowerCase != "price monitoring extension" &&
      rns.title.trim.toLowerCase != "second price monitoring extn" &&
      rns.title.trim.toLowerCase != "pdmr shareholding" &&
      rns.title.trim.toLowerCase != "dividend declaration" &&
      rns.title.trim.toLowerCase != "grant of options" &&
      !rns.title.trim.toLowerCase.startsWith("form 8.5")
}
