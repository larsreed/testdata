package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NorskeNavnSpec extends FlatSpec  {

  trait Setup {
    val xgen= NorskeNavn()
  }

  ignore should "produce correct contents" in {
      new Setup {
        val res= xgen.get(120)
        for (s<-res) assert(s.matches("[A-Z][a-z-\\']+ [A-Z][a-z-\\']+ [A-Z][a-z-\\']+"), res)
      }
  }
}
