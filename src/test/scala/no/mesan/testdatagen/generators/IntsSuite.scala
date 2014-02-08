package no.mesan.testdatagen.generators

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class IntsSuite extends FunSuite with Printer {

  def generator= Ints()

  print(false) {
    println(generator.from(4).to(44).get(30))
    println(generator.step(7).from(4).to(44).reversed.get(30))
  }

  test("from<to") {
    intercept[IllegalArgumentException] {
      generator.from(10).to(5).get(1)
    }
  }

  test("sequential not step 0") {
    intercept[IllegalArgumentException] {
      generator.step(0).sequential.get(1)
    }
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      generator.get(-1)
    }
    intercept[IllegalArgumentException] {
      generator.getStrings(-1)
    }
  }

  test("normal sequence") {
    assert(generator.sequential.from(-2).to(2).get(5) === List(-2, -1, 0, 1, 2))
  }

  test("sequence that overflows") {
    assert(generator.step(2).sequential.from(1).to(6).get(5) === List(1, 3, 5, 1, 3))
  }

  test("wrap the edge") {
    assert(generator.sequential.from(Int.MaxValue-3).get(5) ===
      List(Int.MaxValue-3, Int.MaxValue-2, Int.MaxValue-1, Int.MaxValue-3, Int.MaxValue-2))
    assert(generator.reversed.to(Int.MinValue+3).get(5) ===
      List(Int.MinValue+3, Int.MinValue+2, Int.MinValue+1, Int.MinValue+3, Int.MinValue+2))
  }

  test("reverted sequence") {
    assert(generator.from(1).to(6).reversed.get(5) === List(6, 5, 4, 3, 2))
  }

  test("negative step ignored") {
    assert(generator.step(-3).sequential.from(1).to(6).get(5) === List(1, 4, 1, 4, 1))
  }

  test("sequence of 1 & 2") {
    assert(generator.sequential.from(19278).to(19278).get(2) === List(19278, 19278))
    assert(generator.sequential.from(19278).to(19279).get(2) === List(19278, 19279))
  }

  test("0 sequential elements") {
    assert(generator.sequential.from(1).to(6).get(0) === List())
  }

  test("0 random elements") {
    assert(generator.from(1).to(6).get(0) === List())
  }

  test("normal random") {
    val res = generator.from(-10).to(10).get(25)
    assert(res.length === 25)
    assert(res.forall(i => i <= 10 && i >= -10))
  }

  test("even numbers") {
    val res = generator.filter(i => (i % 2) == 0).get(10)
    assert(res.length === 10)
    assert(res.forall(i => (i % 2) == 0))
    val res2 = generator.from(0).sequential.filter(i => (i % 2) == 0).get(9)
    assert(res2.length === 9)
    assert(res2.forall(i => (i % 2) == 0))
  }

  test("formatting") {
    val res = generator.step(2).from(-2).to(10).sequential.formatWith(i => f"$i%02d").getStrings(7)
    assert(res === List("-2", "00", "02", "04", "06", "08", "10"))
    val res3 = generator.step(2).from(-2).to(10).sequential.format("%02d").getStrings(7)
    assert(res3 === List("-2", "00", "02", "04", "06", "08", "10"))
    val res2 = generator.step(2).from(-2).to(10).sequential.formatWith(i => f"$i%02d").getStrings(0)
    assert(res2 === Nil)
  }

  test("unique list") {
    val res = generator.from(-1500).to(-1400).unique.getStrings(100).toSet
    assert(res.size == 100)
  }

  test("unique string list") {
    val res = generator.from(1500).to(1599).format("%12d").unique.getStrings(99).toSet
    assert(res.size == 99)
  }
}
