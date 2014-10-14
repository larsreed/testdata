package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AdresserSpec extends FlatSpec {

  "The Adresser generator" should "generate right contents with number" in {
    val res= Adresser().get(111)
    res.foreach{
      adr => assert(adr.matches("^.*[0-9]+[A-F]?$"), adr)
    }
  }

  it should "generate right contents without number" in {
    val res= Adresser(false).get(111)
    res.foreach{
      adr => assert(adr.matches("^.*$"), adr)
    }
  }
}