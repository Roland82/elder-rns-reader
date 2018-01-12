package uk.co.rnsreader

import fs2.Strategy
import org.http4s.client.blaze.PooledHttp1Client
import org.joda.time.DateTime
import uk.co.rnsreader.announcements.businesswire.{BusinessWireBaseUrlTag, ProcessBusinessWire}
import uk.co.rnsreader.announcements.rns.{ProcessRns, TrustNetBaseUrl, TrustNetBaseUrlTag}
import uk.co.rnsreader.outputters.{AwsEmailCredentials, EmailSender}
import uk.co.rnsreader.outputters.ConsoleOutputter._
import com.typesafe.config.ConfigFactory
import uk.co.rnsreader.announcements.AnnouncementFilterer._
import shapeless.tag

import scalaz.Scalaz._

object Main{
  val configuration = ConfigFactory.load()
  val announcementFilter = filterFromConfig(configuration)(_)
  val trustNetBaseUrl = tag[TrustNetBaseUrlTag](configuration.getString("processors.rns.baseUrl"))
  val businessWireBaseUrl = tag[BusinessWireBaseUrlTag](configuration.getString("processors.businesswire.baseUrl"))
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
      case "RNS" => ProcessRns.process(trustNetBaseUrl)(announcementFilter)(cutoffDateTime)
      case "Businesswire" => ProcessBusinessWire.process(businessWireBaseUrl)((e) => true)(cutoffDateTime)
      case _ => {
        println("Unknown news source " + newsSource + ". Exiting")
        throw new Exception("Unknown news source " + newsSource + ". Exiting")
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

