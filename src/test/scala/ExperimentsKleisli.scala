import scala.concurrent.Future
import scalaz.Kleisli
import scalaz.Kleisli._
import scalaz.std.double
import scalaz.std.option._

object ExperimentsKleisli extends App {

  def parseInt(s: String): Option[Int] =
    try {
      Some(s.toInt)
    } catch {
      case e: Exception => None
    }

  def double(s: Int): Option[Int] = Some(s * 2)

  def convertToFuture(i: Option[Int]): Future[Int] = i match {
    case Some(e) => Future.successful(e)
    case None => Future.failed(new Exception())
  }


  val parseIntKleisli = Kleisli(parseInt)
  println(parseIntKleisli.apply("1"))


  // If your function is already inside a kleisli then you can do the small arrow
  val doubledResult1 = parseIntKleisli >=> Kleisli(double)
  // Or this is the same thing
  val doubledResult2 = parseIntKleisli andThen kleisli(double)

  // If your function is not then use double arrow to lift it into the kleisli
  val doubledResult3 = parseIntKleisli >==> double
  // Or this is the same thing
  parseIntKleisli.andThenK(double)

  println("Doubled Result 1 " + doubledResult1("1"))
  println("Doubled Result 2 " + doubledResult3("1"))

  // Map will map over the the monad inside the kleisli
  val mappedResult = parseIntKleisli map { _ * 3 }
  println("Mapped Result " + mappedResult("1"))


  // MapT
  val mapTResult = parseIntKleisli mapT convertToFuture
  println("MapT Result. Valid int " + mapTResult("1"))
  println("MapT Result. Invalid int value should be a failed future  " + mapTResult("a"))


  // TODO: I have no idea what this does
  val diMapResult = parseIntKleisli.dimap[BigDecimal, BigInt](e => e.toString, a => BigInt(a))




}
