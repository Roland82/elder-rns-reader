package uk.co.rnsreader.email

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.simpleemail.model._
import jp.co.bizreach.ses.SESClient
import com.amazonaws.regions.Regions.EU_WEST_1
import org.joda.time.DateTime

import collection.JavaConverters._

object EmailSender {
  def sendEmail(content: String, awsEmailCredentials: AwsEmailCredentials) = {
    val date = DateTime.now().toString("dd/MM/yyyy HH:mm")
    implicit val region = EU_WEST_1
    val destination = new Destination(List("roland.ormrod@googlemail.com").asJava)
    val message = new Message(new Content(s"Rns Results at $date"), new Body().withHtml(new Content(content)))
    val email = new SendEmailRequest("rnsreader@binaryalchemy.co.uk", destination, message)

    val sesClient = SESClient(new BasicAWSCredentials(awsEmailCredentials.accessKey, awsEmailCredentials.secretKey))
    val future = sesClient.aws.sendEmailAsync(email)
    val result = future.get()
  }
}
