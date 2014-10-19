package no.mesan.testdatagen.generators.misc

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NamesSpec extends FlatSpec {

  trait Setup {
    val xgen= Names(3)
  }

  "The names generator" should "generate expected contents" in {
      new Setup {
        val res= xgen.get(20)
        for (s<-res) assert(s.matches("[A-Z][a-z-\\']+ [A-Z][a-z-\\']+ [A-Z][a-z-\\']+"), res)
      }
  }
}