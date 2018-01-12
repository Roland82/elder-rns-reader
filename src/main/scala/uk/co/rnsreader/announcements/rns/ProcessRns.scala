package uk.co.rnsreader.announcements.rns

import fs2.{Strategy, Task}
import org.http4s.client.Client
import org.joda.time.DateTime
import uk.co.rnsreader.ContentMatcher.findInterestingRns
import uk.co.rnsreader.announcements.AnnouncementFilterer.AnnouncementFilter
import uk.co.rnsreader.announcements.{AnnouncementProcessor, AnnouncementResult, RnsItem}
import uk.co.rnsreader.announcements.rns.TrustNetClient.{getRnsContent, _}

import scalaz.{-\/, \/, \/-}

object ProcessRns extends AnnouncementProcessor {

  def process(baseUrl: String)(filter: AnnouncementFilter)(date: DateTime)(implicit client: Client, strategy: Strategy): Task[Vector[Throwable \/ AnnouncementResult]] = {
    val rnsTask = getTrustNetPage(baseUrl)(date).map(e => e.map(rns => rns.filter(filter)))

    def callEndpoints(rnsListCallResult: Throwable \/ List[RnsItem]): Task[Vector[Throwable \/ AnnouncementResult]] = rnsListCallResult match {
      case -\/(error) => Task.fail(error)
      case \/-(rnsItems) => Task.parallelTraverse(rnsItems) { rns =>
        getRnsContent(baseUrl)(rns.path).map {
          case \/-(result) => \/-(AnnouncementResult(rns, findInterestingRns(result)))
          case -\/(e) => -\/(e)
        }
      }
    }

    rnsTask.flatMap(callEndpoints)
  }
}
