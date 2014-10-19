package no.mesan.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class KjennemerkerSpec extends FlatSpec {

    "The Kjennerker generator" should "produce correct contents" in {
      assert(Kjennemerker().get(120).forall(km=>
        km.length==7 &&
        km.substring(0, 2).matches("[A-Z][A-Z]") &&
        km.substring(2).matches("[0-9]+")))
    }
}
