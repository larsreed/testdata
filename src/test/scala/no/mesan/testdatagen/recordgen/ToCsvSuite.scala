package no.mesan.testdatagen.recordgen

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.scalatest.FunSuite
import no.mesan.testdatagen.aggreg.SomeNulls
import no.mesan.testdatagen.generators.{Booleans, Chars, Dates, FromList, Ints, Strings}
import no.mesan.testdatagen.generators.misc.MailAddresses
import no.mesan.testdatagen.generators.norway.Fnr

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class ToCsvSuite extends FunSuite with Printer {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val fnrGen = Fnr(FromList(dates).sequential)
    val boolGen = Booleans()
    val mailGen = SomeNulls(5, MailAddresses())
    val recordGen = ToCsv(true).
      add("id", idGen).
      add("userId", codeGen).
      add("ssn", fnrGen).
      add("mail", mailGen).
      add("active", boolGen)
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

  test("needs one generator") {
    intercept[IllegalArgumentException] {
      ToCsv(true).get(1)
    }
  }

  test("count") {
    new Setup {
      assert(recordGen.get(30).size === 30 + 1)
      assert(ToCsv(false).add("id", idGen).get(20).size === 20)
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
      val res = ToCsv(false).add("fnutt", fnuttGen).getStrings(1)(0)
      val exp= "\"" + "\\" + "\"" + "\""
      assert(res===exp,res +"=" + exp)
    }
  }

}
