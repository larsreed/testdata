package net.kalars.testgen.generators.norway

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class KommuneGeneratorSuite extends FunSuite {

  print {
    println(KommuneGenerator().get(120))
    println(KommuneGenerator.kommunenr().get(120))
    println(KommuneGenerator.kommunenavn().get(120))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      KommuneGenerator(false).get(-1)
    }
    intercept[IllegalArgumentException] {
      KommuneGenerator(false).getStrings(-1)
    }
  }

  test("count") {
    assert(KommuneGenerator(true).get(30).size === 30)
  }

  test("contents") {
    val res = KommuneGenerator().sequential.get(300)
    for (s<-res) assert(s.matches("^[0-9][0-9][0-9][0-9] .+"))
  }

  test("kommunenr") {
    val res = KommuneGenerator.kommunenr().get(300)
    for (s<-res) assert(s.matches("^[0-9][0-9][0-9][0-9]$"))
  }

}
