package net.kalars.testgen.recordgen

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner
import net.kalars.testgen.aggreg.SomeNulls
import net.kalars.testgen.generators.{Booleans, Chars, Dates, Ints, ListGenerator, Strings}
import net.kalars.testgen.generators.misc.MailGenerator
import net.kalars.testgen.generators.norway.FnrGenerator
import net.kalars.testgen.generators.FixedGenerator

@RunWith(classOf[JUnitRunner])
class FixedWidthGeneratorSuite extends FunSuite {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).format("%4d").sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val fnrGen = FnrGenerator(ListGenerator(dates).sequential)
    val recordGen = FixedWidthGenerator().
      add("id", idGen, 5).
      add("userId", codeGen, 3).
      add("ssn", fnrGen,11)
  }

  print {
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
      FixedWidthGenerator().add("bah", FixedGenerator(12), 0).get(1)
    }
  }

  test("needs one generator") {
    intercept[IllegalArgumentException] {
      FixedWidthGenerator().get(1)
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
      assert(FixedWidthGenerator(false).add("aha", FixedGenerator("aha"), 4).get(1)(0)==="aha ")
    }
  }
}
