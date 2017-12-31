package no.netcompany.testdatagen.utils

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PercentageSpec extends FlatSpec with Percentage {

  "The Percentage trait" should "always hit when pct is 100" in {
    for (i<-0 to 5000) assert(hit(100))
  }

  it should "never hit when pct is 0" in {
    for (i<-0 to 5000) assert(!hit(0))
  }

  it should "hit within approximate limits" in {
    var count=0
    for (i<-0 to 15000) if (hit(50)) count+=1
    assert(count>6000, "count=" + count)
    assert(count<9000, "count=" + count)
  }
}
