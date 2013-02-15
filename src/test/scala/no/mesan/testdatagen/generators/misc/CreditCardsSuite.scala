package no.mesan.testdatagen.generators.misc

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class CreditCardsSuite extends FunSuite with Printer {

  print(true) {
    println(CreditCards().get(12))
    println(CreditCards.visas.get(12))
    println(CreditCards.masterCards.get(12))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      CreditCards().get(-1)
    }
    intercept[IllegalArgumentException] {
      CreditCards().getStrings(-1)
    }
  }

  test("count") {
    assert(CreditCards().get(30).size === 30)
  }

  test("contents") {
    val res = CreditCards().get(80)
    for (n <- res) assert(n>=1000000000000000L && n<=9999999999999999L, n)
  }

  test("unique") {
    val res = new CreditCards(List(12345678901234L)).unique.get(9).toSet
    assert(res.size===9)
  }

  test("visa") {
    val res = CreditCards.visas.get(20)
    for (n <- res) assert(n>=4000000000000000L && n<=4999999999999999L, n)
  }

  test("mastercard") {
    val res = CreditCards.masterCards.get(20)
    for (n <- res) assert(n>=5100000000000000L && n<=5599999999999999L, n)
  }

}
