package no.netcompany.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.aggreg.SomeNulls
import no.netcompany.testdatagen.generators.misc.{MailAddresses, Urls}
import no.netcompany.testdatagen.generators.norway.{Fnr, Kjennemerker, NorskeNavn}
import no.netcompany.testdatagen.generators.{Booleans, Dates, Doubles, FromList, Ints, Strings}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ToHtmlSpec extends FlatSpec {
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
    val kjmGen = SomeNulls(15, Kjennemerker())
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

  "The ToHtml generator" should "demand at least one generator" in {
    intercept[IllegalArgumentException] { ToHtml("tab").get(1) }
  }

  ignore should "produce correct contents" in {
    new Setup {
      val res=recordGen.get(30).mkString("\n")
      assert(res.matches("(?s)..."))
    }
  }

  it should "quote special characters correctly" in {
    new Setup {
      val tullGen = Strings().chars("<&>").sequential
      val res = ToHtml().add("tull", tullGen).getStrings(3).mkString(" ")
      val exp= "c"
      assert(res.contains("&lt;"), res)
      assert(res.contains("&gt;"), res)
      assert(res.contains("&amp;"), res)
    }
  }
}
