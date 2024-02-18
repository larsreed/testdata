package net.kalars.testdatagen.generators.misc

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CreditCardsSpec extends FlatSpec {

  "CreditCards" should "generate values in the right range" in {
    val res = CreditCards().get(80)
    for (n <- res) assert(n>=1000000000000000L && n<=9999999999999999L, n)
  }

  it should "use correct range for Visa" in {
    val res = CreditCards.visas.get(20)
    for (n <- res) assert(n>=4000000000000000L && n<=4999999999999999L, n)
  }

  it should "use correct range for MasterCard" in {
    val res = CreditCards.masterCards.get(20)
    for (n <- res) assert(n>=5100000000000000L && n<=5599999999999999L, n)
  }
}