package net.kalars.testgen.generators.misc

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import net.kalars.testgen.FunSuite

@RunWith(classOf[JUnitRunner])
class UrlsSuite extends FunSuite {

  trait Setup {
    val xgen= Urls()
  }

  print {
    new Setup {
      println(xgen.get(120))
    }
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      new Setup {
        xgen.get(-1)
      }
    }
    intercept[IllegalArgumentException] {
      new Setup {
        xgen.getStrings(-1)
      }
    }
  }

  test("count") {
      new Setup {
        assert(xgen.get(30).size===30)
      }
  }

  test("contents") {
      new Setup {
        val res= xgen.get(20)
        for (s<-res) assert(s.matches("^http(s)?:.*[.].+$"), s)
      }
  }

}
