package no.mesan.testdatagen.generators

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class StringsSuite extends FunSuite with Printer {
    print(false) {
       println(Strings().lengthBetween(1,3).chars('a' to 'c').reversed.get(120))
       println(Strings().lengthBetween(1,3).chars('a' to 'c').sequential.get(100))
       println(Strings().lengthBetween(1000,1003).chars('0' to '1').sequential.get(2))
       println(Strings().lengthBetween(1000,1003).chars(' ' to 'Z').reversed.get(2))
       println(Strings().chars("abx").lengthBetween(1, 2).sequential.get(25))
       println(Strings().chars("abx").lengthBetween(1, 2).reversed.get(25))
       println(Strings().chars("abx").lengthBetween(2, 3).sequential.get(25))
       println(Strings().chars("abx").lengthBetween(2, 3).get(25))
    }

    test("from<=to") {
      intercept[IllegalArgumentException] {
        Strings().from("Z").to("A").get(1)
      }
  }

  test("negative get") {
      intercept[IllegalArgumentException] {
        Strings().get(-1)
      }
      intercept[IllegalArgumentException] {
        Strings().getStrings(-1)
      }
  }
    test("no negative length") {
      intercept[IllegalArgumentException] {
        Strings().lengthBetween(-1,1)
      }
      intercept[IllegalArgumentException] {
        Strings().length(-1)
      }
  }

  test("from<=to for lengths") {
      intercept[IllegalArgumentException] {
        Strings().lengthBetween(5,4)
      }
  }

  test("normal sequence") {
    val res= Strings().chars("ab").lengthBetween(1, 2).sequential.get(1000)
    assert(res.length===1000)
    val exp= List("a", "b", "aa", "ab", "ba", "bb")
    exp.foreach(s=> assert(res contains s, s))
  }

  test("normal sequence 2") {
    val res= Set() ++ Strings().chars("abx").lengthBetween(1, 2).sequential.get(5000)
    val expect=Set() ++ List("a", "b", "x", "aa", "ab", "ax", "ba", "bb", "bx", "xa", "xb", "xx")
    assert(res===expect)
    assert(res.size===expect.size, expect)
  }

  test("reverted sequence") {
    assert(Strings().chars("abc").length(1).reversed.get(5)===List("c", "b", "a", "c", "b"))
  }

  test("sequence of 1 & 2") {
    assert(Strings().chars('a' to 'z').length(1).sequential.get(2)===List("a", "b"))
    assert(Strings().chars("a").length(1).sequential.get(2)===List("a", "a"))
  }

  test("0 sequential elements") {
    assert(Strings().sequential.from("a").to("z").get(0)===List())
  }

  test("0 random elements") {
    assert(Strings().from("a").to("z").get(0)===List())
  }

  test("normal random") {
    val res= Strings().length(9).chars(' ' to 'Z').get(25)
    assert(res.length===25)
    assert(res.forall(s=> s.length==9 && s>=" " && s<="ZZZZZZZZZ"))
  }

  test("normal random 2") {
    val res= Set() ++ Strings().chars("abx").lengthBetween(1, 2).get(5000)
    val expect=Set() ++ List("a", "b", "x", "aa", "ab", "ax", "ba", "bb", "bx", "xa", "xb", "xx")
    assert(res===expect)
  }

  test("filter 1") {
    val res= Strings().length(4).chars('a' to 'z').filter(s=>s contains "e").get(10)
    assert(res.length===10)
    assert(res.forall(s=> s contains "e"), res)
  }

  test("filter 2") {
    val res= Strings().length(4).chars('a' to 'z').sequential.filter(s=>s contains "e").get(10)
    assert(res.length===10)
    assert(res.forall(s=> s contains "e"), res)
  }

  test("impossible filter 2") {
    val res= Strings().chars("ABCDEF").lengthBetween(2, 3).sequential.filter(s=>false).get(10)
    assert(res.length===0)
  }

  test("formatting") {
    val  res= Strings().length(2).chars("ABC").sequential.formatWith(i=>f"$i%7s").getStrings(3)
    assert(res=== List("     AA", "     AB", "     AC"))
    val  res3= Strings().length(2).chars("AB").sequential.format("%-5s").getStrings(3)
    assert(res3=== List("AA   ", "AB   ", "BA   "))
    val  res2= Strings().from("A").to("B").sequential.format("%7s").getStrings(0)
    assert(res2=== Nil)
  }
  test("unique list") {
    val  res= Strings().chars("ABC").length(2).unique.get(9).toSet
    assert(res.size===9)
  }
}
