package no.mesan.testdatagen.recordgen

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer
import no.mesan.testdatagen.aggreg.SomeNulls
import no.mesan.testdatagen.generators.{Booleans, Chars, Dates, Fixed, FromList, Ints, Strings}
import no.mesan.testdatagen.generators.misc.MailAddresses
import no.mesan.testdatagen.generators.norway.Fnr

@RunWith(classOf[JUnitRunner])
class ToJsonSuite extends FunSuite with Printer {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val fnrGen = Fnr(FromList(dates).sequential)
    val boolGen = Booleans()
    val mailGen = SomeNulls(50, MailAddresses())
    val recordGen = ToJson("data", nulls=KeepNull).
      add("id", idGen).
      addQuoted("userId", codeGen).
      add("ssn", fnrGen).
      addQuoted("mail", mailGen).
      add("active", boolGen)
  }

  print(false) {
    new Setup {
      val recordGenInner= ToJson(nulls=SkipNull, bare=true).
        addQuoted("userId", codeGen).
        addQuoted("ssn", fnrGen).
        addQuoted("mail", mailGen)
      val recordGenOuter= ToJson("data", nulls=KeepNull).
        add("id", idGen).
        add("userData", recordGenInner).
        add("active", boolGen)
      println(recordGenOuter.get(12).mkString("\n"))
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
      ToJson().get(1)
    }
  }

  test("count") {
    new Setup {
      assert(recordGen.get(30).size === 30 + 2)
      assert(ToJson().add("id", idGen).get(20).size === 20 +2)
    }
  }

  test("keeping null") {
    val res= ToJson(nulls=KeepNull).addQuoted("x", Fixed(null)).getStrings(1)(1)
    assert(res.matches("""(?s)^.*"x": null.*"""), res)
  }

  test("skipping null") {
    val res= ToJson(nulls=SkipNull).addQuoted("x", Fixed(null)).getStrings(1)(1)
    assert(res.matches("(?s)^[^x].*$"), res)
  }

  test("empty null") {
    val res= ToJson(nulls=EmptyNull).addQuoted("x", Fixed(null)).getStrings(1)(1)
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
      val res = ToJson().addQuoted("fnutt", fnuttGen).getStrings(1)(1)
      assert(res.matches("""(?s).*["]fnutt["]: ["][\\]["]["].*"""))
    }
  }

}
