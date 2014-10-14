package no.mesan.testdatagen.recordgen

import no.mesan.testdatagen.generators.norway.{Fnr, NorskeNavn}
import no.mesan.testdatagen.generators.{Dates, FromList, Ints}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ToFileSpec extends FlatSpec {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val nameGen = NorskeNavn()
    val fnrGen = Fnr(FromList(dates).sequential)
    val recordGen = ToCsv(true).
      add("id", idGen).
      add("name", nameGen).
      add("fnr", fnrGen)
    val fName= "target/test.txt"
    val gen= recordGen.toFile(fName)
  }

  "The ToFile generator" should "only accept positive record counts" in {
    intercept[IllegalArgumentException] { new Setup { gen.get(-1) } }
    intercept[IllegalArgumentException] { new Setup { gen.getStrings(-1) } }
  }

  it should "require at least one generator" in {
    intercept[IllegalArgumentException] { ToCsv(true).toFile("test.txt").get(1) }
  }
}
