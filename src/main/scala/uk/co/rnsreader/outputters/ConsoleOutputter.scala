package uk.co.rnsreader.outputters

import uk.co.rnsreader.announcements.{AnnouncementResult, BusinessWireRRSItem, RnsItem}

import scalaz.{-\/, \/, \/-}

object ConsoleOutputter {
  def outputConsoleLog(a: Throwable \/ AnnouncementResult): Unit = {

    val announcmentDescription = (result: AnnouncementResult) => result.newsItem match {
      case BusinessWireRRSItem(title, description, link, date) => s"Business wire announcement at $link with title $title on ${date.toString("dd/MM/yyyy hh:mm:ss")}} "
      case RnsItem(companyName, ticker, title, path, _) => s"RNS announcement for company $companyName ${ticker.map(e => "(" + e + ")").getOrElse("")} at $path with title $title"
    }

    a match {
      case -\/(error) => println(s"Error getting match due to ${error.getMessage}")
      case \/-(result) => {
        if (result.matches.nonEmpty) {
          val groupedMatches = result.matches.map(_.toString().toLowerCase()).groupBy(e => e)
          println(Console.WHITE + announcmentDescription(result))
          groupedMatches.foreach(e => println(Console.YELLOW + s"${e._2.size} x ${e._1}"))
        } else {
          println(s"No Match for " + announcmentDescription(result))
        }
      }
    }
  }
}
