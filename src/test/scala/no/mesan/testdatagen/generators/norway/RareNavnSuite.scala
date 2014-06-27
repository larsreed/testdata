package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RareNavnSuite extends FunSuite  {

  test("negative get") {
    intercept[IllegalArgumentException] {
      RareNavn().get(-1)
    }
    intercept[IllegalArgumentException] {
      RareNavn().getStrings(-1)
    }
  }

  test("count") {
    assert(RareNavn().get(30).size === 30)
  }

  test("contents") {
    val res = RareNavn().sequential.get(300)
    assert(res.contains("Buster Minal"))
    val res2 = RareNavn().sequential.getStrings(300)
    assert(res2.contains("Buster Minal"))
  }
}
