package net.kalars.testgen.generators.misc

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import net.kalars.testgen.{ExtendedGenerator, FunSuite}

@RunWith(classOf[JUnitRunner])
class CarMakesSuite extends FunSuite {
  print {
    println(CarMakes().get(25))
    println(CarMakes().reversed.get(25))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      CarMakes().get(-1)
    }
    intercept[IllegalArgumentException] {
      CarMakes().getStrings(-1)
    }
  }

  test("from/to not suported") {
    val l: ExtendedGenerator[String] = CarMakes()
    intercept[UnsupportedOperationException] {
      l.from("a").get(1)
    }
    intercept[UnsupportedOperationException] {
      l.to("<").get(1)
    }
  }

  test("reverted sequence") {
    assert(CarMakes().reversed.get(25).size === 25)
  }

  test("0 sequential elements") {
    assert(CarMakes().sequential.get(0) === List())
  }

  test("0 random elements") {
    assert(CarMakes().get(0) === List())
  }

  test("normal random") {
    assert(CarMakes().get(25).size === 25)
  }

  test("filter 1") {
    val gen = CarMakes().filter(s => s startsWith "A")
    val res = gen.get(100)
    assert(res.forall(s => List("Alfa Romeo", "Aston Martin", "Atlas", "Audi", "Austin",
     "Autobianchi") contains s))
  }
}
