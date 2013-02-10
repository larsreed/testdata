package net.kalars.testgen.generators.norway

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PoststedGeneratorSuite extends FunSuite {

  print {
    println(PoststedGenerator().get(120))
    println(PoststedGenerator.postnr().get(120))
    println(PoststedGenerator.poststed().get(120))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      PoststedGenerator(false).get(-1)
    }
    intercept[IllegalArgumentException] {
      PoststedGenerator(false).getStrings(-1)
    }
  }

  test("count") {
    assert(PoststedGenerator(true).get(30).size === 30)
  }

  test("contents") {
    val res = PoststedGenerator().sequential.get(300)
    for (s<-res) assert(s.matches("^[0-9][0-9][0-9][0-9] .+"))
  }

  test("postnummer") {
    val res = PoststedGenerator.postnr().get(300)
    for (s<-res) assert(s.matches("^[0-9][0-9][0-9][0-9]$"))
  }
}
