package uk.co.rnsreader

import fs2.Strategy
import org.http4s.client.blaze.PooledHttp1Client
import org.joda.time.DateTime
import uk.co.rnsreader.announcements.businesswire.ProcessBusinessWire
import uk.co.rnsreader.announcements.rns.ProcessRns
import uk.co.rnsreader.outputters.{AwsEmailCredentials, EmailSender}
import uk.co.rnsreader.outputters.ConsoleOutputter._

import scalaz.Scalaz._

object Main{
  val newsSource = System.getenv("NEWS_SOURCE")
  val sendEmail = Option(System.getenv("ENABLE_SEND_EMAIL"))
  val awsAccessKey = Option(System.getenv("AWS_ACCESS_KEY"))
  val awsSecretKey = Option(System.getenv("AWS_SECRET_KEY"))

  val awsEmailCredentials = sendEmail match {
    case Some(s) => if (s == "true") (awsAccessKey |@| awsSecretKey) (AwsEmailCredentials.apply) else None
    case _ => None
  }

  implicit val httpClient = PooledHttp1Client()
  implicit val strategy = Strategy.fromExecutionContext(scala.concurrent.ExecutionContext.Implicits.global)
  val cutoffDateTime = DateTime.now().minusMinutes(10)

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
        awsEmailCredentials.foreach(e => EmailSender.sendEmail(results.toList, e))
        results foreach outputConsoleLog
        println("Try and do this search everyday to add ideas https://www.google.co.uk/search?q=new+technology+trends")
      }
    )

    output.unsafeRun
    System.exit(0)
  }
}

