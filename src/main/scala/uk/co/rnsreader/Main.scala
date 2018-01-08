package uk.co.rnsreader

import org.joda.time.DateTime
import uk.co.rnsreader.email.EmailSender.sendEmail
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.{-\/, \/-}

object Main{
  val BASE_URL = "https://www2.trustnet.com"
  val date = new DateTime(2018, 1, 8, 0, 0)

  def main(args: Array[String]): Unit = {
    val future = ProcessRns(BASE_URL, date)

    future.onComplete {
      case Success(results) => {
        results foreach printResultToConsole
        val emailOutput = results map createFriendlyOutput
        sendEmail(emailOutput.fold("")(_ + _ ))
        println("Try and do this search everyday to add ideas https://www.google.co.uk/search?q=new+technology+trends")
      }
      case Failure(f) => {
        println(f.getMessage)
        println("Try and do this search everyday to add ideas https://www.google.co.uk/search?q=new+technology+trends")
      }
    }


    Await.result(future, Duration(5, concurrent.duration.MINUTES))
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

