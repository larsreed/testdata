package no.mesan.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BooleansSpec extends FlatSpec  {

  "The Booleans generator" should "generate an expected sequence" in {
    assert(Booleans().sequential.get(5)===List(false, true, false, true, false))
  }

  it should "filter" in {
    assert(Booleans().sequential.filter(t=> !t).get(3)===List(false, false, false))
  }

  it should "generate randowm values" in {
    val res= Booleans().get(250)
    assert(res.length===250)
    assert(res contains true)
    assert(res contains false)
  }

  it should "have default string formatting" in {
    val  res= Booleans().sequential.getStrings(4)
    assert(res=== List("false", "true", "false", "true"))
  }

  it should "allow customized formatting" in {
    val  res= Booleans().format("0", "1").sequential.getStrings(4)
    assert(res=== List("0", "1", "0", "1"))
  }
}
