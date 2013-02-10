package net.kalars.testgen.generators.norway

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NorskeNavnGeneratorSuite extends FunSuite {

  trait Setup {
    val xgen= NorskeNavnGenerator(false)
  }

  print {
    println(NorskeNavnGenerator(false).get(50))
    println(NorskeNavnGenerator(true).get(50))
    println(NorskeNavnGenerator(true).kunFornavn.get(20))
    println(NorskeNavnGenerator(true).kunEtternavn.get(20))
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
