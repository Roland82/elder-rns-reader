package uk.co.rnsreader

import fs2.Strategy
import org.http4s.client.blaze.PooledHttp1Client
import org.joda.time.DateTime
import uk.co.rnsreader.announcements.businesswire.ProcessBusinessWire
import uk.co.rnsreader.announcements.rns.ProcessRns
import uk.co.rnsreader.email.AwsEmailCredentials
import uk.co.rnsreader.outputters.ConsoleOutputter._

object Main{
  val newsSource = System.getenv("NEWS_SOURCE")
  val awsEmailCredentials = AwsEmailCredentials(System.getenv("AWS_ACCESS_KEY"), System.getenv("AWS_SECRET_KEY"))
  implicit val httpClient = PooledHttp1Client()
  implicit val strategy = Strategy.fromExecutionContext(scala.concurrent.ExecutionContext.Implicits.global)
  val cutoffDateTime = DateTime.now().minusMinutes(1200)

  def main(args: Array[String]): Unit = {
    val task = newsSource match {
      case "RNS" => ProcessRns.process("https://www2.trustnet.com")(cutoffDateTime)
      case "Businesswire" => ProcessBusinessWire.process("https://feed.businesswire.com")(cutoffDateTime)
      case _ => {
        println("Unknown news source " + newsSource + ". Exiting")
        throw new Exception()
      }
    }

    val output = task.attemptFold(
      e => {
        println(e.getMessage)
        println("Try and do this search everyday to add ideas https://www.google.co.uk/search?q=new+technology+trends")
      },
      results => {
        results foreach outputConsoleLog
       // val emailOutput = results map createFriendlyOutput
        // sendEmail(emailOutput.fold("")(_ + _ ), awsEmailCredentials)
        println("Try and do this search everyday to add ideas https://www.google.co.uk/search?q=new+technology+trends")
      }
    )

    output.unsafeRun
    System.exit(0)
  }

//  def createFriendlyOutput(r: Result): String = {
//    r.matches match {
//      case -\/(error) => "<p style='color: red'>There was an error " + error + "</p>"
//
//      case \/-(result) => {
//        result match {
//          case Some(matches) => {
//            val groupedMatches = matches.map(_.toString().toLowerCase()).groupBy(e => e)
//
//            val matchList = groupedMatches.map(e => s"<tr><td style='width: 50%;'>${e._2.size}x</td><td>${e._1}</td><tr>").fold("")(_ + _)
//
//            val tickerSymbolHtml = r.rns.ticker.map(r => s"(<a target='_blank' href='http://www.lse.co.uk/SharePrice.asp?SharePrice=$r'>$r</a>)").getOrElse("")
//            s"<table style=' margin-bottom: 10px; border: 1px black solid; width: 100%; text-align: left; font-family: helvetica; font-weight: lighter'>" +
//              s"<tr><th style='width: 50%;'>${r.rns.companyName} $tickerSymbolHtml</th><th><a href='${r.rns.path}'>${r.rns.title}</a></th></t>" +
//              s"$matchList" +
//            "</table>"
//          }
//          case None => ""
//        }
//      }
//    }
//  }
}

