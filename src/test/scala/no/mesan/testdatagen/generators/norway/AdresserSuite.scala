package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class AdresserSuite extends FunSuite with Printer {

  print(false) {
    println(Adresser().get(10))
    println(Adresser(false).get(10))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      Adresser().get(-1)
    }
    intercept[IllegalArgumentException] {
      Adresser().getStrings(-1)
    }
  }

  test("count") {
    assert(Adresser().get(120).size === 120)
  }

  test("contents with number") {
    val res= Adresser().get(111)
    res.foreach{
      adr => assert(adr.matches("^.*[0-9]+[A-F]?$"), adr)
    }
  }

  test("contents without number") {
    val res= Adresser(false).get(111)
    res.foreach{
      adr => assert(adr.matches("^.*$"), adr)
    }
  }

  test("empty") {
    assert(Adresser().get(0).size === 0)
  }
}