import org.scalatest.{FlatSpec, FunSpec, Matchers}
import uk.co.rnsreader.ContentMatcher._

class ContentMatcherSpec extends FunSpec with Matchers {
  describe("Cryptocurrency Matcher") {

    List(
      "We are changing our name to blockchain",
      "We are changing our name to BLOCKCHAIN",
      "We are looking at litecoin trading",
      "We are looking at bitcoin trading",
      "We are looking at bitcoin cash trading"
    ).foreach(e => {
      it("should match a string like " + e) {
        matchStringInContent(List(SINGLE_WORDS_MATCHER))(e).size shouldEqual 1
      }
    }
    )

    List(
      "We are changing our name to blockchsain"
    ).foreach(e => {
      it("should not match a string like " + e) {
        matchStringInContent(List(SINGLE_WORDS_MATCHER))(e).size shouldEqual 0
      }
    }
    )
  }

  describe("Ahead of market expectations Matcher") {
    List(
      "This is ahead of market expectations",
      "This is ahead of market expectation",
      "This is ahead of expectation",
      "This is ahead of expectations",
      "This is ahead of our expectations",
      "This is AHEAD OF EXPECTATIONS"
    ).foreach(e => {
      it("should match a string like " + e) {
        matchStringInContent(List(AHEAD_OF_EXPECTATIONS))(e).size shouldEqual 1
      }
    })
  }

  describe("Virtual Reality Matcher") {
    List(
      "Aquired a Virtual reality company",
      "we are making use of VR technology"
    ).foreach(e => {
      it("should match a string like " + e) {
        matchStringInContent(List(SINGLE_WORDS_MATCHER))(e).size shouldEqual 1
      }
    })

    List(
      "Word that shouldnt be matched here TESTVR",
      "Word that shouldnt be matched here VRTEST",
      "Word that shouldnt be matched here TEVRST"
    ).foreach(e => {
      it("shouldn't match a string like " + e) {
        matchStringInContent(List(SINGLE_WORDS_MATCHER))(e).size shouldEqual 0
      }
    })
  }
}