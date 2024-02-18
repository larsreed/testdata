package net.kalars.testdatagen.generators.misc

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FibonaccisSpec extends FlatSpec {

  "Fibonaccis" should "return 1 for the first two values" in {
    val expected= List(1, 1)
    assert(Fibonaccis().gen.take(2).map(_.toInt) === expected)
  }

  it should "return known values" in {
    val expected= List(1, 1, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584)
    assert(Fibonaccis().filter(n=> !(n.toInt equals 2)).get(expected.size) === expected)
  }
}