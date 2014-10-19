package no.mesan.testdatagen.recordgen

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.generators.norway.Fnr
import no.mesan.testdatagen.generators.{Dates, Fixed, FromList, Ints, Strings}

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ToFixedWidthSpec extends FlatSpec {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).format("%4d").sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val fnrGen = Fnr(FromList(dates).sequential)
    val recordGen = ToFixedWidth().
      add("id", idGen, 5).
      add("userId", codeGen, 3).
      add("ssn", fnrGen,11)
  }

  "The FixedWidth generator" should "not accept 0-width specs" in {
    intercept[IllegalArgumentException] { ToFixedWidth().add("bah", Fixed(12), 0).get(1) }
  }

  it should "require at least one generator" in {
    intercept[IllegalArgumentException] {
      ToFixedWidth().get(1)
    }
  }

  it should "generate expected contents" in {
    new Setup {
      val res=recordGen.get(3).mkString("\n")
      assert(res.matches("(?s)\\s*id.*$"),res)
      assert(ToFixedWidth(false).add("aha", Fixed("aha"), 4).get(1)(0)==="aha ")
    }
  }
}
