package no.mesan.testdatagen.generators

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.{Unique, Printer}

@RunWith(classOf[JUnitRunner])
class DoublesSuite extends FunSuite with Printer {

  print(false) {
    println(Doubles().from(4).to(44).get(30))
    println(Doubles().step(7).from(4).to(44).reversed.get(30))
  }

  test("from<to") {
    intercept[IllegalArgumentException] {
      Doubles().from(10.0).to(5).get(1)
    }
  }

  test("sequential not step 0") {
    intercept[IllegalArgumentException] {
      Doubles().step(0).sequential.get(1)
    }
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      Doubles().get(-1)
    }
    intercept[IllegalArgumentException] {
      Doubles().getStrings(-1)
    }
  }

  test("normal sequence") {
    val res = Doubles().sequential.from(-2).to(2).get(5)
    for (d <- res) assert(d <= 2 & d >= -2, d + "")
  }

  test("sequence that overflows") {
    assert(Doubles().step(2).sequential.from(1.0).to(6.0).get(5) === List(1.0, 3.0, 5.0, 1.0, 3.0))
  }

  test("reverted sequence") {
    assert(Doubles().from(-1.0).to(6.0).reversed.get(5) === List(6.0, 5.0, 4.0, 3.0, 2.0))
  }

  test("negative step ignored") {
    assert(Doubles().step(-3.5).sequential.from(1.5).to(11).get(5) === List(1.5, 5.0, 8.5, 1.5, 5.0))
  }

  test("sequence of 1 & 2") {
    assert(Doubles().sequential.from(19278.0).to(19278.0).get(2) === List(19278.0, 19278.0))
    assert(Doubles().sequential.from(19278.0).to(19279.5).get(2) === List(19278.0, 19279.0))
  }

  test("0 sequential elements") {
    assert(Doubles().sequential.from(11.44).to(61.19).get(0) === List())
  }

  test("0 random elements") {
    assert(Doubles().from(1).to(6).get(0) === List())
  }

  test("normal random") {
    val res = Doubles().step(3.33).from(-67.63).to(110.12).get(125)
    assert(res.length === 125)
    assert(res.forall(i => i <= 110.12 && i >= -67.63))
  }

  test("filtering") {
    val res = Doubles().from(-100).to(100).filter(i => i < 0).get(100)
    assert(res.length === 100)
    assert(res.forall(i => math.abs(i) != i))
  }

  test("formatting") {
    val res = Doubles().step(2.5).from(-2.5).to(10).sequential.formatWith(i => f"$i%04.2f").
      getStrings(7).map(s => s.replaceAll("[.,]", ":"))
    assert(res === List("-2:50", "0:00", "2:50", "5:00", "7:50", "10:00", "-2:50"))
    val res3 = Doubles().step(2).from(-2.5).to(10).sequential.format("%02.0f").
      getStrings(7).map(s => s.replaceAll("[.,]", ":"))
    assert(res3 === List("-3", "-1", "02", "04", "06", "08", "10"))
    val res2 = Doubles().step(2).from(-2).to(10).sequential.formatWith(i => f"$i%02f").getStrings(0)
    assert(res2 === Nil)
  }

  test("unique list") {
    val  res= Unique(Doubles() from(-999) to(-997)).get(500).toSet
    assert(res.size===500)
  }
}
