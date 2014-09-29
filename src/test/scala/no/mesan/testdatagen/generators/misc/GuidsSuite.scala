package no.mesan.testdatagen.generators.misc

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GuidsSuite extends FlatSpec {

  "The Guids" should "produce correctly formatted strings" in {
    Guids().getStrings(120).foreach(s =>
      println(s))
    Guids().getStrings(120).foreach(s =>
      assert(s.matches("^[0-9a-f]{8}-([0-9a-f]{4}-){2}[0-9a-f]{16}$"), s))
  }
}
