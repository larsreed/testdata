package no.netcompany.testdatagen.generators.misc

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MarkovSpec extends FlatSpec {

  "The Markov generator INTERNAL implementation" should "parse an empty list" in {
    // NOTE: This is a test of internal representation, not end functionality
    val gen= new Markov()
    val res= gen.build(List())
    assert(res.size===0)
  }

  it should "parse a single word" in {
    // NOTE: This is a test of internal representation, not end functionality
    val gen= new Markov()
    val res= gen.build(List("word"))
    assert(res.size===1)
    assert(res.getOrElse("word", List("failed")).size===0)
  }

  it should "parse a simple sequence" in {
    // NOTE: This is a test of internal representation, not end functionality
    val gen= new Markov()
    val res= gen.build(List("begin", "end"))
    assert(res.size===2)
    assert(res.getOrElse("begin", List()).head === "end")
    assert(res.getOrElse("end", List("failed")).size===0)
  }

  it should "parse another simple sequence" in {
    // NOTE: This is a test of internal representation, not end functionality
    val gen= new Markov()
    val res= gen.build(List("a", "b", "a", "c"))
    assert(res.size===3)
    val a= res.getOrElse("a", List())
    val b= res.getOrElse("b", List())
    val c= res.getOrElse("c", List())
    assert(a.size===2, a)
    assert(b.size===1, b)
    assert(c.size===0, c)
    assert(!a.contains("a"))
    assert(a.contains("b"))
    assert(a.contains("c"))
  }

  "The Markov generator" should "generate a simple sequence" in {
    val gen= new Markov().buildFromList(List("a", "b","c"))
    val possible= List("a b c",
      "b c a", "b c b", "b c c",
      "c a b", "c b c", "c c a", "c c b", "c c c")
    for (_ <-1 to 50) {
      val res= gen.mkString(3)
      assert(possible.contains(res), res)
    }
  }
}
