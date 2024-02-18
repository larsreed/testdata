package net.kalars.testdatagen.aggreg

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.generators.{Booleans, Chars, FromList}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class WeightedGeneratorSpec extends FlatSpec {

  trait Setup {
    val xgen= WeightedGenerator[Any]((1, FromList(1,2,3)),
                                     (2, Chars("1abc")),
                                     (1, Booleans().format("0", "1"))).
                                       filter(x=> !x.toString.matches("1"))
  }

  "WeightedGenerator" should "produce expected contents" in {
    new Setup {
      val res= xgen.get(1000)
      assert(res contains 2)  // could fail randomly..
      assert(res contains 'c')  // could fail randomly..
      assert(res contains true)  // could fail randomly..
    }
  }

  it should "format contents according to spec" in {
    new Setup {
      val res= xgen.getStrings(1000)
      assert(res contains "2")  // could fail randomly..
      assert(res contains "c")  // could fail randomly..
      assert(res contains "0", res)  // could fail randomly..
    }
  }

}