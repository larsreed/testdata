package no.netcompany.testdatagen.dsl

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class DslLikeSyntaxSpec extends FlatSpec with DslLikeSyntax {

  "The positive method" should "only generate positive values" in {
    val i= (positive integers) get 200
    val l= (positive longs) get 200
    val d= (positive doubles) get 200
    for (round <- 0 to 199) {
      assert(i(round)>0)
      assert(l(round)>0)
      assert(d(round)>0)
    }
  }

  "The negative method" should "only generate negative values" in {
    val i= (negative integers) get 200
    val l= (negative longs) get 200
    val d= (negative doubles) get 200
    for (round <- 0 to 199) {
      assert(i(round)<0)
      assert(l(round)<0)
      assert(d(round)<0)
    }
  }

  "The sequential method" should "generate sequential values" in {
    val i= (sequential integers) get 200
    val l= (sequential longs) get 200
    val d= (sequential dates) get 200
    for (round <- 0 to 199) {
      assert(i(round)===round+1)
      assert(l(round)===round+1)
      assert(d(round)=== DateTime.now.plusDays(round).withTimeAtStartOfDay)
    }
  }

  "The randomStrings method" should "generate strings with length between 1 and 24" in {
    val ss= randomStrings get 200
    for (s <- ss) assert(s.length>=1 && s.length <=24)
  }

  "The futureDates method" should "generate dates in the future" in {
    val fd= futureDates get 300
    for (d <- fd) assert(d.isAfter(DateTime.now))
  }

  "The previousDates method" should "generate dates in the past" in {
    val fd= previousDates get 300
    for (d <- fd) assert(d.isBefore(DateTime.now))
  }
}
