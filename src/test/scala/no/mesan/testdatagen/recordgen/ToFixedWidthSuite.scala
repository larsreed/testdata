package no.mesan.testdatagen.recordgen

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.scalatest.FunSuite
import no.mesan.testdatagen.generators.{Dates, Fixed, FromList, Ints, Strings}
import no.mesan.testdatagen.generators.norway.Fnr

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class ToFixedWidthSuite extends FunSuite with Printer {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).format("%4d").sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val fnrGen = Fnr(FromList(dates).sequential)
    val recordGen = ToFixedWidth().
      add("id", idGen, 5).
      add("userId", codeGen, 3).
      add("ssn", fnrGen,11)
  }

  print(false) {
    new Setup {
      println(recordGen.get(120).mkString("\n"))
    }
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      new Setup {
        recordGen.get(-1)
      }
    }
    intercept[IllegalArgumentException] {
      new Setup {
        recordGen.getStrings(-1)
      }
    }
  }

  test("0-width is meaningsless") {
    intercept[IllegalArgumentException] {
      ToFixedWidth().add("bah", Fixed(12), 0).get(1)
    }
  }

  test("needs one generator") {
    intercept[IllegalArgumentException] {
      ToFixedWidth().get(1)
    }
  }

  test("count") {
    new Setup {
      assert(recordGen.get(30).size === 30 + 1)
    }
  }

  test("contents") {
    new Setup {
      val res=recordGen.get(3).mkString("\n")
      assert(res.matches("(?s)\\s*id.*$"),res)
      assert(ToFixedWidth(false).add("aha", Fixed("aha"), 4).get(1)(0)==="aha ")
    }
  }
}
