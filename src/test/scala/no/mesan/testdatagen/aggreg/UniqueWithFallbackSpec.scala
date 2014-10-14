package no.mesan.testdatagen.aggreg

import scala.language.postfixOps

import no.mesan.testdatagen.generators.{FromList, Ints}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class UniqueWithFallbackSpec extends FlatSpec {

  trait Setup {
    val ints= (Ints() from -50 to 50 distinct) get 101
    val mainGen= FromList(ints)
    val altGen= Ints() from 1000 to 5000
    val xgen= UniqueWithFallback(mainGen, altGen)
  }

  "UniqueWithFallbackSpec" should "produce expected contents" in {
    new Setup {
      val res= xgen.get(1000)
      for (i <- -50 to 50) assert(res contains i, i)
      val xset= res.toSet
      assert(xset.size===1000)
    }
  }
}
