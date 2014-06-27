package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NorskeNavnSuite extends FunSuite  {

  trait Setup {
    val xgen= NorskeNavn()
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
        assert(xgen.get(300).size===300)
      }
  }

  ignore("contents") {
      new Setup {
        val res= xgen.get(120)
        for (s<-res) assert(s.matches("[A-Z][a-z-\\']+ [A-Z][a-z-\\']+ [A-Z][a-z-\\']+"), res)
      }
  }

}
