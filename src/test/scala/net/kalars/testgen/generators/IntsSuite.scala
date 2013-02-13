package net.kalars.testgen.generators

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class IntsSuite extends FunSuite {

  print {
    println(Ints().from(4).to(44).get(30))
    println(Ints().step(7).from(4).to(44).reversed.get(30))
  }

  test("from<to") {
    intercept[IllegalArgumentException] {
      Ints().from(10).to(5).get(1)
    }
  }

  test("sequential not step 0") {
    intercept[IllegalArgumentException] {
      Ints().step(0).sequential.get(1)
    }
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      Ints().get(-1)
    }
    intercept[IllegalArgumentException] {
      Ints().getStrings(-1)
    }
  }

  test("normal sequence") {
    assert(Ints().sequential.from(-2).to(2).get(5) === List(-2, -1, 0, 1, 2))
  }

  test("sequence that overflows") {
    assert(Ints().step(2).sequential.from(1).to(6).get(5) === List(1, 3, 5, 1, 3))
  }

  test("reverted sequence") {
    assert(Ints().from(1).to(6).reversed.get(5) === List(6, 5, 4, 3, 2))
  }

  test("negative step ignored") {
    assert(Ints().step(-3).sequential.from(1).to(6).get(5) === List(1, 4, 1, 4, 1))
  }

  test("sequence of 1 & 2") {
    assert(Ints().sequential.from(19278).to(19278).get(2) === List(19278, 19278))
    assert(Ints().sequential.from(19278).to(19279).get(2) === List(19278, 19279))
  }

  test("0 sequential elements") {
    assert(Ints().sequential.from(1).to(6).get(0) === List())
  }

  test("0 random elements") {
    assert(Ints().from(1).to(6).get(0) === List())
  }

  test("normal random") {
    val res = Ints().from(-10).to(10).get(25)
    assert(res.length === 25)
    assert(res.forall(i => i <= 10 && i >= -10))
  }

  test("even numbers") {
    val res = Ints().filter(i => (i % 2) == 0).get(10);
    assert(res.length === 10)
    assert(res.forall(i => (i % 2) == 0))
    val res2 = Ints().from(0).sequential.filter(i => (i % 2) == 0).get(9);
    assert(res2.length === 9)
    assert(res2.forall(i => (i % 2) == 0))
  }

  test("formatting") {
    val res = Ints().step(2).from(-2).to(10).sequential.formatWith(i => i.formatted("%02d")).getStrings(7)
    assert(res === List("-2", "00", "02", "04", "06", "08", "10"))
    val res3 = Ints().step(2).from(-2).to(10).sequential.format("%02d").getStrings(7)
    assert(res3 === List("-2", "00", "02", "04", "06", "08", "10"))
    val res2 = Ints().step(2).from(-2).to(10).sequential.formatWith(i => i.formatted("%02d")).getStrings(0)
    assert(res2 === Nil)
  }

  test("unique list") {
    val res = Ints().from(-1500).to(-1400).unique.getStrings(100).toSet
    assert(res.size == 100)
  }

  test("unique string list") {
    val res = Ints().from(1500).to(1599).format("%12d").unique.getStrings(99).toSet
    assert(res.size == 99)
  }
}
