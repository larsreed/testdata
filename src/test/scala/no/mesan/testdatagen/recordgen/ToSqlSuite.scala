package no.mesan.testdatagen.recordgen

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer
import no.mesan.testdatagen.aggreg.SomeNulls
import no.mesan.testdatagen.generators.{Booleans, Chars, Dates, Doubles, FromList, Ints, Strings}
import no.mesan.testdatagen.generators.misc.{MailAddresses, Names, Urls}
import no.mesan.testdatagen.generators.norway.{Fnr, Kjennemerker}

@RunWith(classOf[JUnitRunner])
class ToSqlSuite extends FunSuite with Printer {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val nameGen = Names(2)
    val bornGen = SomeNulls(25, FromList(dates).sequential.formatWith(Dates.dateFormatter("yyyy-MM-dd")))
    val fnrGen = Fnr(FromList(dates).sequential)
    val boolGen = Booleans()
    val scoreGen = SomeNulls(50, Doubles().from(0).to(10000))
    val urlGen = SomeNulls(33, Urls())
    val mailGen = SomeNulls(20, MailAddresses())
    var kjmGen = SomeNulls(12, Kjennemerker())
    val recordGen = ToSql("User").
      add("id", idGen).
      addQuoted("userId", codeGen).
      addQuoted("ssn", fnrGen).
      add("born", bornGen).
      addQuoted("name", nameGen).
      addQuoted("mail", mailGen).
      addQuoted("homePage", urlGen).
      add("active", boolGen).
      add("score", scoreGen).
      addQuoted("car", kjmGen)
  }

  print(false) {
    new Setup {
      println(recordGen.get(4).mkString("\n"))
    }
  }

  test("needs one generator") {
    intercept[IllegalArgumentException] {
      ToSql("tab", "go").get(1)
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

  test("count") {
    new Setup {
      assert(recordGen.get(30).size === 30)
    }
  }

  test("contents") {
    new Setup {
      val res=recordGen.get(30).mkString("\n")
      assert(res.matches("(?s)^(insert into .* values .*)+"))
    }
  }

  test("quoting") {
    new Setup {
      var fnuttGen = Chars("'")
      val res = ToSql("tbl", "").addQuoted("fnutt", fnuttGen).getStrings(1)(0)
      val exp= "insert into tbl (fnutt) values ('''')"
      assert(res===exp,res +"=" + exp)
    }
  }
}
