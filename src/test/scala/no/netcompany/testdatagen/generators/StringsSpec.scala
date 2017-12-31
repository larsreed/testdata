package no.netcompany.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.language.postfixOps

import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, FunSuite}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class StringsSpec extends FlatSpec {

  "The Strings generator" should "check that from<=to" in {
    intercept[IllegalArgumentException] {
      Strings().from("Z").to("A").get(1)
    }
  }

  it should "not allow negative lengths" in {
    intercept[IllegalArgumentException] {
      Strings().lengthBetween(-1,1)
    }
    intercept[IllegalArgumentException] {
      Strings().length(-1)
    }
  }

  it should "check that from<=to for lengths" in {
    intercept[IllegalArgumentException] {
      Strings().lengthBetween(5,4)
    }
  }

  it should "generate expected sequences" in {
    val res= Strings().chars("ab").lengthBetween(1, 2).sequential.get(1000)
    assert(res.length===1000)
    val exp= List("a", "b", "aa", "ab", "ba", "bb")
    exp.foreach(s=> assert(res contains s, res + "<" + s))
  }

  it should "generate expected sequences #2" in {
    val res= Set() ++ Strings().chars("abx").lengthBetween(1, 2).sequential.get(5000)
    val expect=Set() ++ List("a", "b", "x", "aa", "ab", "ax", "ba", "bb", "bx", "xa", "xb", "xx")
    assert(res===expect)
    assert(res.size===expect.size, expect)
  }

  it should "generate sequences with lenghts 1 & 2" in {
    assert(Strings().chars('a' to 'z').length(1).sequential.get(2)===List("a", "b"))
    assert(Strings().chars("a").length(1).sequential.get(2)===List("a", "a"))
  }

  it should "generate random sequences" in {
    val res= Strings().length(9).chars(' ' to 'Z').get(25)
    assert(res.length===25)
    assert(res.forall(s=> s.length==9 && s>=" " && s<="ZZZZZZZZZ"))
  }

  it should "generate random sequences #2" in {
    val res= Set() ++ Strings().chars("abx").lengthBetween(1, 2).get(5000)
    val expect=Set() ++ List("a", "b", "x", "aa", "ab", "ax", "ba", "bb", "bx", "xa", "xb", "xx")
    assert(res===expect)
  }

  it should "filter output" in {
    val res= Strings().length(4).chars('a' to 'z').filter(s=>s contains "e").get(10)
    assert(res.length===10)
    assert(res.forall(s=> s contains "e"), res)
  }

  it should "filter output #2" in {
    val res= Strings().length(4).chars('a' to 'z').sequential.filter(s=>s contains "e").get(10)
    assert(res.length===10)
    assert(res.forall(s=> s contains "e"), res)
  }

  it should "format output" in {
    val  res= Strings().length(2).chars("ABC").sequential.formatWith(i=>f"$i%7s").getStrings(3)
    assert(res=== List("     AA", "     AB", "     AC"))
    val  res3= Strings().length(2).chars("AB").sequential.format("%-5s").getStrings(3)
    assert(res3=== List("AA   ", "AB   ", "BA   "))
    val  res2= Strings().from("A").to("B").sequential.format("%7s").getStrings(0)
    assert(res2=== Nil)
  }
}
