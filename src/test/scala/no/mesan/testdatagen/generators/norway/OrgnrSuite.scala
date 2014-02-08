package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer
import scala.language.postfixOps
import scala.annotation.tailrec

@RunWith(classOf[JUnitRunner])
class OrgnrSuite extends FunSuite with Printer {

  def sjekkOrgnr(orgNr: Int) = {
    val digits= orgNr.toString map(_.toString.toInt) toList
    @tailrec def sjekk(rest: List[Int], fakt: List[Int], sum: Int): Boolean =
      if (fakt.isEmpty) {
        val mod11 = sum % 11
        (mod11 == 0 && rest.head == 0) || (rest.head == 11 - mod11)
      }
      else
        sjekk(rest.tail, fakt.tail, sum + (rest.head * fakt.head))
    sjekk(digits, List(3, 2, 7, 6, 5, 4, 3, 2), 0)
  }

  print(false) {
    println(Orgnr() get 120)
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      Orgnr().get(-1)
    }
    intercept[IllegalArgumentException] {
      Orgnr().getStrings(-1)
    }
  }

  test("count") {
    assert(Orgnr().get(120).size === 120)
  }

  test("contents checksum") {
    val res= Orgnr() get 300
    for (nr<-res) assert(sjekkOrgnr(nr), ""+nr)
  }

  test("empty") {
    assert(Orgnr().get(0).size === 0)
  }
}
