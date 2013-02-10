package net.kalars.testgen.generators.misc

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

import net.kalars.testgen.{ExtendedGenerator, SingleGenerator}

@RunWith(classOf[JUnitRunner])
class CarMakesGeneratorSuite extends FunSuite {
  print {
    println(CarMakesGenerator().get(25))
    println(CarMakesGenerator().reversed.get(25))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      CarMakesGenerator().get(-1)
    }
    intercept[IllegalArgumentException] {
      CarMakesGenerator().getStrings(-1)
    }
  }

  test("from/to not suported") {
    val l: ExtendedGenerator[String] = CarMakesGenerator()
    intercept[UnsupportedOperationException] {
      l.from("a").get(1)
    }
    intercept[UnsupportedOperationException] {
      l.to("<").get(1)
    }
  }

  test("reverted sequence") {
    assert(CarMakesGenerator().reversed.get(25).size === 25)
  }

  test("0 sequential elements") {
    assert(CarMakesGenerator().sequential.get(0) === List())
  }

  test("0 random elements") {
    assert(CarMakesGenerator().get(0) === List())
  }

  test("normal random") {
    assert(CarMakesGenerator().get(25).size === 25)
  }

  test("filter 1") {
    val gen = CarMakesGenerator().filter(s => s startsWith "A")
    val res = gen.get(100)
    assert(res.forall(s => List("Alfa Romeo", "Aston Martin", "Atlas", "Audi", "Austin",
     "Autobianchi") contains s))
  }
}
