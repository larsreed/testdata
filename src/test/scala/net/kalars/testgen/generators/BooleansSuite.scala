package net.kalars.testgen.generators

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BooleansSuite extends FunSuite {

    print {
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
    assert(Booleans().reversed.get(5)===List(true, false, true, false, true))
  }

  test("only false by filter") {
    assert(Booleans().sequential.filter(t=> !t).get(3)===List(false, false, false))
  }

  test("0 sequential elements") {
    assert(Booleans().reversed.get(0)===List())
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
    val  res= Booleans().unique.get(2)
    assert(res== List(false, true) || res==List(true, false))
  }
}
