package no.netcompany.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class FixedSpec extends FlatSpec {

  "The Fixed generator" should "generate a contionous stream of equal values" in {
    val res= Fixed(List(42)) get 100
    assert(res forall(l=> l.size==1 && l(0)==42))
  }

  it should "reject a filter that filters away its only legal value" in {
    intercept[IllegalArgumentException] {
      val gen= Fixed(10) filter {i=> i!=10}
      gen get 4
    }
  }
}
