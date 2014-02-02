package no.mesan.testdatagen.aggreg

import scala.language.postfixOps

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

import no.mesan.testdatagen.generators.{Ints, Booleans, Chars, FromList}
import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class SequenceOfSuite extends FunSuite with Printer {

  trait Setup {
    val noOfGens= 3
    val xgen= SequenceOf().add(FromList(1, 2, 3) sequential).
                           add(Chars("abc") sequential).
                           add(Ints() from 4 sequential)
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
      assert(xgen.get(30).size === noOfGens*30)
    }
  }

  test("contents") {
    new Setup {
      val exp= List("1", "2", "3", "a", "b", "c", "4", "5", "6")
      val res1= xgen.get(3)
      val res2= xgen.getStrings(3)
      assert(exp==res1)
      assert(exp==res2)
    }
  }
}
