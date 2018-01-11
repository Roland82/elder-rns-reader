package uk.co.integration

import fs2.Strategy
import org.http4s.client.blaze.PooledHttp1Client
import org.joda.time.DateTime
import org.scalatest.{FunSpec, Matchers}
import uk.co.rnsreader.announcements.rns.ProcessRns

class RnsProcessorSpec extends FunSpec  with Matchers  {
  implicit val httpClient = PooledHttp1Client()
  implicit val strategy = Strategy.fromExecutionContext(scala.concurrent.ExecutionContext.Implicits.global)

  describe("Rns Processor") {
    it("should handle partial failures and still report back") {
      val future = ProcessRns.process("http://localhost:8080")(new DateTime(2000, 1, 1, 0, 0))
      val result = future.unsafeRun()
      result.size shouldBe 2
    }
  }
}
