package no.mesan.testdatagen.aggreg

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.generators.{Ints, Booleans, Chars, FromList}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SomeNullsSpec extends FlatSpec {

  trait Setup {
    val xgen= SomeNulls(50, Ints())
  }

  "SomeNulls" should "generate expected output" in  {
    new Setup {
      val nulls= xgen.genStrings.take(500).filter(_==null)
      assert(nulls.size>150 && nulls.size<350)
    }
  }
}
