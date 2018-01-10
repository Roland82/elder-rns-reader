package uk.co.integration

import fs2.Strategy
import org.http4s.client.blaze.PooledHttp1Client
import org.joda.time.DateTime
import org.scalatest.{FunSpec, Matchers}
import uk.co.rnsreader.announcements.businesswire.ProcessBusinessWire

class ProcessBusinessWireSpec extends FunSpec with Matchers {
  implicit val httpClient = PooledHttp1Client()
  implicit val strategy = Strategy.fromExecutionContext(scala.concurrent.ExecutionContext.Implicits.global)

  describe("Process Businesswire feed") {
//    val task = ProcessBusinessWire.apply("http://localhost:8080", new DateTime(2018, 1, 10, 0, 0))
//    val result = task.unsafeRun()
//
//
//    val (rns0, matches0) = result(0)
//    val (rns1, matches1) = result(1)
//    val (rns2, matches2) = result(2)
//
//    it("should detect 3 matches in the test content") {
//      result.size shouldBe 3
//    }
//
//    describe("Where it finds a match in the title of the XML feed item") {
//      it("Should report back that there was one regex match") {
//        matches0.size shouldEqual 1
//      }
//
//      it("The RNS item returned should have the title with the matched word in it") {
//        rns0.title shouldEqual "This is a title with the word blockchain in it"
//      }
//      it("The matched word should be in the regex match list returned") {
//        matches0.head.toString() shouldEqual "blockchain"
//      }
//    }
//
//    describe("Where it finds a match in the description of the XML feed item") {
//      it("Should report back that there was one regex match") {
//        matches1.size shouldEqual 1
//      }
//
//      it("The RNS item returned should have the description with the matched word in it") {
//        rns1.description shouldEqual "A description with the word blockchain in it"
//      }
//      it("The matched word should be in the regex match list returned") {
//        matches1.head.toString() shouldEqual "blockchain"
//      }
//    }
//
//    describe("When no matches are found in the XML feed it should check the annoucement and find matches there") {
//      it("Should report back that there was 7 regex matches") {
//        matches2.size shouldEqual 7
//      }
//
//      it("The announcment shouldnt have any matching words in the title or description") {
//        rns2.description shouldEqual "Announcement with match in content"
//        rns2.title shouldEqual "Announcement with match in content"
//      }
//      it("The matched word should be in the regex match list returned") {
//        matches2.head.toString() shouldEqual "Blockchain"
//      }
//    }
  }
}
