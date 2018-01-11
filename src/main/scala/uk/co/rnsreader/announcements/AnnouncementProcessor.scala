package uk.co.rnsreader.announcements

import fs2.{Strategy, Task}
import org.http4s.client.Client
import org.joda.time.DateTime

import scalaz.\/


trait AnnouncementProcessor {
  def process(baseUrl: String)(date: DateTime)(implicit client: Client, strategy: Strategy): Task[Vector[Throwable \/ AnnouncementResult]]
}
