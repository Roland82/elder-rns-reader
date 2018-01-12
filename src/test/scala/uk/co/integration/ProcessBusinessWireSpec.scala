package uk.co.integration

import fs2.Strategy
import org.http4s.client.blaze.PooledHttp1Client
import org.joda.time.DateTime
import org.scalatest.{FunSpec, Matchers}
import uk.co.rnsreader.announcements.BusinessWireRRSItem
import uk.co.rnsreader.announcements.businesswire.ProcessBusinessWire


class ProcessBusinessWireSpec extends FunSpec with Matchers {
  implicit val httpClient = PooledHttp1Client()
  implicit val strategy = Strategy.fromExecutionContext(scala.concurrent.ExecutionContext.Implicits.global)

  describe("Process Businesswire feed") {
    val task = ProcessBusinessWire.process("http://localhost:8080")(new DateTime(2018, 1, 10, 0, 0))
    val result = task.unsafeRun()


    val result0 = result(0)
    val result1 = result(1)
    val result2 = result(2)

    it("should detect 3 matches in the test content") {
      result.size shouldBe 3
    }

    describe("Where it finds a match in the title of the XML feed item") {
      it("Should report back that there was one regex match") {
        result0.fold((e) => fail(e.getMessage),
          e => {
            e.matches.size shouldEqual 1
            e.matches.head.toString() shouldEqual "blockchain"
          }
        )
      }

      it("The RNS item returned should have the title with the matched word in it") {
        result0.fold((e) => fail(e.getMessage),
          e => {
            e.newsItem.asInstanceOf[BusinessWireRRSItem].title shouldEqual "This is a title with the word blockchain in it"
          }
        )
      }
    }

    describe("Where it finds a match in the description of the XML feed item") {
      it("Should report back that there was one regex match") {
        result1.fold(
          (e) => fail(e.getMessage),
          e => {
            e.matches.size shouldEqual 1
            e.matches.head.toString() shouldEqual "blockchain"
          }
        )
      }

      it("The RNS item returned should have the title with the matched word in it") {
        result1.fold(
          (e) => fail(e.getMessage),
          e => {
            e.newsItem.asInstanceOf[BusinessWireRRSItem].title shouldEqual "A description with the word blockchain in it"
          }
        )
      }
    }

    describe("When no matches are found in the XML feed it should check the annoucement and find matches there") {
      it("Should report back that there was 7 regex matches") {
        result2.fold(
          (e) => fail(e.getMessage),
          e => {
            e.matches.size shouldEqual 7
            e.matches.head.toString() shouldEqual "Blockchain"
          }
        )


      }

      it("The announcment shouldnt have any matching words in the title or description") {
        result2.fold(
          (e) => fail(e.getMessage),
          e => {
            e.newsItem.asInstanceOf[BusinessWireRRSItem].description shouldEqual "Announcement with match in content"
            e.newsItem.asInstanceOf[BusinessWireRRSItem].title shouldEqual "Announcement with match in content"
          }
        )
      }
    }
  }
}
