package no.mesan.testdatagen.generators

import scala.language.postfixOps

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.{Unique, ExtendedGenerator}


@RunWith(classOf[JUnitRunner])
class FromListSpec extends FlatSpec {

  "FromList" should "not accept from/to-limits" in {
    val l: ExtendedGenerator[Int] = FromList(List(1, 2, 3))
    intercept[UnsupportedOperationException] {
      l.from(1).get(1)
    }
    intercept[UnsupportedOperationException] {
      l.to(1).get(1)
    }
  }

  it should "not allow an empty input list" in {
    intercept[IllegalArgumentException] {
      val l: List[String] = Nil
      FromList(l).get(1)
    }
  }

  private def aStringGen(chrs: String): Strings = Strings() chars chrs lengthBetween(1, 2) sequential

  it should "act as an intermediary for other generators" in {
    val orig = aStringGen("ab") get 100
    val gen = FromList(orig).sequential
    assert(gen.get(6) === List("a", "b", "aa", "ab", "ba", "bb"))
  }


  it should "be able to reverse its input" in {
    assert(FromList(List("A", "B", "C").reverse).sequential.get(25) ===
      List("C", "B", "A", "C", "B", "A", "C", "B", "A", "C", "B", "A", "C", "B", "A",
        "C", "B", "A", "C", "B", "A", "C", "B", "A", "C"))
  }

  it should "be generate short sequences" in {
    assert(FromList(List(1, 2)).sequential.get(2) === List(1, 2))
    assert(FromList(List(1)).sequential.get(2) === List(1, 1))
  }

  it should "generate random sequences" in {
    val orig = aStringGen("abc") get 100
    val gen = FromList(orig)
    val res = gen.get(25)
    assert(res.length === 25)
    assert(res.forall(s => (s.length == 1 || s.length == 2) && s.matches("^[a-c]*$")))
  }

  it should "allow filtering" in {
    val orig = aStringGen("abc") get 100
    val gen = FromList(orig).filter(s => !(s contains "b"))
    val res = gen.get(100)
    assert(res.forall(s => s.matches("^[ac]*$")))
  }

  it should "format results" in {
    val g = FromList(List(1, 2, 3, 4, 5, 6)).format("%04d").sequential
    assert(g.getStrings(4) === List("0001", "0002", "0003", "0004"))
  }

  it should "handle single element input list" in {
    val res = Unique(FromList(List("a"))).get(1).toSet
    assert(res.size == 1)
  }
}
