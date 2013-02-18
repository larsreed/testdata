package no.mesan.testdatagen.recordgen

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer
import no.mesan.testdatagen.aggreg.SomeNulls
import no.mesan.testdatagen.generators.{Booleans, Dates, Doubles, FromList, Ints, Strings}
import no.mesan.testdatagen.generators.misc.{MailAddresses, Urls}
import no.mesan.testdatagen.generators.norway.{Fnr, Kjennemerker, NorskeNavn}

@RunWith(classOf[JUnitRunner])
class ToHtmlSuite extends FunSuite with Printer {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val nameGen = NorskeNavn()
    val bornGen = SomeNulls(25, FromList(dates).sequential.formatWith(Dates.dateFormatter("yyyy-MM-dd")))
    val fnrGen = Fnr(FromList(dates).sequential)
    val boolGen = Booleans()
    val scoreGen = SomeNulls(50, Doubles().from(0).to(10000))
    val urlGen = SomeNulls(33, Urls())
    val mailGen = SomeNulls(20, MailAddresses())
    var kjmGen = SomeNulls(15, Kjennemerker())
    val recordGen = ToHtml("Brukere").
      add("id", idGen).
      add("userId", codeGen).
      add("ssn", fnrGen).
      add("born", bornGen).
      add("name", nameGen).
      add("mail", mailGen).
      add("homePage", urlGen).
      add("active", boolGen).
      add("score", scoreGen).
      add("car", kjmGen)
  }

  print(false) {
    new Setup {
      println(recordGen.getStrings(12))
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
      assert(recordGen.get(30).size === 1)
    }
  }

  ignore("contents") {
    new Setup {
      val res=recordGen.get(30).mkString("\n")
      assert(res.matches("(?s)..."))
    }
  }

  test("quoting") {
    new Setup {
      var tullGen = Strings().chars("<&>").sequential
      val res = ToHtml().add("tull", tullGen).getStrings(3).mkString(" ")
      val exp= "c"
      assert(res.contains("&lt;"))
      assert(res.contains("&gt;"))
      assert(res.contains("&amp;"))
    }
  }
}
