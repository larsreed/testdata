package no.mesan.testdatagen.aggreg

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.FunSuite
import no.mesan.testdatagen.generators.{Fixed, Strings}

@RunWith(classOf[JUnitRunner])
class TextWrapperSuite extends FunSuite {

  trait Setup {
    val xgen= TextWrapper(Strings().chars('A' to 'Z').sequential).surroundWith("* ", "\n")
    val fix= Fixed("ABCDEFG")
  }

  print {
    new Setup {
      println(xgen.get(120))
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
        assert(xgen.get(30).size===30)
      }
  }

  test("right substring") {
    new Setup {
      assert(TextWrapper(fix).substring(3).get(1)(0)==="DEFG")
    }
  }

  test("middle substring") {
    new Setup {
      assert(TextWrapper(fix).substring(2,4).get(1)(0)==="CD")
    }
  }

  test("transform") {
    new Setup {
      assert(TextWrapper(fix).transform(_.toLowerCase).get(1)(0)==="abcdefg")
    }
  }

  test("order of operations") {
    new Setup {
      assert(TextWrapper(fix).transform(_.toLowerCase).
          substring(1,	4).
          surroundWith("(", ")").
          get(1)(0)==="(bcd)")
      assert(TextWrapper(fix).transform(_.toLowerCase).
          surroundWith("(", ")").
          substring(1,	4).
          get(1)(0)==="abc")
    }
  }

  test("upper/lower") {
    new Setup {
      assert(TextWrapper(fix).toLower.
          get(1)(0)==="abcdefg")
      assert(TextWrapper(fix).toLower.toUpper.
          get(1)(0)==="ABCDEFG")
    }
  }

  test("prefix and suffix") {
    val v = TextWrapper(Strings().chars("ABC").
        sequential.
        formatWith(s => s.toLowerCase)).
      surroundWith("<", ">")

    val res = v.getStrings(12)
    assert(res.forall(s => s.matches("<[a-c]>")), res)
  }

}
