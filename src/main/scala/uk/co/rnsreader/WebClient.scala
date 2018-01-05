package uk.co.rnsreader

import dispatch.{Future, Http, url}
import org.asynchttpclient.Response
import org.joda.time.DateTime
import org.jsoup.Jsoup.parse
import org.jsoup.nodes.Document
import uk.co.rnsreader.pages.Rns
import scalaz.Kleisli._
import scala.concurrent.ExecutionContext.Implicits.global

import scalaz.{-\/, ReaderT, \/, \/-}

object WebClient {
  private def responseHandler[T](rp: String => T, url: String)(r: Response): String \/ T = {
    if (r.getStatusCode >= 200 && r.getStatusCode < 300) {
      \/-(rp(r.getResponseBody))
    } else {
      -\/(s"Failed to get content at $url. Status code ${r.getStatusCode}")
    }
  }

  private def get[T](path: String, rp: String => T) = ReaderT[Future, String, String \/ Document] { e =>
    val svc = url(s"$e$path")
    Http.default(svc > responseHandler(parse, s"$e$path") _)
  }

  def getRnsList(date: DateTime): ReaderT[Future, String, String \/ Document] = {
    val d = date.toString("yyyMMdd")
    get(s"/Investments/LatestAnnouncements.aspx?date=$d&pno=1&limit=-1", parse)
  }

//  def getRnsContent(rns: List[Rns])(b: ReaderT[Future, String, List[String \/ Document]] = {
//    rn
//    for {
//      a <- rns
//      x <- get(a.link, parse)
//
//    } yield
//  }
}

