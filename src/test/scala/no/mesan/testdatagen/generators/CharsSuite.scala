package no.mesan.testdatagen.generators

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scala.language.postfixOps

import no.mesan.testdatagen.{Reverse, Printer}

@RunWith(classOf[JUnitRunner])
class CharsSuite extends FunSuite {

  test("from<=to") {
    intercept[IllegalArgumentException] {
      Chars().from('z').to('a').get(1)
    }
  }

  test("normal sequence") {
    val res = Chars().chars("ab").sequential.get(1000)
    assert(res.length === 1000)
    val exp = List('a', 'b')
    exp.foreach(s => assert(res contains s, s))
  }

  test("normal sequence 2") {
    val res = Chars().chars("abx").sequential.get(5000).toSet
    val expect = Set('a', 'b', 'x')
    assert(res === expect)
    assert(res.size === expect.size, expect)
  }

  test("sequence of 1 & 2") {
    assert(Chars().chars('a' to 'z').sequential.get(2) === List('a', 'b'))
    assert(Chars().chars("a").sequential.get(2) === List('a', 'a'))
  }

  test("0 sequential elements") {
    assert(Chars().sequential.get(0) === List())
  }

  test("0 random elements") {
    assert(Chars().get(0) === List())
  }

  test("normal random") {
    val res = Chars().chars(' ' to 'Z').get(25)
    assert(res.length === 25)
    assert(res.forall(s=>s >= ' ' && s <= 'Z'))
  }

  test("filter") {
    val res = Chars().chars('a' to 'z').sequential.filter(c => "aeiouy" contains c).get(10)
    assert(res.length === 10)
    assert(res.forall(c=> (c=='a') || (c=='e') || (c=='i') || (c=='o') || (c=='u') || (c=='y')),
           res)
  }

  test("formatting") {
    val res = Chars().chars("ABC").sequential.formatWith(c => f"$c%7s").getStrings(3)
    assert(res === List("      A", "      B", "      C"))
    val res2 = Chars().sequential.format("%7s").getStrings(0)
    assert(res2 === Nil)
    val res3 = Chars().chars("AB").sequential.format("%-5s").getStrings(3)
    assert(res3 === List("A    ", "B    ", "A    "))
  }

  test("unique list") {
    val res = (Chars() chars "ABCDEF" distinct).get(6).toSet
    assert(res.size === 6)
  }
}
