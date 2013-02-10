package net.kalars.testgen.generators.norway

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class KjennemerkeSuite extends FunSuite {
    print {
       println(KjennemerkeGenerator().get(120))
    }

  test("negative get") {
      intercept[IllegalArgumentException] {
        KjennemerkeGenerator().get(-1)
      }
      intercept[IllegalArgumentException] {
        KjennemerkeGenerator().getStrings(-1)
      }
  }

    test("count") {
      assert(KjennemerkeGenerator().get(120).size===120)
    }

    test("contents") {
      assert(KjennemerkeGenerator().get(120).forall(km=>
        km.length==7 &&
        km.substring(0, 2).matches("[A-Z][A-Z]") &&
        km.substring(2).matches("[0-9]+")))
    }

    test("empty") {
      assert(KjennemerkeGenerator().get(0).size===0)
    }
}
