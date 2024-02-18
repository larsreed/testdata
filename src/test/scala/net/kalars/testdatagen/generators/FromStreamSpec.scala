package net.kalars.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps


@RunWith(classOf[JUnitRunner])
class FromStreamSpec extends FlatSpec {

  private def aStringGen(chrs: String): Strings = Strings() chars chrs lengthBetween(1, 2) sequential

  "FromStream"  should "act as an intermediary for other generators" in {
    val orig = aStringGen("ab").sequential.gen
    val gen = FromStream(orig)
    assert(gen.get(6) === Stream("a", "b", "aa", "ab", "ba", "bb"))
  }

  it should "allow filtering" in {
    val orig = aStringGen("abc") gen
    val gen = FromStream(orig).filter(s => !(s contains "b"))
    val res = gen.get(100)
    assert(res.forall(s => s.matches("^[ac]*$")))
  }
}