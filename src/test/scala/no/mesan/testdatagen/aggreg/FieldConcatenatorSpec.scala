package no.mesan.testdatagen.aggreg

import no.mesan.testdatagen.generators.{Booleans, Chars, FromList}

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FieldConcatenatorSpec extends FlatSpec {

  trait Setup {
    val xgen= FieldConcatenator().add(FromList(1)).
                                  add(Chars("a")).
                                  add(Booleans())
  }

  private def isMatch(s: String): Boolean = (s equals "1atrue") || (s equals "1afalse")

  "A FieldConcatenator" should "generate expected output" in  {
    new Setup {
      val res= xgen.get(1000)
      res foreach { s => assert(isMatch(s)) }
    }
  }

   it should "format contents properly" in {
    new Setup {
      val res= xgen.getStrings(1000)
      res foreach { s => assert(isMatch(s)) }
    }
  }
}
