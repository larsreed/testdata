package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class RareNavnSuite extends FunSuite with Printer {

  print(false) {
    println(RareNavn().get(120))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      RareNavn(false).get(-1)
    }
    intercept[IllegalArgumentException] {
      RareNavn(false).getStrings(-1)
    }
  }

  test("count") {
    assert(RareNavn(true).get(30).size === 30)
  }

  test("contents") {
    val res = RareNavn().sequential.get(300)
    assert(res.contains("Buster Minal"))
    val res2 = RareNavn(false).sequential.getStrings(300)
    assert(res2.contains("Buster Minal"))
  }
}
