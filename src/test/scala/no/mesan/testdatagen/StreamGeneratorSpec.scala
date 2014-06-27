package no.mesan.testdatagen

import org.scalatest.FlatSpec
import scala.language.postfixOps
import no.mesan.testdatagen.generators.{Fixed, Booleans, FromList, Doubles, Chars, Longs, Ints, Dates, Strings}
import no.mesan.testdatagen.generators.norway.{Poststeder, Land, Kommuner}

/** General tests for StreamGenerators. */
class StreamGeneratorSpec extends FlatSpec  {

  // (generator, no.of. unique values)
  val generators: List[(StreamGenerator[_], Int)]=
    List((Strings().lengthBetween(from=100, to=144).distinct, 701),
         (Chars(), 91),
         (Dates().reversed(), 123),
         (Ints().distinct, 400),
         (Longs(from= -42).sequential, 317),
         (Doubles(), 39),
         (FromList("a", "foo", "test", "alpha", "beta", "gamma", "delta"), 7),
         (Booleans(), 2),
         (Fixed(42), 1),
         (Kommuner(), 12),
         (Land(), 15),
         (Poststeder(), 19)
    )
  def generatorList= generators map { tuple => tuple._1 }

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

  it should "throw an exception if asked for a negative number of values" in {
    generatorList map { g =>
      intercept[IllegalArgumentException] { g get -1 }
      intercept[IllegalArgumentException] { g getStrings -1000 }
    }
  }
}
