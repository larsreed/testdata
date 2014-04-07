package no.mesan.testdatagen.generators.sample

import no.mesan.testdatagen.Reverse
import no.mesan.testdatagen.generators.{Chars, FromList}
import org.scalatest.FlatSpec

/**
 * Misc. tests for Reverse.
 */
class ReverseSpec extends FlatSpec {
  "Reverse" should "reverse itself" in {
    assert(List(1,2,3)===Reverse(Reverse(FromList(1, 2, 3, 4).sequential)).get(3))
  }

  it should "work for misc generators" in {
    assert(Reverse(Chars().chars("abc").sequential).getStrings(5) === List("b", "a", "c", "b", "a"))
  }

  "Unique is leaving the party, but in the meantime it" should "make things unique" in {

  }
}
