package no.mesan.testdatagen.generators.misc

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import no.mesan.testdatagen.Printer
import no.mesan.testdatagen.aggreg.TextWrapper

@RunWith(classOf[JUnitRunner])
class GuidsSuite extends FunSuite with Printer {

  print(false) {
    println(Guids().get(10))
    println(TextWrapper(Guids()) surroundWith("{", "}") toUpper() getStrings(120))
    println(Guids().getBigInts(100))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      Guids().get(-1)
    }
    intercept[IllegalArgumentException] {
      Guids().getStrings(-1)
    }
  }

  test("count") {
    assert(Guids().get(120).size === 120)
  }

  test("strings") {
    Guids().getStrings(120).foreach(s => 
      assert(s.matches("^[0-9a-f]{8}-([0-9a-f]{4}-){2}[0-9a-f]{16}$"), s))
  }

  test("empty") {
    assert(Guids().get(0).size === 0)
  }
}
