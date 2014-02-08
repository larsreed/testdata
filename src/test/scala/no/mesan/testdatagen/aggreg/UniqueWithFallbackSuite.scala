package no.mesan.testdatagen.aggreg

import scala.language.postfixOps

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

import no.mesan.testdatagen.generators.{FromList, Ints}
import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class UniqueWithFallbackSuite extends FunSuite with Printer {

  trait Setup {
    val ints= (Ints() from -50 to 50 unique) get(101)
    val mainGen= FromList(ints)
    val altGen= Ints() from 1000 to 5000
    val xgen= UniqueWithFallback(mainGen, altGen)
  }

  print(false) {
    new Setup {
      println(xgen.get(120))
      println(xgen.getStrings(120))
    }
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      new Setup { xgen.get(-1) }
    }
    intercept[IllegalArgumentException] {
      new Setup { xgen.getStrings(-1) }
    }
  }

  test("count") {
    new Setup { assert(xgen.get(30).size === 30) }
  }

  test("contents") {
    new Setup {
      val res= xgen.get(1000)
      for (i <- -50 to 50) assert(res contains i, i)
      val xset= res.toSet
      assert(xset.size===1000)
    }
  }
}
