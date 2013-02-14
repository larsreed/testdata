package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.FunSuite

@RunWith(classOf[JUnitRunner])
class KjennemerkerSuite extends FunSuite {
    print {
       println(Kjennemerker().get(120))
    }

  test("negative get") {
      intercept[IllegalArgumentException] {
        Kjennemerker().get(-1)
      }
      intercept[IllegalArgumentException] {
        Kjennemerker().getStrings(-1)
      }
  }

    test("count") {
      assert(Kjennemerker().get(120).size===120)
    }

    test("contents") {
      assert(Kjennemerker().get(120).forall(km=>
        km.length==7 &&
        km.substring(0, 2).matches("[A-Z][A-Z]") &&
        km.substring(2).matches("[0-9]+")))
    }

    test("empty") {
      assert(Kjennemerker().get(0).size===0)
    }
}
