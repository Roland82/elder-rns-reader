package uk.co.rnsreader.announcements

import org.scalatest.{FunSpec, Matchers}
import uk.co.rnsreader.announcements.AnnouncementFilterer._
import scalaz.Maybe._
class AnnouncementsFilterSpec extends FunSpec with Matchers {

  describe("Announcment filter") {
    it("Will filter out by title correctly") {
      filtersWithTitleEqualTo("Filters")(RnsItem("", Empty(), "Filters", "", "")) shouldBe false
      filtersWithTitleEqualTo("Filters")(RnsItem("", Empty(), "Filterss", "", "")) shouldBe true
    }

    it("Will filter out by title starting with correctly") {
      filtersWithTitleStartingWith("Filters")(RnsItem("", Empty(), "FiltersAndOtherStuff", "", "")) shouldBe false
      filtersWithTitleStartingWith("Filters")(RnsItem("", Empty(), "AndOtherStuff", "", "")) shouldBe true
    }
  }
}
