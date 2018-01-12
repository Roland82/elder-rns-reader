package uk.co.rnsreader.announcements

import com.typesafe.config.Config
import scala.collection.JavaConverters._

object AnnouncementFilterer {
  type AnnouncementFilter = RnsItem => Boolean

  def filterFromConfig(c: Config)(rns: RnsItem): Boolean = {
    (c.getStringList("rnsProcessor.filter.title.equalTo").asScala.toStream.map(filtersWithTitleEqualTo) ++
      c.getStringList("rnsProcessor.filter.title.startingWith").asScala.toStream.map(filtersWithTitleStartingWith))
    .forall(e => e(rns))
  }

  def filtersWithTitleEqualTo(title: String): AnnouncementFilter = (rns: RnsItem) => rns.title != title
  def filtersWithTitleStartingWith(title: String): AnnouncementFilter = (rns: RnsItem) => !rns.title.startsWith(title)
}


