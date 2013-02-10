package net.kalars.testgen.generators.norway

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

import net.kalars.testgen.generators.Dates

@RunWith(classOf[JUnitRunner])
class FnrGeneratorSuite extends FunSuite {

  def sjekkFnr(fnr: String) = {
    def sjekk(fnr: List[Int], fakt: List[Int], sum: Int): Boolean =
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

  print {
    val dg = Dates().from(y = 1968, m = 9, d = 20).to(y = 1968, m = 9, d = 20)
    println(FnrGenerator(dg).withDnr.boysOnly.get(1200))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      FnrGenerator().get(-1)
    }
    intercept[IllegalArgumentException] {
      FnrGenerator().getStrings(-1)
    }
  }

  test("count") {
    assert(FnrGenerator().get(120).size === 120)
  }

  ignore("contents") {
    val res= FnrGenerator().boysOnly.withDnr.get(100) ++
             FnrGenerator().girlsOnly.get(100) ++
             FnrGenerator().get(100)
    for (fnr<-res) assert(sjekkFnr(fnr), fnr)
  }

  test("empty") {
    assert(FnrGenerator().get(0).size === 0)
  }
}
