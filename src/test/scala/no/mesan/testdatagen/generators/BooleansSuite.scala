package no.mesan.testdatagen.generators

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.{Reverse, Unique, Printer}

@RunWith(classOf[JUnitRunner])
class BooleansSuite extends FunSuite with Printer {

    print(false) {
      println(Booleans().get(30))
    }

  test("negative get") {
    intercept[IllegalArgumentException] {
      Booleans().get(-1)
    }
    intercept[IllegalArgumentException] {
      Booleans().getStrings(-1)
    }
  }

  test("normal sequence") {
    assert(Booleans().sequential.get(5)===List(false, true, false, true, false))
  }

  test("reverted sequence") {
    assert(Reverse(Booleans().sequential).get(4)===List(true, false, true, false))
  }

  test("only false by filter") {
    assert(Booleans().sequential.filter(t=> !t).get(3)===List(false, false, false))
  }

  test("0 sequential elements") {
    assert(Reverse(Booleans()).get(0)===List())
  }

  test("0 random elements") {
    assert(Booleans().get(0)===List())
  }

  test("normal random") {
    val res= Booleans().get(250)
    assert(res.length===250)
    assert(res contains true)
    assert(res contains false)
  }

  test("default formatting") {
    val  res= Booleans().sequential.getStrings(4)
    assert(res=== List("false", "true", "false", "true"))
  }

  test("special formatting") {
    val  res= Booleans().format("0", "1").sequential.getStrings(4)
    assert(res=== List("0", "1", "0", "1"))
  }

  test("unique list") {
    val  res= Unique(Booleans()).get(2)
    assert(res== List(false, true) || res==List(true, false))
  }
}
