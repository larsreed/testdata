package net.kalars.testgen.recordgen

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner
import net.kalars.testgen.aggreg.SomeNulls
import net.kalars.testgen.generators.{Booleans, Chars, Dates, Doubles, Ints, FromList, Strings}
import net.kalars.testgen.generators.misc.{MailAddresses, Urls}
import net.kalars.testgen.generators.norway.{Fnr, Kjennemerker, NorskeNavn}

@RunWith(classOf[JUnitRunner])
class HtmlGeneratorSuite extends FunSuite {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val nameGen = NorskeNavnGenerator()
    val bornGen = SomeNulls(4, ListGenerator(dates).sequential.formatWith(Dates.dateFormatter("yyyy-MM-dd")))
    val fnrGen = FnrGenerator(ListGenerator(dates).sequential)
    val boolGen = Booleans()
    val scoreGen = SomeNulls(2, Doubles().from(0).to(10000))
    val urlGen = SomeNulls(3, UrlGenerator())
    val mailGen = SomeNulls(5, MailGenerator())
    var kjmGen = SomeNulls(6, KjennemerkeGenerator())
    val recordGen = HtmlGenerator("Brukere").
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

  print {
    new Setup {
      println(recordGen.getStrings(12))
    }
  }

  test("needs one generator") {
    intercept[IllegalArgumentException] {
      SqlGenerator("tab", "go").get(1)
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
      val res = HtmlGenerator().add("tull", tullGen).getStrings(3).mkString(" ")
      val exp= "c"
      assert(res.contains("&lt;"))
      assert(res.contains("&gt;"))
      assert(res.contains("&amp;"))
    }
  }
}
