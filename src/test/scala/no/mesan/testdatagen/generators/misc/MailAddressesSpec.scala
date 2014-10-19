package no.mesan.testdatagen.generators.misc

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MailAddressesSpec extends FlatSpec {
  "The MailAdress generator" should "produce expected contents" in {
    val res= MailAddresses().get(20)
    for (s<-res) assert(s.matches("^[a-z]+[.a-z]+[@][.a-z]+$"), s)
  }
}
