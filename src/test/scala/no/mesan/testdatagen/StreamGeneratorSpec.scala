package no.mesan.testdatagen

import no.mesan.testdatagen.generators.misc.{CreditCards, CarMakes}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import scala.language.postfixOps
import no.mesan.testdatagen.generators.{Fixed, Booleans, FromList, Doubles, Chars, Longs, Ints, Dates, Strings}
import no.mesan.testdatagen.generators.norway._

/** General tests for StreamGenerators. */
@RunWith(classOf[JUnitRunner])
class StreamGeneratorSpec extends FlatSpec  {

  // (generator, no.of. unique values)

  def extendedGenerators: List[(StreamGenerator[_] with ExtendedGenerator[_], Int)]=
    List(
      (Strings().lengthBetween(from=100, to=144).distinct, 701),
       (Chars(), 91),
       (Dates().reversed(), 123),
       (Ints().distinct, 400),
       (Longs(from= -42).sequential, 317),
       (Doubles(), 39),
       (Kommuner(), 12),
       (Land(), 15),
       (Booleans(), 2),
       (Poststeder(), 19),
       (Fixed(42), 1),
       (FromList("a", "foo", "test", "alpha", "beta", "gamma", "delta"), 6),
       (Orgnr(), 97),
       (CarMakes(), 19)
    )
  def generators: List[(StreamGenerator[_], Int)]=
    extendedGenerators ++
      List(
        (Fnr(), 35),
        (CreditCards(), 72),
        (CreditCards.visas, 3)
      )
  def generatorList= generators map { tuple => tuple._1 }
  def extendedGeneratorList= extendedGenerators map { tuple => tuple._1 }

  "A StreamGenerator" should "be lazy" in {
    generatorList map { g=>
      g.gen.take(Integer.MAX_VALUE)
      g.genStrings.take(Integer.MAX_VALUE)
    }
  }

  it should "generate unique values when asked to" in {
    generators map { g=>
      val res= g._1.distinct.genStrings.take(g._2).toSet
      assert(res.size===g._2, g._1.toString)
    }
  }

  it should "return an empty list on get(0)" in {
    generatorList map { g=> assert(List()===g.get(0)) }
    generatorList map { g=> assert(List()===g.getStrings(0)) }
  }

  it should "return the specified number of entries" in {
    generatorList map { g=> assert(g.get(17).size===17) }
    generatorList map { g=> assert(g.getStrings(19).size===19) }
  }

  it should "throw an exception if asked for a negative number of values" in {
    generatorList map { g =>
      intercept[IllegalArgumentException] { g get -1 }
      intercept[IllegalArgumentException] { g getStrings -1000 }
    }
  }

  "An Extended generator" should "return an empty list on get(0).sequential" in {
    extendedGeneratorList map { g=> assert(List()===g.sequential.get(0)) }
    extendedGeneratorList map { g=> assert(List()===g.sequential.getStrings(0)) }
  }

  it should "return the specified number of sequential entries" in {
    extendedGeneratorList map { g=> assert(g.sequential.get(17).size===17) }
    extendedGeneratorList map { g=> assert(g.sequential.getStrings(19).size===19) }
  }
}
