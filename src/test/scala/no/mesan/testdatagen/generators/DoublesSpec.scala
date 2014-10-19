package no.mesan.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DoublesSpec extends FlatSpec {

  "The Doubles generator" should "check that from<to" in {
    intercept[IllegalArgumentException] {
      Doubles().from(10.0).to(5).get(1)
    }
  }

  it should "disallow step size of 0" in {
    intercept[IllegalArgumentException] {
      Doubles().step(0).sequential.get(1)
    }
  }

  it should "generate a normal sequence of doubles" in {
    val res = Doubles().sequential.from(-2).to(2).get(5)
    for (d <- res) assert(d <= 2 & d >= -2, d + "")
  }

  it should "rotate when the upper limit is reached" in {
    assert(Doubles().step(2).sequential.from(1.0).to(6.0).get(5) === List(1.0, 3.0, 5.0, 1.0, 3.0))
  }

  it should "generate reverted sequences" in {
    assert(Doubles().from(-1.0).to(6.0).step(-1).get(5) === List(6.0, 5.0, 4.0, 3.0, 2.0))
  }

  it should "generate sequences of 1 & 2" in {
    assert(Doubles().sequential.from(19278.0).to(19278.0).get(2) === List(19278.0, 19278.0))
    assert(Doubles().sequential.from(19278.0).to(19279.5).get(2) === List(19278.0, 19279.0))
  }

  it should "generate sequences within bounds" in {
    val res = Doubles().step(3.33).from(-67.63).to(110.12).get(125)
    assert(res.length === 125)
    assert(res.forall(i => i <= 110.12 && i >= -67.63))
  }

  it should "allow filtering" in {
    val res = Doubles().from(-100).to(100).filter(i => i < 0).get(100)
    assert(res.length === 100)
    assert(res.forall(i => math.abs(i) != i))
  }

  it should "allow formatting" in {
    val res = Doubles().step(2.5).from(-2.5).to(10).sequential.formatWith(i => f"$i%04.2f").
      getStrings(7).map(s => s.replaceAll("[.,]", ":"))
    assert(res === List("-2:50", "0:00", "2:50", "5:00", "7:50", "10:00", "-2:50"))
    val res3 = Doubles().step(2).from(-2.5).to(10).sequential.format("%02.0f").
      getStrings(7).map(s => s.replaceAll("[.,]", ":"))
    assert(res3 === List("-3", "-1", "02", "04", "06", "08", "10"))
    val res2 = Doubles().step(2).from(-2).to(10).sequential.formatWith(i => f"$i%02f").getStrings(0)
    assert(res2 === Nil)
  }
}
