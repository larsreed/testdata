package no.mesan.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, FunSuite}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RareNavnSpec extends FlatSpec {

  "The RareNavn generator" should "generate correct contents" in {
    val res = RareNavn().sequential.get(300)
    assert(res.contains("Buster Minal"))
    val res2 = RareNavn().sequential.getStrings(300)
    assert(res2.contains("Buster Minal"))
  }
}
