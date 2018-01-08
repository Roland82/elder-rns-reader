package uk.co.rnsreader.email

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.simpleemail.model._
import jp.co.bizreach.ses.SESClient
import com.amazonaws.regions.Regions.EU_WEST_1

import collection.JavaConverters._

object EmailSender {
  def sendEmail(content: String) = {
    implicit val region = EU_WEST_1
    val destination = new Destination(List("roland.ormrod@googlemail.com").asJava)
    val message = new Message(new Content("This is content"), new Body().withHtml(new Content(content)))
    val email = new SendEmailRequest("rnsreader@binaryalchemy.co.uk", destination, message)

    val sesClient = SESClient(new BasicAWSCredentials("AKIAJTF6R6C4BSFZ5ZSA", "jU78GDWogOOBNrsB3s/skOIGQPP5oY/KWJrawNM8"))
    val future = sesClient.aws.sendEmailAsync(email)
    val result = future.get()
  }
}
