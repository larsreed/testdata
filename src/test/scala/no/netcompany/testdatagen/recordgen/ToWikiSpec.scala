package no.netcompany.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.aggreg.SomeNulls
import no.netcompany.testdatagen.generators.misc.MailAddresses
import no.netcompany.testdatagen.generators.norway.Fnr
import no.netcompany.testdatagen.generators.{Booleans, Dates, Fixed, FromList, Ints, Strings}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ToWikiSpec extends FlatSpec {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val fnrGen = Fnr(FromList(dates).sequential)
    val boolGen = Booleans()
    val mailGen = SomeNulls(20, MailAddresses())
    val recordGen = ToWiki().
      add("id", idGen).
      add("userId", codeGen).
      add("ssn", fnrGen).
      add("mail", mailGen).
      add("active", boolGen)
  }
  "The ToWiki generator" should "require at least 1 input" in {
    intercept[IllegalArgumentException] { ToWiki().get(1) }
  }

  it should "produce expected contents" in {
    new Setup {
      val res=recordGen.get(3).mkString("\n")
      assert(res.matches("(?s)\\s*\\|\\|\\s*id\\s*\\|\\|.*$"),res)
    }
  }

  it should "quote correctly" in {
    new Setup {
      val badGen = Fixed("\no[|]")
      val res = ToWiki().add("", badGen).getStrings(1)(0)
      val exp= """|  \\o\[\|\] |"""
      assert(res===exp)
    }
  }
}
