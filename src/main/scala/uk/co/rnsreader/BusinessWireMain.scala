package uk.co.rnsreader

import fs2.Strategy
import org.http4s.client.blaze.PooledHttp1Client
import org.joda.time.DateTime
import uk.co.rnsreader.announcements.businesswire.{BusinessWireRRSItem, ProcessBusinessWire}
import uk.co.rnsreader.email.AwsEmailCredentials
import uk.co.rnsreader.email.EmailSender.sendEmail
import scala.util.matching.Regex
import scalaz.{-\/, \/, \/-}


object BusinessWireMain {
  val awsEmailCredentials = AwsEmailCredentials(System.getenv("AWS_ACCESS_KEY"), System.getenv("AWS_SECRET_KEY"))
  implicit val httpClient = PooledHttp1Client()
  implicit val strategy = Strategy.fromExecutionContext(scala.concurrent.ExecutionContext.Implicits.global)
  val BASE_URL = "https://feed.businesswire.com"
  val date = DateTime.now()
  val cutoffDate = date.minusMinutes(1)

  def main(args: Array[String]): Unit = {
    val task = ProcessBusinessWire(BASE_URL, cutoffDate)

    val output = task.attemptFold(
      e => {
        println(e.getMessage)
        println("Try and do this search everyday to add ideas https://www.google.co.uk/search?q=new+technology+trends")
      },
      results => {
        results foreach printResultToConsole
        val emailOutput = results map createFriendlyOutput
        if (results.nonEmpty) {
          sendEmail(emailOutput.fold("")(_ + _), awsEmailCredentials)
        }
      }
    )

    output.unsafeRun
    System.exit(0)
  }

  def printResultToConsole(r: (BusinessWireRRSItem, Throwable \/ List[Regex.Match])) = {
    val (announcement, result) = r

    result match {
      case -\/(error) => {
        // TODO: Handle 302 redirection (Too many in the console
        //println(Console.WHITE + s"${announcement.date} ${announcement.title} @ ${announcement.link} -> ${error.getMessage}")
      }
      case \/-(matches) => {
        val groupedMatches = matches.map(_.toString().toLowerCase()).groupBy(e => e)
        println(Console.WHITE + s"${announcement.date} ${announcement.title} @ ${announcement.link}")
        groupedMatches.foreach(e => {
          println(Console.YELLOW + s"${e._2.size} x ${e._1}")
        })
      }
    }

  }

  def createFriendlyOutput(r: (BusinessWireRRSItem, Throwable \/ List[Regex.Match])): String = {
    val (announcement, result) = r
    result match {
      case -\/(_) => {
        // TODO: Too many errors to deal with this now (302 redirects)
        ""
      }

      case \/-(matches) => {
        val groupedMatches = matches.map(_.toString().toLowerCase()).groupBy(e => e)

        val matchList = groupedMatches.map(e => s"<tr><td style='width: 50%;'>${e._2.size}x</td><td>${e._1}</td><tr>").fold("")(_ + _)

        // val tickerSymbolHtml = r.rns.ticker.map(r => s"(<a target='_blank' href='http://www.lse.co.uk/SharePrice.asp?SharePrice=$r'>$r</a>)").getOrElse("")
        s"<table style=' margin-bottom: 10px; border: 1px black solid; width: 100%; text-align: left; font-family: helvetica; font-weight: lighter'>" +
          s"<tr><th style='width: 50%;'>Company Name Parsing still to be done</th><th><a href='${announcement.link}'>${announcement.title}</a></th></t>" +
          s"$matchList" +
          "</table>"
      }
    }
  }
}
