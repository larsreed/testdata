package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer
import no.mesan.testdatagen.generators.Dates
import scala.language.existentials
import scala.annotation.tailrec

@RunWith(classOf[JUnitRunner])
class FnrSuite extends FunSuite with Printer {

  def sjekkFnr(fnr: String) = {
    @tailrec def sjekk(fnr: List[Int], fakt: List[Int], sum: Int): Boolean =
      if (fakt.isEmpty) {
        val mod11 = sum % 11
        (mod11 == 0 && fnr.head == 0) || (fnr.head == 11 - mod11)
      }
      else
        sjekk(fnr.tail, fakt.tail, sum + (fnr.head * fakt.head))
    val fnrList = fnr.toList map (_ - '0')
    sjekk(fnrList, List(3, 7, 6, 1, 8, 9, 4, 5, 2), 0) &&
      sjekk(fnrList, List(5, 4, 3, 2, 7, 6, 5, 4, 3, 2), 0)
  }

  print(false) {
    val dg = Dates().from(y = 1968, m = 9, d = 20).to(y = 1968, m = 9, d = 20)
    println(Fnr(dg).withDnr(50) boysOnly() get 1200)
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      Fnr().get(-1)
    }
    intercept[IllegalArgumentException] {
      Fnr().getStrings(-1)
    }
  }

  test("count") {
    assert(Fnr().get(120).size === 120)
  }

  test("contents checksum") {
    val res= (Fnr() boysOnly() withDnr 100 get 100) ++
             (Fnr() girlsOnly() get 100) ++
             (Fnr() get 100)
    for (fnr<-res) assert(sjekkFnr(fnr), fnr)
  }

  test("only dnr") {
    val res= Fnr() withDnr 100 get 200
    for (fnr<-res) assert(fnr.matches("^[4-7].*"), fnr)
  }

  test("no dnr") {
    val res= Fnr() withDnr 0 get 200
    for (fnr<-res) assert(fnr.matches("^[0-3].*"), fnr)
  }

  test("girls") {
    val res= Fnr() girlsOnly() get 200
    for (fnr<-res) assert(fnr.matches("^........[02468].*"), fnr)
  }

  test("boys") {
    val res= Fnr() boysOnly() get 200
    for (fnr<-res) assert(fnr.matches("^........[13579].*"), fnr)
  }

  test("empty") {
    assert(Fnr().get(0).size === 0)
  }
}
