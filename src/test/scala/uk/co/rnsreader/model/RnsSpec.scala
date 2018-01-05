package uk.co.rnsreader.model

import org.scalatest.{FunSpec, Matchers}
import uk.co.rnsreader.pages.Rns._

import scalaz.Maybe
import scalaz.Maybe.Just


class RnsSpec extends FunSpec with Matchers {
  describe("Company Details parser") {
    List(
      ("Clear Leisure Plc (CLP)", ("Clear Leisure Plc", Just("CLP"))),
      ("Clear Leisure Plc", ("Clear Leisure Plc", Maybe.empty))

    ).foreach(e => {
      it(s"Should return  ${e._1} as ${e._2}") {
        parseCompanyDetails(e._1) shouldEqual e._2
      }
    })
  }
}
