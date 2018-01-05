package uk.co.rnsreader.email

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.simpleemail.model._
import jp.co.bizreach.ses.SESClient
import com.amazonaws.regions.Regions.EU_WEST_1

import collection.JavaConverters._

object EmailSender extends App {
  implicit val region = EU_WEST_1
  val destination = new Destination(List("roland.ormrod@googlemail.com").asJava)
  val message = new Message(new Content("This is content"), new Body(new Content("This is body")))
  val email = new SendEmailRequest("rnsreader@binaryalchemy.co.uk", destination, message)

  val sesClient = SESClient(new BasicAWSCredentials("FROM CONFIG", "FROM CONFIG"))
  println("HI HI HI 3")
  val future = sesClient.aws.sendEmailAsync(email)
  println("HI HI HI 2")
  val result = future.get()
  println("HI HI HI")
}
