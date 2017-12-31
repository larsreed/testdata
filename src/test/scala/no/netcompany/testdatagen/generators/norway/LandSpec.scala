package no.netcompany.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.language.postfixOps
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class LandSpec extends FlatSpec {

  "The Land generator" should "ultimately generate 'Norge'" in {
    val res= (Land() sequential) get 300
    assert(res.contains("Norge"))
  }
}
