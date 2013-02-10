package net.kalars.testgen.generators.misc

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MailGeneratorSuite extends FunSuite {

  trait Setup {
    val xgen= MailGenerator()
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
        for (s<-res) assert(s.matches("^[a-z]+[.a-z]+[@][.a-z]+$"), s)
      }
  }

}
