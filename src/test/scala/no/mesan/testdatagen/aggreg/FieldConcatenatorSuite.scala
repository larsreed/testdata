package no.mesan.testdatagen.aggreg

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.scalatest.FunSuite
import no.mesan.testdatagen.generators.{Booleans, Chars, FromList}

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class FieldConcatenatorSuite extends FunSuite with Printer {

  trait Setup {
    val xgen= FieldConcatenator().add(FromList(1)).
                                  add(Chars("a")).
                                  add(Booleans())
  }

  print(false) {
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

  private def isMatch(s: String): Boolean = {
    (s equals "1atrue") || (s equals "1afalse")
  }


  test("contents") {
    new Setup {
      val res= xgen.get(1000)
      res foreach { s => assert(isMatch(s)) }
    }
  }

  test("formatted contents") {
    new Setup {
      val res= xgen.getStrings(1000)
      res foreach { s => assert(isMatch(s)) }
    }
  }

}
