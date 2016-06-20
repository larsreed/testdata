package no.mesan.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.language.{existentials, postfixOps}
import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import java.util.Date

@RunWith(classOf[JUnitRunner])
class DatesSpec extends FlatSpec {

  "The Dates generator" should "validate its from/to-arguments" in {
    intercept[IllegalArgumentException] {
      (Dates() from(y = 2012, m = 12, d = 12) to(y = 2012, m = 9, d = 20)) get 1
    }
  }

  it must "have a step size !=0 for sequential generation" in {
    intercept[IllegalArgumentException] {
      Dates().step().sequential.get(1)
    }
    intercept[IllegalArgumentException] {
      Dates().step(0, 0, 0, 0, 0, 0, 0).sequential.get(1)
    }
  }

  it should "generate a straight sequence of days when requested" in {
    val gen = (Dates() from(y = 2012, m = 10, d = 10) to(y = 2012, m = 10, d = 30)) sequential
    val res= gen get 5
    for (i <- 10 to 14) assert(res.contains(new DateTime(2012, 10, i, 0, 0)))
  }

  it should "generate a straight sequence of seconds when requested" in {
    val res = Dates().timeOnly.sequential get 5
    for (i <- 0 to 4)
      assert(res(i).getMinuteOfDay == 0
          && res(i).getHourOfDay == 0
          && res(i).getSecondOfDay == i,
        i)
  }

  it should "generate a straight sequence of dates at midnight when requested" in {
    val res = Dates().dateAndTime.from(y = 2012, m = 1, d = 1).sequential.get(30)
    val d1 = new DateTime(2012, 1, 1, 0, 0)
    val d2 = new DateTime(2012, 1, 2, 0, 0)
    for (i <- 0 to 23) assert(res.contains(new DateTime(d1.getYear, d1.getMonthOfYear,
      d1.getDayOfMonth, i, 0, 0, 0)), i + " 1")
    for (i <- 0 to 5) assert(res.contains(new DateTime(d2.getYear, d2.getMonthOfYear,
      d2.getDayOfMonth, i, 0, 0, 0)), i + " 2")
  }

  it should "repeat from start when limit is reached" in {
    val genr = Dates() from(y = 1900, m = 1, d = 1) to(y = 1900, m = 1, d = 3) sequential
    val res= genr.get(6).toSet
    assert(res.size === 3)
    val r2 = (Dates() from(y = 4040, m = 12, d = 1) to(y = 4040, m = 12, d = 4) reversed()) get 40
    assert(r2.toSet.size === 4)
  }

  it should "deduce missing day from date limits" in {
    // With m=2, d defaults to 31 ... should become 29 for a leap year
    val g = Dates() from(y = 2004, m = 2) to(y = 2004, m = 2) reversed()
    val dt: DateTime = g.gen.head
    val (y, m, d) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth)
    assert(y === 2004 && m === 2 && d === 29, dt)
  }

  it should "generate random values within year limits" in {
    val res = (Dates() from(y = 2012) to(y = 2018)) get 42
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert((2012 to 2018) contains y, "y " + dt)
      assert((1 to 12) contains m, "m " + dt)
      assert((1 to 31) contains d, "d " + dt)
      assert(hh === 0 && mm === 0 &&  ss === 0 && ms === 0)
    }
  }

  it should "do so even with time included" in {
    val res = (Dates() from(y = 2012) to(y = 2018) dateAndTime) get 42
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert((2012 to 2018) contains y, "y " + dt)
      assert((1 to 12) contains m, "m " + dt)
      assert((1 to 31) contains d, "d " + dt)
      assert((0 to 23) contains hh, "hh " + dt)
      assert((0 to 59) contains mm, "mm " + dt)
      assert((0 to 59) contains ss, "ss " + dt)
      assert((0 to 999) contains ms, "ms " + dt)
    }
  }

  it should "generate random values within month limits" in {
    val res = (Dates() from(y = 2012, m = 4) to(y = 2012, m = 4)) get 42
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert(y === 2012 &&  m === 4, dt)
      assert((1 to 30) contains d, "d " + dt)
      assert(hh === 0 && mm === 0 && ss === 0 && ms === 0)
    }
  }

  it should "still handle time included" in {
    val g = Dates() from(y = 2012, m = 4, d = 8, hh = 21) to(y = 2012, m = 4, d = 8, hh = 23) dateAndTime
    val res= g get 42
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert(y === 2012 && m === 4 && d === 8)
      assert((21 to 23) contains hh, "hh " + dt)
      assert((0 to 59) contains mm, "mm " + dt)
      assert((0 to 59) contains ss, "ss " + dt)
      assert((0 to 999) contains ms, "ms " + dt)
    }
  }

  it should "use the specified date part for 'time only'" in {
    val res = (Dates() from(hh = 3) setStdDate(2012, 11, 10) timeOnly) get 42
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert(y === 2012 && m === 11 && d === 10)
      assert((3 to 23) contains hh, "hh " + dt)
      assert((0 to 59) contains mm, "mm " + dt)
      assert((0 to 59) contains ss, "ss " + dt)
      assert((0 to 999) contains ms, "ms " + dt)
    }
  }

  it should "even within a given minute" in {
    val res = (Dates() from(hh = 3, mm = 14) to(hh = 3, mm = 14) setStdDate(2012, 11, 10)
      timeOnly) get 42
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert(y === 2012 && m === 11 && d === 10 && hh === 3 && mm === 14)
      assert((0 to 59) contains ss, "ss " + dt)
      assert((0 to 999) contains ms, "ms " + dt)
    }
  }

  it should "use the specified format" in {
    val g= (Dates() from(y = 2012, m = 1, d = 1) step(hh = 4, mm = 30)
      format "yyyy.MM.dd HH:mm:ss"  dateAndTime).sequential.genStrings
    val res= g.take(10).toList
    val expect = List("2012.01.01 00:00:00", "2012.01.01 04:30:00", "2012.01.01 09:00:00", "2012.01.01 13:30:00",
      "2012.01.01 18:00:00", "2012.01.01 22:30:00", "2012.01.02 03:00:00", "2012.01.02 07:30:00",
      "2012.01.02 12:00:00", "2012.01.02 16:30:00")
    assert(res === expect)
  }

  it should "be able to generate unique dates" in {
    val gen= Dates() from(y = 2012, m = 12, d = 1) to(y = 2012, m = 12, d = 30) format "d" distinct
    val res1= gen get 30
    val res2= gen getStrings 30
    for (i<-1 to 30) {
      assert(res1 contains new DateTime(2012,12,i,0,0,0,0), i)
      assert(res2 contains i+"", i)
    }
  }

  it should "be able to use and generate Java dates" in {
    val start= new Date()
    val end= new Date(start.getTime+100000000L)
    val res= (Dates() from start to end dateAndTime) getJavaDates 500
    assert(res.size==500)
    res.foreach { dt=> assert(dt.getTime >= start.getTime && dt.getTime <= end.getTime, dt) }
  }
}
