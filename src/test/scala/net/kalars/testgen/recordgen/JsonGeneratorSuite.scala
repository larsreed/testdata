package net.kalars.testgen.recordgen

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import net.kalars.testgen.FunSuite
import net.kalars.testgen.aggreg.SomeNulls
import net.kalars.testgen.generators.{Booleans, Chars, Dates, Fixed, FromList, Ints, Strings}
import net.kalars.testgen.generators.misc.MailAddresses
import net.kalars.testgen.generators.norway.Fnr

@RunWith(classOf[JUnitRunner])
class JsonGeneratorSuite extends FunSuite {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val fnrGen = Fnr(FromList(dates).sequential)
    val boolGen = Booleans()
    val mailGen = SomeNulls(2, MailAddresses())
    val recordGen = JsonGenerator("data", nulls=KeepNull).
      add("id", idGen).
      addQuoted("userId", codeGen).
      add("ssn", fnrGen).
      addQuoted("mail", mailGen).
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
      JsonGenerator().get(1)
    }
  }

  test("count") {
    new Setup {
      assert(recordGen.get(30).size === 30 + 2)
      assert(JsonGenerator().add("id", idGen).get(20).size === 20 +2)
    }
  }

  test("keeping null") {
    val res= JsonGenerator(nulls=KeepNull).addQuoted("x", Fixed(null)).getStrings(1)(1)
    assert(res.matches("""(?s)^.*"x": null.*"""), res)
  }

  test("skipping null") {
    val res= JsonGenerator(nulls=SkipNull).addQuoted("x", Fixed(null)).getStrings(1)(1)
    assert(res.matches("(?s)^[^x].*$"), res)
  }

  test("empty null") {
    val res= JsonGenerator(nulls=EmptyNull).addQuoted("x", Fixed(null)).getStrings(1)(1)
    assert(res.matches("""(?s).*"x": "".*"""), res)
    assert(!res.matches("(?s).*null.*"), "!" + res)
  }

  test("contents") {
    new Setup {
      val res=recordGen.get(30).mkString(" ")
      assert(res.matches("(?s)^.+\"data\":.+ssn.*active.*$"), res)
    }
  }

  test("quoting") {
    new Setup {
      var fnuttGen = Chars("\"")
      val res = JsonGenerator().addQuoted("fnutt", fnuttGen).getStrings(1)(1)
      assert(res.matches("""(?s).*["]fnutt["]: ["][\\]["]["].*"""))
    }
  }

}
