package net.kalars.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class CharsSpec extends FlatSpec {

  "The Chars generator" should "check that from/to are in correct order" in {
    intercept[IllegalArgumentException] {
      Chars().from('z').to('a').get(1)
    }
  }

  it should "return a list, however long, of the requested characters" in {
    val res = (Chars() chars "ab" sequential).gen take 1000
    assert(res.length === 1000)
    val exp = List('a', 'b')
    exp.foreach(s => assert(res contains s, s))

    val res2 = (Chars() chars "abx" sequential) get 5000 toSet
    val expect = Set('a', 'b', 'x')
    assert(res2 === expect)
    assert(res2.size === expect.size, expect)
  }

  it should "use the supplied character range in the specified order" in {
    assert(Chars().chars('a' to 'z').sequential.get(2) === List('a', 'b'))
    assert(Chars().chars("a").sequential.get(2) === List('a', 'a'))
  }

  it should "not return values outside its range" in {
    val res = Chars() chars(' ' to 'Z') get 25
    assert(res.length === 25)
    assert(res.forall(s=> s >= ' ' && s <= 'Z'), res)
  }

  it should "obey a specified filter function" in  {
    val gen = (Chars() chars('a' to 'z') sequential).filter(c => "aeiouy" contains c).gen
    val res= gen take 10
    assert(res.length === 10)
    assert(res.forall(c=> (c=='a') || (c=='e') || (c=='i') || (c=='o') || (c=='u') || (c=='y')),
           res)
  }

  it should "be able to use use different format specifications" in  {
    val res1 = Chars().chars("ABC").sequential.formatWith(c => f"$c%7s").getStrings(3)
    assert(res1 === List("      A", "      B", "      C"))
    val res2 = Chars().sequential.format("%7s").getStrings(0)
    assert(res2 === Nil)
    val res3 = Chars().chars("AB").sequential.format("%-5s").getStrings(3)
    assert(res3 === List("A    ", "B    ", "A    "))
  }
}