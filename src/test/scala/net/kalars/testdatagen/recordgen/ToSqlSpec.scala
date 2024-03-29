package net.kalars.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.aggreg.SomeNulls
import net.kalars.testdatagen.generators.misc.{MailAddresses, Names, Urls}
import net.kalars.testdatagen.generators.norway.{Fnr, Kjennemerker}
import net.kalars.testdatagen.generators.{Booleans, Chars, Dates, Doubles, FromList, Ints, Strings}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ToSqlSpec extends FlatSpec {
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
    val kjmGen = SomeNulls(12, Kjennemerker())
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

  "The ToSql generator" should "require at least 1 input" in {
    intercept[IllegalArgumentException] { ToSql("tab", "go").get(1) }
  }

  it should "produce expected contents" in {
    new Setup {
      val res=recordGen.get(30).mkString("\n")
      assert(res.matches("(?s)^(insert into .* values .*)+"))
    }
  }

  it should "quote correctly" in {
    new Setup {
      val fnuttGen = Chars("'")
      val res = ToSql("tbl", "").addQuoted("fnutt", fnuttGen).getStrings(1)(0)
      val exp= "insert into tbl (fnutt) values ('''')"
      assert(res===exp,res +"=" + exp)
    }
  }
}