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
class WikiGeneratorSuite extends FunSuite {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val fnrGen = FnrGenerator(ListGenerator(dates).sequential)
    val boolGen = Booleans()
    val mailGen = SomeNulls(5, MailGenerator())
    val recordGen = WikiGenerator().
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
      WikiGenerator().get(1)
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
      assert(res.matches("(?s)\\s*\\|\\|\\s*id\\s*\\|\\|.*$"),res)
    }
  }

  test("quoting") {
    new Setup {
      var badGen = FixedGenerator("""
[|]
""")
      val res = WikiGenerator().add("", badGen).getStrings(1)(1)
      val exp= "\\s*\\|"
      // TODO assert(res.,res +"=" + exp)
    }
  }

}
