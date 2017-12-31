package no.netcompany.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.ExtendedGenerator
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps


@RunWith(classOf[JUnitRunner])
class FromListSpec extends FlatSpec {

  "FromList" should "not accept from/to-limits" in {
    val l: ExtendedGenerator[Int] = FromList(List(1, 2, 3))
    intercept[UnsupportedOperationException] {
      l.from(1).get(1)
    }
    intercept[UnsupportedOperationException] {
      l.to(1).get(1)
    }
  }

  it should "not allow an empty input list" in {
    intercept[IllegalArgumentException] {
      val l: List[String] = Nil
      FromList(l).get(1)
    }
  }

  private def aStringGen(chrs: String): Strings = Strings() chars chrs lengthBetween(1, 2) sequential

  it should "act as an intermediary for other generators" in {
    val orig = aStringGen("ab") get 100
    val gen = FromList(orig).sequential
    assert(gen.get(6) === List("a", "b", "aa", "ab", "ba", "bb"))
  }


  it should "be able to reverse its input" in {
    assert(FromList(List("A", "B", "C").reverse).sequential.get(25) ===
      List("C", "B", "A", "C", "B", "A", "C", "B", "A", "C", "B", "A", "C", "B", "A",
        "C", "B", "A", "C", "B", "A", "C", "B", "A", "C"))
  }

  it should "handle input lengths of one" in {
    assert(FromList(List(42)).sequential.get(2) === List(42, 42))
    assert(FromList(List(42)).get(2) === List(42, 42))
    assert(FromList(List(42)).gen.take(2).toList === List(42, 42))
  }

  it should "generate from short sequences" in {
    assert(FromList(List(1, 2)).sequential.get(2) === List(1, 2))
    assert(FromList(List(1)).sequential.get(2) === List(1, 1))
  }

  it should "generate random sequences" in {
    val orig = aStringGen("abc") get 100
    val gen = FromList(orig)
    val res = gen.get(25)
    assert(res.length === 25)
    assert(res.forall(s => (s.length == 1 || s.length == 2) && s.matches("^[a-c]*$")))
  }

  it should "allow filtering" in {
    val orig = aStringGen("abc") get 100
    val gen = FromList(orig).filter(s => !(s contains "b"))
    val res = gen.get(100)
    assert(res.forall(s => s.matches("^[ac]*$")))
  }

  it should "format results" in {
    val g = FromList(List(1, 2, 3, 4, 5, 6)).format("%04d").sequential
    assert(g.getStrings(4) === List("0001", "0002", "0003", "0004"))
  }

  it should "handle single element input list" in {
    val res = FromList(List("a")).distinct.get(1).toSet
    assert(res.size == 1)
  }

  "FromLists.weighted" should "return elements according to its weights" in {
    val gen= FromList.weighted(List(
      (2, 1),
      (3, 2),
      (1, 3)
    )).sequential
    val res= gen.get(7)
    assert(res===List(1, 1, 2, 2, 2, 3, 1))
  }

  it should "refuse empty input" in {
    intercept[IllegalArgumentException] {
      FromList.weighted(List()).get(1)
    }
  }
}
