package no.mesan.testdatagen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.aggreg._
import no.mesan.testdatagen.generators.misc._
import no.mesan.testdatagen.generators.norway._
import no.mesan.testdatagen.generators._
import no.mesan.testdatagen.recordgen._
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

/** General tests for StreamGenerators. */
@RunWith(classOf[JUnitRunner])
class StreamGeneratorSpec extends FlatSpec  {

  case class GenSpec(extended: Boolean, gen: Generator[_], no: Int, count0: Boolean,
                     countDistinct: Boolean)
  def genSpec(gen: Generator[_], no: Int= 42, count0: Boolean= true, countDistinct: Boolean= true): GenSpec =
    GenSpec(extended=false, gen, no, count0, countDistinct)
  def extGenSpec(gen: ExtendedGenerator[_], no: Int= 42, count0: Boolean= true, countDistinct: Boolean= true): GenSpec =
    GenSpec(extended=true, gen, no, count0, countDistinct)


  def extendedGenerators: List[GenSpec]=
    List(
      extGenSpec(Strings().lengthBetween(from=100, to=144).distinct, 701),
      extGenSpec(Chars(), 91),
      extGenSpec(Dates().reversed(), 123),
      extGenSpec(Ints().distinct, 400),
      extGenSpec(Longs(from= -42).sequential, 317),
      extGenSpec(Doubles()),
      extGenSpec(Kommuner()),
      extGenSpec(Land(), 15),
      extGenSpec(Booleans(), 2),
      extGenSpec(Poststeder()),
      extGenSpec(Fixed(42), 1),
      extGenSpec(FromList("a", "foo", "test", "alpha", "beta", "gamma", "delta"), 6),
      extGenSpec(FromFile("ints.txt"), 7),
      extGenSpec(Orgnr()),
      extGenSpec(CarMakes(), 19)
    )
  def simpleGenerators: List[GenSpec]=
    List(
      genSpec(Fnr()),
      genSpec(CreditCards()),
      genSpec(Fibonaccis(), 11),
      genSpec(Guids()),
      genSpec(Markov.norwegian()),
      genSpec(Adresser()),
      genSpec(Kjennemerker()),
      genSpec(NorskeNavn()),
      genSpec(RareNavn()),
      genSpec(Names(3)),
      genSpec(Urls()),
      genSpec(FieldConcatenator().add(FromList(1, 2, 3)).add(Chars("abc")), 7),
      genSpec(SomeNulls(50, Ints()), 124, countDistinct=false),
      genSpec(TextWrapper(Strings().chars('A' to 'Z').sequential).surroundWith("* ", "\n"), 15),
      genSpec(TwoWithPredicate[Int](Ints() from -50 to 50 format "%08d", (t: (Int, Int))=> t._1 <= t._2)),
      genSpec(UniqueWithFallback(FromList((Ints() from -50 to 50 distinct) get 101), Ints() from 1000 to 5000)),
      genSpec(TwoFromFunction(FromList("abc", "" ,"d", "ef"), (v:String)=> v.length), countDistinct=false),
      genSpec(WeightedGenerator[Any]((1, FromList(1,2,3)), (2, Chars("1abc")), (1, Booleans().format("0", "1"))),
              3, countDistinct=false),
      genSpec(MailAddresses()),
      genSpec(ToCsv(withHeaders = false).add("x", Ints() from 1 sequential), count0=false),
      genSpec(ToSql("tab").add("col", Ints().from(1).sequential)),
      genSpec(ToWiki().add("x", Ints().from(1).sequential), count0=false, countDistinct=false),
      genSpec(ToFixedWidth(withHeaders = false).add("x", Strings().chars('A' to 'Z').length(4), 6)),
      genSpec(ToHtml("x").add("y", Ints().from(1).sequential), count0=false, countDistinct=false),
      genSpec(ToXmlAttributes("root", "data", SkipNull).add("y", Ints().from(1).sequential),
        count0=false, countDistinct=false),
      genSpec(ToXmlElements("root", "data", SkipNull).add("y", Ints().from(1).sequential),
        count0=false, countDistinct=false),
      genSpec(ToHtml("x").add("y", Ints().from(1).sequential), count0=false, countDistinct=false),
      genSpec(ToSql("x").add("y", Ints().from(1).sequential)),
      genSpec(ToJson().add("x", Ints().from(1).sequential), count0=false, countDistinct=false)
    )
  def generators: List[GenSpec]= extendedGenerators ++ simpleGenerators
  def generatorList: List[Generator[_]] = generators map { spec => spec.gen }
  def extendedGeneratorList: List[ExtendedGenerator[Any]] =
    extendedGenerators map { spec => spec.gen.asInstanceOf[ExtendedGenerator[Any]] }

  "A StreamGenerator" should "be lazy" in {
    generatorList map { g=>
      g.gen.take(Integer.MAX_VALUE)
      g.genStrings.take(Integer.MAX_VALUE)
    }
  }

  it should "generate unique values when asked to" in {
    generators map {
      case g: GenSpec if g.countDistinct =>
        val res= g.gen.distinct.genStrings.take(g.no).toSet
        assert(res.size===g.no, s"$g.gen $g.no: $res")
      case _ => true
    }
  }

  it should "return an empty list on get(0)" in {
    generators foreach { g=> if (g.count0) assert(List()===g.gen.get(0), g) }
    generators foreach { g=> if (g.count0) assert(List()===g.gen.getStrings(0)) }
  }

  it should "return the specified number of entries" in {
    generators foreach { g=> if (g.countDistinct) assert(g.gen.get(17).size===17, g) }
    generators foreach { g=> if (g.countDistinct) assert(g.gen.getStrings(19).size===19, g) }
  }

  it should "throw an exception if asked for a negative number of values" in {
    generatorList map { g =>
      intercept[IllegalArgumentException] { g get -1 }
      intercept[IllegalArgumentException] { g getStrings -1000 }
    }
  }

  "An Extended generator" should "return an empty list on get(0).sequential" in {
    extendedGeneratorList foreach { g=> assert(List()===g.sequential.get(0)) }
    extendedGeneratorList foreach { g=> assert(List()===g.sequential.getStrings(0)) }
  }

  it should "return the specified number of sequential entries" in {
    extendedGeneratorList foreach { g=> assert(g.sequential.get(17).size===17) }
    extendedGeneratorList foreach { g=> assert(g.sequential.getStrings(19).size===19) }
  }
}
