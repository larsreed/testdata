package no.mesan.testdatagen.aggreg

import scala.language.postfixOps

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

import no.mesan.testdatagen.generators.{Ints, Chars, FromList}
import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class SequenceOfSuite extends FunSuite with Printer {

  trait Setup {
    val noOfGens= 3
    val xgen= SequenceOf.strings(FromList(1, 2, 3) sequential,
                                 Chars("abc") sequential,
                                 Ints() from 4 sequential)
  }

  print(false) {
    new Setup {
      println(xgen.get(120))
      println(xgen.getStrings(120))
    }
  }

  test("count") {
    new Setup {
      private val size = xgen.get(30).size
      assert(size>=28 && size<=32)
    }
  }

  test("plain contents") {
    new Setup {
      val exp= List("1", "2", "3", "a", "b", "c", "4", "5", "6")
      val res1= xgen.get(9)
      val res2= xgen.getStrings(9)
      assert(exp==res1, "1" + res1)
      assert(exp==res2, "2" + res2)
    }
  }

  test("weighted relative contents") {
    val xgen= SequenceOf[Any]().addWeighted((5, FromList(1, 2, 3) sequential),
                                          (3, Chars("abc") sequential),
                                          (2, Ints() from 4 sequential))
      val exp= List("1", "2", "3", "1", "2", "3", "a", "b", "c", "4", "5")
      val res= xgen.getStrings(12)
      assert(exp===res)
  }

  test("weighted absolute contents") {
    val xgen= SequenceOf[Any]().makeAbsolute().
        addWeighted((5, FromList(1, 2, 3) sequential),
                  (3, Chars("abc") sequential),
                  (2, Ints() from 4 sequential))
      val exp= List("1", "2", "3", "1", "2", "a", "b", "c", "4", "5")
      val res= xgen.getStrings(1)
      assert(exp===res)
  }
}
