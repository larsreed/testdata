package net.kalars.testgen.recordgen

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

import net.kalars.testgen.aggreg.SomeNulls
import net.kalars.testgen.generators.{Booleans, Chars, Dates, Ints, ListGenerator, Strings}
import net.kalars.testgen.generators.misc.MailGenerator
import net.kalars.testgen.generators.norway.FnrGenerator

@RunWith(classOf[JUnitRunner])
class CsvGeneratorSuite extends FunSuite {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val fnrGen = FnrGenerator(ListGenerator(dates).sequential)
    val boolGen = Booleans()
    val mailGen = SomeNulls(5, MailGenerator())
    val recordGen = CsvGenerator(true).
      add("id", idGen).
      add("userId", codeGen).
      add("ssn", fnrGen).
      add("mail", mailGen).
      add("active", boolGen)
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

  test("needs one generator") {
    intercept[IllegalArgumentException] {
      CsvGenerator(true).get(1)
    }
  }

  test("count") {
    new Setup {
      assert(recordGen.get(30).size === 30 + 1)
      assert(CsvGenerator(false).add("id", idGen).get(20).size === 20)
    }
  }

  test("contents") {
    new Setup {
      val res=recordGen.get(30).mkString(",")
      assert(res.matches("^([\"].*[\"],?)+$"),res)
    }
  }

  test("quoting") {
    new Setup {
      var fnuttGen = Chars("\"")
      val res = CsvGenerator(false).add("fnutt", fnuttGen).getStrings(1)(0)
      val exp= "\"" + "\\" + "\"" + "\""
      assert(res===exp,res +"=" + exp)
    }
  }

}
