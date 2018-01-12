package uk.co.rnsreader.outputters

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions.EU_WEST_1
import com.amazonaws.services.simpleemail.model._
import jp.co.bizreach.ses.SESClient
import org.joda.time.DateTime
import uk.co.rnsreader.announcements.{AnnouncementResult, BusinessWireRRSItem, RnsItem}
import collection.JavaConverters._
import scalaz.{-\/, \/, \/-}

object EmailSender {
  def sendEmail(a: List[Throwable \/ AnnouncementResult], awsEmailCredentials: AwsEmailCredentials) = {
    val content = createHtml(a)

    if (content != "") {
      val date = DateTime.now().toString("dd/MM/yyyy HH:mm")
      implicit val region = EU_WEST_1
      val destination = new Destination(List("roland.ormrod@googlemail.com").asJava)
      val message = new Message(new Content(s"Rns Results at $date"), new Body().withHtml(new Content(content)))
      val email = new SendEmailRequest("rnsreader@binaryalchemy.co.uk", destination, message)

      val sesClient = SESClient(new BasicAWSCredentials(awsEmailCredentials.accessKey, awsEmailCredentials.secretKey))
      sesClient.aws.sendEmail(email)
    } else {
      println("No email content to send")
    }
  }


  private def createHtml(a: List[Throwable \/ AnnouncementResult]): String = {
    (a map generateResultEntry).fold("")(_ + _)
  }

  private def generateResultEntry(r: Throwable \/ AnnouncementResult) = {
    r match {
      case -\/(error) => "<p style='color: red'>There was an error " + error + "</p>"

      case \/-(result) => {
        if (result.matches.nonEmpty) {
          val groupedMatches = result.matches.map(_.toString().toLowerCase()).groupBy(e => e)
          val matchList = groupedMatches.map(e => s"<tr><td style='width: 50%;'>${e._2.size}x</td><td>${e._1}</td><tr>").fold("")(_ + _)

          val announcementLinkHtml = result.newsItem match {
            case b: BusinessWireRRSItem => s"<a href='${b.link}'>${b.title}</a>"
            case rns: RnsItem => s"<a href='https://www2.trustnet.com${rns.path}'>${rns.title}</a>"
          }

          val companyAndTicker = result.newsItem match {
            case b: BusinessWireRRSItem => s"Unknown Company (BusinessWire TODO) "
            case rns: RnsItem => rns.ticker.map(r => s"${rns.companyName} (<a target='_blank' href='http://www.lse.co.uk/SharePrice.asp?SharePrice=$r'>$r</a>)").getOrElse("")
          }

          s"<table style=' margin-bottom: 10px; border: 1px black solid; width: 100%; text-align: left; font-family: helvetica; font-weight: lighter'>" +
            s"<tr><th style='width: 50%;'>$companyAndTicker</th><th>$announcementLinkHtml</th></t>" +
            s"$matchList" +
            "</table>"
        } else {
          ""
        }
      }
    }
  }
}
