package uk.co.rnsreader

import scala.util.matching.Regex


object ContentMatcher {
  val SINGLE_WORDS_MATCHER = "(?i)(blockchain|bitcoin|litecoin|bitcoin cash|big data|hadoop|machine learning|artificial intelligence| VR |Virtual Reality|Augmented reality|Cybersecurity)".r
  val BUBBLE_WORDS_MATCHER = "(?i)(blockchain|bitcoin|litecoin|bitcoin cash|ripple|cryptocurrency| ico |initial coin offering)".r
  val AHEAD_OF_EXPECTATIONS = "(?i)ahead of( \\w+ | )expectation(s|)|ahead of thee( \\w+ | )expectation(s|)".r
  val BELOW_EXPECTATIONS = "(?i)below( \\w+ | )expectation(s|)".r

  def matchStringInContent(stringsToMatch: List[Regex])(d: String) : List[Regex.Match] = {
    val allMatches = stringsToMatch.map {
      e => {
        e.findAllMatchIn(d)
      }
    }

    allMatches.flatMap(_.toList)
  }

  def findInterestingRns(s: String): List[Regex.Match] =
    matchStringInContent(
      List(
        SINGLE_WORDS_MATCHER,
        AHEAD_OF_EXPECTATIONS,
        BELOW_EXPECTATIONS
      )
    )(s)

  def findBubbleRns(s: String) : Option[List[Regex.Match]] = {
    val result = matchStringInContent(
      List(
        BUBBLE_WORDS_MATCHER
      )
    )(s)
    if (result.nonEmpty) Some(result) else None
  }

}
