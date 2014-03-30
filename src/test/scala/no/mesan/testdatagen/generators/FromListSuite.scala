package no.mesan.testdatagen.generators

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.{Unique, ExtendedGenerator, Printer}


@RunWith(classOf[JUnitRunner])
class FromListSuite extends FunSuite with Printer {
  print(false) {
    println(FromList(List(1, 2, 3, 4, 5, 6)).get(25))
    println(FromList(List("A", "B", "C")).reversed.get(25))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      FromList(List(1, 2, 3)).get(-1)
    }
    intercept[IllegalArgumentException] {
      FromList(List(1, 2, 3)).getStrings(-1)
    }
  }

  test("from/to not suported") {
    val l: ExtendedGenerator[Int] = FromList(List(1, 2, 3))
    intercept[UnsupportedOperationException] {
      l.from(1).get(1)
    }
    intercept[UnsupportedOperationException] {
      l.to(1).get(1)
    }
  }

  test("cannot get from empty list") {
    intercept[IllegalArgumentException] {
      val l: List[String] = Nil
      FromList(l).get(1)
    }
  }

  test("use as keeper") {
    val orig = Strings().chars("ab").lengthBetween(1, 2).sequential.get(100)
    val gen = FromList(orig).sequential
    assert(gen.get(6) === List("a", "b", "aa", "ab", "ba", "bb"))
  }

  test("reverted sequence") {
    assert(FromList(List("A", "B", "C")).reversed.get(25) ===
      List("C", "B", "A", "C", "B", "A", "C", "B", "A", "C", "B", "A", "C", "B", "A",
        "C", "B", "A", "C", "B", "A", "C", "B", "A", "C"))
  }

  test("sequence of 1 & 2") {
    assert(FromList(List(1, 2)).sequential.get(2) === List(1, 2))
    assert(FromList(List(1)).sequential.get(2) === List(1, 1))
  }

  test("0 sequential elements") {
    assert(FromList(List(1, 2, 3)).sequential.get(0) === List())
  }

  test("0 random elements") {
    assert(FromList(List(1, 2, 3)).get(0) === List())
  }

  test("normal random") {
    val orig = Strings().chars("abc").lengthBetween(1, 2).sequential.get(100)
    val gen = FromList(orig)
    val res = gen.get(25)
    assert(res.length === 25)
    assert(res.forall(s => (s.length == 1 || s.length == 2) && s.matches("^[a-c]*$")))
  }

  test("filter 1") {
    val orig = Strings().chars("abc").lengthBetween(1, 2).sequential.get(100)
    val gen = FromList(orig).filter(s => !(s contains "b"))
    val res = gen.get(100)
    assert(res.forall(s => s.matches("^[ac]*$")))
  }

  test("formatting") {
    assert(FromList(List(1, 2, 3, 4, 5, 6)).format("%04d").sequential.getStrings(4) ===
      List("0001", "0002", "0003", "0004"))
  }

  test("unique list") {
    val res = Unique(FromList(('a' to 'z').toList)).getStrings(26).toSet
    assert(res.size == 26)
  }

  test("unique string list") {
    val res = Unique(FromList((900 to 999).toList)).getStrings(100).toSet
    assert(res.size == 100)
  }

  test("short list") {
    val res = Unique(FromList(List("a"))).get(1).toSet
    assert(res.size == 1)
  }
}
