package uk.co.rnsreader

import fs2.Strategy
import org.http4s.client.blaze.PooledHttp1Client
import org.joda.time.DateTime
import uk.co.rnsreader.email.EmailSender.sendEmail

import scalaz.{-\/, \/-}

object Main{
  implicit val httpClient = PooledHttp1Client()
  implicit val strategy = Strategy.fromExecutionContext(scala.concurrent.ExecutionContext.Implicits.global)
  val BASE_URL = "https://www2.trustnet.com"
  val date = DateTime.now()

  def main(args: Array[String]): Unit = {
    val task = ProcessRns(BASE_URL, date)

    val output = task.attemptFold(
      e => {
        println(e.getMessage)
        println("Try and do this search everyday to add ideas https://www.google.co.uk/search?q=new+technology+trends")
      },
      results => {
        results foreach printResultToConsole
        val emailOutput = results map createFriendlyOutput
        sendEmail(emailOutput.fold("")(_ + _ ))
        println("Try and do this search everyday to add ideas https://www.google.co.uk/search?q=new+technology+trends")
      }
    )

    output.unsafeRun
    System.exit(0)
  }

  def printResultToConsole(r: Result) = {
    r.matches match {
      case -\/(error) => println(Console.RED + "There was an error " + error)

      case \/-(result) => {
        result match {
          case Some(matches) => {
            val groupedMatches = matches.map(_.toString().toLowerCase()).groupBy(e => e)
            println(Console.WHITE + s"${r.rns.companyName} ${r.rns.ticker.map(e => "(" + e + ")").getOrElse("")} ${r.rns.announcementTitle} -> <a href='${r.rns.link}'>Link</a>")
            groupedMatches.foreach(e => {
              println(Console.YELLOW + s"${e._2.size} x ${e._1}")
            })
          }
          case None => // Don't output anything
        }

      }
    }
  }

  def createFriendlyOutput(r: Result): String = {
    r.matches match {
      case -\/(error) => "<p style='color: red'>There was an error " + error + "</p>"

      case \/-(result) => {
        result match {
          case Some(matches) => {
            val groupedMatches = matches.map(_.toString().toLowerCase()).groupBy(e => e)

            val matchList = groupedMatches.map(e => s"<p style='color: #2805B3'><b>${e._2.size} x ${e._1}</b></p>").fold("")(_ + _)
            s"${r.rns.companyName} ${r.rns.ticker.map(e => "<p style='color: green'>(" + e + ")").getOrElse("")} ${r.rns.announcementTitle} -> ${r.rns.link}</p>$matchList"
          }
          case None => ""
        }
      }
    }
  }
}

