package no.netcompany.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, FunSuite}
import org.scalatest.junit.JUnitRunner

import no.netcompany.testdatagen.Printer
import no.netcompany.testdatagen.aggreg.SomeNulls
import no.netcompany.testdatagen.generators.{Booleans, Chars, Dates, Fixed, FromList, Ints, Strings}
import no.netcompany.testdatagen.generators.misc.MailAddresses
import no.netcompany.testdatagen.generators.norway.{NorskeNavn, Fnr}

@RunWith(classOf[JUnitRunner])
class ToJsonSpec extends FlatSpec {
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

  "The ToJson generator" should "require at least one input" in {
    intercept[IllegalArgumentException] { ToJson().get(1) }
  }

  it should "keep nulls when required" in {
    val res= ToJson(nulls=KeepNull).addQuoted("x", Fixed(null)).getStrings(1)(0)
    assert(res.matches("""(?s)^.*"x": null.*"""), res)
  }

  it should "skip nulls when required" in {
    val res= ToJson(nulls=SkipNull).addQuoted("x", Fixed(null)).getStrings(1)(0)
    assert(res.matches("(?s)^[^x].*$"), res)
  }

  it should "produce empty nulls when required" in {
    val res= ToJson(nulls=EmptyNull).addQuoted("x", Fixed(null)).getStrings(1)(0)
    assert(res.matches("""(?s).*"x": "".*"""), res)
    assert(!res.matches("(?s).*null.*"), "!" + res)
  }

  it should "produce expected contents" in {
    new Setup {
      val res=recordGen.get(30).mkString(" ")
      assert(res.matches("(?s)^.+\"data\":.+ssn.*active.*$"), res)
    }
  }

  it should "quote correctly" in {
    new Setup {
      val fnuttGen = Chars("\"")
      val res = ToJson().addQuoted("fnutt", fnuttGen).getStrings(1)(0)
      assert(res.matches("""(?s).*["]fnutt["]: ["][\\]["]["].*"""))
    }
  }

  it should "handle nesting" in {
    val intGen= Ints() from 1
    val gen1= ToJson(bare = true).add("int", intGen)
    val gen2= ToJson(header = "combined").add("name", NorskeNavn()).add("embedded", gen1)
    // TODO Assert
  }
}
