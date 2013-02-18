package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class NorskeNavnSuite extends FunSuite with Printer {

  trait Setup {
    val xgen= NorskeNavn(false)
  }

  print(false) {
    println(NorskeNavn(false).get(50))
    println(NorskeNavn(true).get(50))
    println(NorskeNavn(true).kunFornavn.get(20))
    println(NorskeNavn(true).kunEtternavn.get(20))
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
