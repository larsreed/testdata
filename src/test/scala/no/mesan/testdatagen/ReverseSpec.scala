package no.mesan.testdatagen

import no.mesan.testdatagen.generators.{Dates, Booleans, Chars, FromList}
import org.scalatest.FlatSpec
import org.joda.time.DateTime

/**
 * Misc. tests for Reverse.
 */
class ReverseSpec extends FlatSpec {
  "Reverse" should "reverse itself" in {
    assert(List(1,2,3)===Reverse(Reverse(FromList(1, 2, 3, 4).sequential)).get(3))
  }

  it should "work for date generators" in {
    val res = Dates().from(y = 2012, m = 10, d = 10).to(y = 2012, m = 10, d = 30).reversed().get(10)
    for (i <- 30 to 21 by -1) assert(res.contains(new DateTime(2012, 10, i, 0, 0)))
  }

  it should "work for misc generators" in {
    assert(Reverse(Chars().chars("abc").sequential).getStrings(5) === List("b", "a", "c", "b", "a"))
    assert(Reverse(Booleans().sequential).get(4)===List(true, false, true, false))
  }

  "Unique is leaving the party, but in the meantime it" should "make things unique" in {
    true
  }
}
