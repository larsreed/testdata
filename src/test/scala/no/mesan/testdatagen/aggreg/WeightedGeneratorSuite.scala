package no.mesan.testdatagen.aggreg

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.FunSuite
import no.mesan.testdatagen.generators.{Booleans, Chars, FromList}

@RunWith(classOf[JUnitRunner])
class WeightedGeneratorSuite extends FunSuite {

  trait Setup {
    val xgen= WeightedGenerator().add(1, FromList(1,2,3)).
                                  add(2, Chars("1abc")).
                                  add(1, Booleans().format("0", "1")).
                                  filter(x=> !(x.toString.matches("1")))
  }

  print {
    new Setup {
      println(xgen.get(120))
      println(xgen.getStrings(120))
    }
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      new Setup {
        xgen.get(-1)
      }
    }
    intercept[IllegalArgumentException] {
      new Setup {
        xgen.getStrings(-1)
      }
    }
  }

  test("count") {
    new Setup {
      assert(xgen.get(30).size === 30)
    }
  }

  test("contents") {
    new Setup {
      val res= xgen.get(1000)
      assert(res contains 2)  // could fail randomly..
      assert(res contains 'c')  // could fail randomly..
      assert(res contains true)  // could fail randomly..
    }
  }

  test("formatted contents") {
    new Setup {
      val res= xgen.getStrings(1000)
      assert(res contains "2")  // could fail randomly..
      assert(res contains "c")  // could fail randomly..
      assert(res contains "0")  // could fail randomly..
    }
  }

}
