package net.kalars.testdatagen.generators.misc

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GuidsSpec extends FlatSpec {

  "The Guids" should "produce correctly formatted strings" in {
    Guids().getStrings(120).foreach(s =>
      assert(s.matches("^[0-9a-f]{8}-([0-9a-f]{4}-){2}[0-9a-f]{16}$"), s))
  }
}