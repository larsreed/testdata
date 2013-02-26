package no.mesan.testdatagen.generators.misc

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class FibonaccisSuite extends FunSuite with Printer {

  print(false) {
    // println(Fibonaccis() filter(bi=> bi%2==0) getStrings(120))
    println(Fibonaccis() getStrings(500))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      Fibonaccis().get(-1)
    }
    intercept[IllegalArgumentException] {
      Fibonaccis().getStrings(-1)
    }
  }

  test("count") {
    assert(Fibonaccis().get(120).size === 120)
  }

  test("empty") {
    assert(Fibonaccis().get(0).size === 0)
  }

  test("one") {
    val expected= List(1)
    assert(Fibonaccis().get(expected.size) === expected)
  }

  test("two") {
    val expected= List(1, 1)
    assert(Fibonaccis().get(expected.size) === expected)
  }

  test("not two") {
    val expected= List(1, 1, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584)
    assert(Fibonaccis().filter(n=> !(n==2)).get(expected.size) === expected)
  }
}