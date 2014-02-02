package no.mesan.testdatagen.generators.misc

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class MarkovSuite extends FunSuite with Printer {

  print(false) {
    // println(Markov() filter(bi=> bi%2==0) getStrings(120))
    println(Markov().getStrings(500))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      new Markov().get(-1)
    }
    intercept[IllegalArgumentException] {
      new Markov().getStrings(-1)
    }
  }

  test("no filter") {
    intercept [UnsupportedOperationException] {
      new Markov().filter(s=>true)
    }
  }

  test("no format") {
    intercept[UnsupportedOperationException] {
      new Markov().formatWith(s=>s)
    }
  }

  test("count") {
    assert(Markov().get(120).size === 120)
    assert(Markov().getStrings(120).size === 120)
  }

  test("empty") {
    intercept[IllegalArgumentException] {
      new Markov().get(0)
    }
  }

  test("parse empty") {
    // NOTE: This is a test of internal representation, not end functionality
    val gen= new Markov()
    val res= gen.build(List())
    assert(res.size===0)
  }

  test("parse single word") {
    // NOTE: This is a test of internal representation, not end functionality
    val gen= new Markov()
    val res= gen.build(List("word"))
    assert(res.size===1)
    assert(res.getOrElse("word", List("failed")).size===0)
  }

  test("parse simplest sequence") {
    // NOTE: This is a test of internal representation, not end functionality
    val gen= new Markov()
    val res= gen.build(List("begin", "end"))
    assert(res.size===2)
    assert(res.getOrElse("begin", List())(0) === "end")
    assert(res.getOrElse("end", List("failed")).size===0)
  }

  test("parse simple sequence") {
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

  test("simple generation") {
    val gen= new Markov().buildFromList(List("a", "b","c"))
    val possible= List("a b c",
      "b c a", "b c b", "b c c",
      "c a b", "c b c", "c c a", "c c b", "c c c")
    for (n<-1 to 50) {
      val res= gen.getStrings(3)(0)
      assert(possible.contains(res), res)
    }
  }
}