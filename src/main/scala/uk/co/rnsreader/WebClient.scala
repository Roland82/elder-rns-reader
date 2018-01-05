package uk.co.rnsreader

import dispatch.{Future, Http, as, url}
import org.asynchttpclient.Response
import org.jsoup.Jsoup.parse

import scalaz.{-\/, \/, \/-}

object WebClient {
  def get(path: String) = {
    Future[String \/ String] = {
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
}
