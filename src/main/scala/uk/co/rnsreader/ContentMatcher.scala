package uk.co.rnsreader

import scala.util.matching.Regex


object ContentMatcher {
  val SINGLE_WORDS_MATCHER = "(?i)(blockchain|bitcoin|litecoin|bitcoin cash|big data|hadoop|machine learning|artificial intelligence| VR |Virtual Reality|Augmented reality|Cybersecurity|CRISPR)".r
  val BUBBLE_WORDS_MATCHER = "(?i)(blockchain|bitcoin|litecoin|bitcoin cash|ripple|cryptocurrency)".r
  val AHEAD_OF_EXPECTATIONS = "(?i)ahead of( \\w+ | )expectation(s|)|ahead of thee( \\w+ | )expectation(s|)".r
  val BELOW_EXPECTATIONS = "(?i)below( \\w+ | )expectation(s|)".r

  def matchStringInContent(stringsToMatch: List[Regex])(d: String) : Option[List[Regex.Match]] = {
    val allMatches = stringsToMatch.map {
      e => {
        e.findAllMatchIn(d)
      }
    }

    val matches = allMatches.flatMap(_.toList)
    if (matches.nonEmpty) Some(matches) else None
  }

  def findInterestingRns : String => Option[List[Regex.Match]] =
    matchStringInContent(
      List(
        SINGLE_WORDS_MATCHER,
        AHEAD_OF_EXPECTATIONS,
        BELOW_EXPECTATIONS
      )
    )

  def findBubbleRns : String => Option[List[Regex.Match]] =
    matchStringInContent(
      List(
        BUBBLE_WORDS_MATCHER
      )
    )
}
