package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PoststederSpec extends FlatSpec  {

  "The Poststeder generator" should "generate values like NNNN Xxxx" in  {
    val res = Poststeder().sequential.get(300)
    for (s<-res) assert(s.matches("^[0-9][0-9][0-9][0-9] .+"))
  }

  it should "generate numbers like NNNN" in {
    val res = Poststeder.postnr().get(300)
    for (s<-res) assert(s.matches("^[0-9][0-9][0-9][0-9]$"))
  }
}
