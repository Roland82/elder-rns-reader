package uk.co.rnsreader.announcements

import fs2.{Strategy, Task}
import org.http4s.client.Client
import org.joda.time.DateTime
import uk.co.rnsreader.announcements.AnnouncementFilterer.AnnouncementFilter

import scalaz.\/


trait AnnouncementProcessor {
  def process(baseUrl: String)(filter: AnnouncementFilter)(date: DateTime)(implicit client: Client, strategy: Strategy): Task[Vector[Throwable \/ AnnouncementResult]]
}
