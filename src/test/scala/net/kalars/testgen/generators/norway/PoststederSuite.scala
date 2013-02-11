package net.kalars.testgen.generators.norway

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PoststederSuite extends FunSuite {

  print {
    println(Poststeder().get(120))
    println(Poststeder.postnr().get(120))
    println(Poststeder.poststed().get(120))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      Poststeder(false).get(-1)
    }
    intercept[IllegalArgumentException] {
      Poststeder(false).getStrings(-1)
    }
  }

  test("count") {
    assert(Poststeder(true).get(30).size === 30)
  }

  test("contents") {
    val res = Poststeder().sequential.get(300)
    for (s<-res) assert(s.matches("^[0-9][0-9][0-9][0-9] .+"))
  }

  test("postnummer") {
    val res = Poststeder.postnr().get(300)
    for (s<-res) assert(s.matches("^[0-9][0-9][0-9][0-9]$"))
  }
}
