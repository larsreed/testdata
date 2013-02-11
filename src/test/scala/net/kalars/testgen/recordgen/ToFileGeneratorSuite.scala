package net.kalars.testgen.recordgen

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner
import net.kalars.testgen.aggreg.SomeNulls
import net.kalars.testgen.generators.{Booleans, Dates, Ints, FromList, Strings}
import net.kalars.testgen.generators.misc.MailAddresses
import net.kalars.testgen.generators.norway.Fnr
import net.kalars.testgen.generators.norway.NorskeNavn

@RunWith(classOf[JUnitRunner])
class ToFileGeneratorSuite extends FunSuite {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val nameGen = NorskeNavnGenerator()
    val fnrGen = FnrGenerator(ListGenerator(dates).sequential)
    val recordGen = CsvGenerator(true).
      add("id", idGen).
      add("name", nameGen).
      add("fnr", fnrGen)
    val gen= recordGen.toFile("target/test.txt")
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      new Setup {
        gen.get(-1)
      }
    }
    intercept[IllegalArgumentException] {
      new Setup {
        gen.getStrings(-1)
      }
    }
  }

  test("needs one generator") {
    intercept[IllegalArgumentException] {
      CsvGenerator(true).toFile("test.txt").get(1)
    }
  }

  test("print") {
    new Setup {
      gen.get(100)
    }
  }
}
