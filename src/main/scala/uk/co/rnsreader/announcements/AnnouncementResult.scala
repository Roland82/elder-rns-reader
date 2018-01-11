package uk.co.rnsreader.announcements

import scala.util.matching.Regex

case class AnnouncementResult(newsItem: NewsItem, matches: List[Regex.Match])