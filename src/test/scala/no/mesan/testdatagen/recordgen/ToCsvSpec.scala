package no.mesan.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.aggreg.SomeNulls
import no.mesan.testdatagen.generators.misc.MailAddresses
import no.mesan.testdatagen.generators.norway.Fnr
import no.mesan.testdatagen.generators.{Booleans, Chars, Dates, FromList, Ints, Strings}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class ToCsvSpec extends FlatSpec {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints() from 1 sequential
    val codeGen = Strings() chars('A' to 'Z') length 4
    val fnrGen = Fnr(FromList(dates) sequential)
    val boolGen = Booleans()
    val mailGen = SomeNulls(20, MailAddresses())
    val recordGen = ToCsv(true).
      add("id", idGen).
      add("userId", codeGen).
      add("ssn", fnrGen).
      add("mail", mailGen).
      add("active", boolGen)
  }

  "The ToCsv generator" should "demand at least one input generator" in {
    intercept[IllegalArgumentException] {
      ToCsv(true).get(1)
    }
  }

  it should "produce correct contents" in {
    new Setup {
      val res=recordGen.get(30).mkString(",")
      assert(res.matches("^([\"].*[\"],?)+$"),res)
    }
  }

  it should "quote special characters correctly" in {
    new Setup {
      var fnuttGen = Chars("\"")
      val res = ToCsv(false).add("fnutt", fnuttGen).getStrings(1)(0)
      val exp= "\"" + "\\" + "\"" + "\""
      assert(res===exp,res +"=" + exp)
    }
  }
}
