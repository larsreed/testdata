package no.mesan.testdatagen.generators

import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import java.util.Date

import no.mesan.testdatagen.{Unique, Printer}

@RunWith(classOf[JUnitRunner])
class DatesSuite extends FunSuite with Printer {

  test("print") {
    Dates().from(y = 2004, m = 2).to(y = 2004, m = 2).get(1)
    // println(Dates().from(m=1, d=1).to(m=1, d=3).sequential.get(6))
    // println(Dates().from(2012,10,10).to(2012,10,30).reversed.get(5))
    // println(Dates().sequential.get(5))
    // println(Dates().dateAndTime.from(2012,1,1,0,0,0,0).step(new Period(1,2,3,4,5,6,7,8)).sequential.get(30))
    // println(Dates().from(y=2012).to(y=2013).dateAndTime.get(400))
  }

  test("from<to") {
    intercept[IllegalArgumentException] {
      Dates().from(y = 2012, m = 12, d = 12).to(y = 2012, m = 9, d = 20).get(1)
    }
  }

  test("sequential not step 0") {
    intercept[IllegalArgumentException] {
      Dates().step().sequential.get(1)
    }
    intercept[IllegalArgumentException] {
      Dates().step(0, 0, 0, 0, 0, 0, 0).sequential.get(1)
    }
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      Dates().get(-1)
    }
    intercept[IllegalArgumentException] {
      Dates().getStrings(-1)
    }
  }

  test("normal date sequence") {
    val res = Dates().from(y = 2012, m = 10, d = 10).to(y = 2012, m = 10, d = 30).sequential.get(5)
    for (i <- 10 to 14) assert(res.contains(new DateTime(2012, 10, i, 0, 0)))
  }

  test("reversed date sequence") {
    val res = Dates().from(y = 2012, m = 10, d = 10).to(y = 2012, m = 10, d = 30).reversed().get(10)
    for (i <- 30 to 21 by -1) assert(res.contains(new DateTime(2012, 10, i, 0, 0)))
  }

  test("normal time sequence") {
    val res = Dates().timeOnly.sequential.get(5)
    for (i <- 0 to 4) assert(res(i).getMinuteOfDay == 0 && res(i).getHourOfDay == 0 &&
      res(i).getSecondOfDay == i, i + "")
  }

  test("normal datetime sequence") {
    val res = Dates().dateAndTime.from(y = 2012, m = 1, d = 1).sequential.get(30)
    val d1 = new DateTime(2012, 1, 1, 0, 0)
    val d2 = new DateTime(2012, 1, 2, 0, 0)
    for (i <- 0 to 23) assert(res.contains(new DateTime(d1.getYear, d1.getMonthOfYear,
      d1.getDayOfMonth, i, 0, 0, 0)), i + " 1")
    for (i <- 0 to 5) assert(res.contains(new DateTime(d2.getYear, d2.getMonthOfYear,
      d2.getDayOfMonth, i, 0, 0, 0)), i + " 2")
  }

  test("sequence that passes limit repeats itself") {
    val res = Dates().from(y = 1900, m = 1, d = 1).to(y = 1900, m = 1, d = 3).sequential.get(6).toSet
    assert(res.size === 3)
    val res2 = Dates().from(y = 4040, m = 12, d = 1).to(y = 4040, m = 12, d = 4).reversed().get(40).toSet
    assert(res2.size === 4)
  }

  test("0 sequential elements") {
    assert(Dates().sequential.get(0) === List())
  }

  test("0 random elements") {
    assert(Dates().get(0) === List())
  }

  test("limits with underspecified date") {
    // With m=2, d defaults to 31 ... should become 29 for a leap year
    val dt: DateTime = Dates().from(y = 2004, m = 2).to(y = 2004, m = 2).reversed().get(1)(0)
    val (y, m, d) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth)
    assert(y === 2004)
    assert(m === 2)
    assert(d === 29)
  }

  test("normal random date") {
    val res = Dates().from(y = 2012).to(y = 2018).get(42)
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert((2012 to 2018).contains(y), "y " + dt)
      assert((1 to 12).contains(m), "m " + dt)
      assert((1 to 31).contains(d), "d " + dt)
      assert(hh === 0)
      assert(mm === 0)
      assert(ss === 0)
      assert(ms === 0)
    }
  }

  test("limited random date") {
    val res = Dates().from(y = 2012, m = 4).to(y = 2012, m = 4).get(42)
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert(y === 2012, "y " + dt)
      assert(m === 4, "m " + dt)
      assert((1 to 30).contains(d), "d " + dt)
      assert(hh === 0)
      assert(mm === 0)
      assert(ss === 0)
      assert(ms === 0)
    }
  }

  test("normal random datetime") {
    val res = Dates().from(y = 2012).to(y = 2018).dateAndTime.get(42)
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert((2012 to 2018).contains(y), "y " + dt)
      assert((1 to 12).contains(m), "m " + dt)
      assert((1 to 31).contains(d), "d " + dt)
      assert((0 to 23).contains(hh), "hh " + dt)
      assert((0 to 59).contains(mm), "mm " + dt)
      assert((0 to 59).contains(ss), "ss " + dt)
      assert((0 to 999).contains(ms), "ms " + dt)
    }
  }

  test("limited random datetime") {
    val res = Dates().from(y = 2012, m = 4, d = 8, hh = 21).to(y = 2012, m = 4, d = 8, hh = 23).dateAndTime.get(42)
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert(y === 2012)
      assert(m === 4)
      assert(d === 8)
      assert((21 to 23).contains(hh), "hh " + dt)
      assert((0 to 59).contains(mm), "mm " + dt)
      assert((0 to 59).contains(ss), "ss " + dt)
      assert((0 to 999).contains(ms), "ms " + dt)
    }
  }

  test("normal random time") {
    val res = Dates().from(hh = 3).setStdDate(2012, 11, 10).timeOnly.get(42)
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert(y === 2012)
      assert(m === 11)
      assert(d === 10)
      assert((3 to 23).contains(hh), "hh " + dt)
      assert((0 to 59).contains(mm), "mm " + dt)
      assert((0 to 59).contains(ss), "ss " + dt)
      assert((0 to 999).contains(ms), "ms " + dt)
    }
  }

  test("limited random time") {
    val res = Dates().from(hh = 3, mm = 14).to(hh = 3, mm = 14).setStdDate(2012, 11, 10).timeOnly.get(42)
    assert(res.length === 42)
    for (dt <- res) {
      val (y, m, d, hh, mm, ss, ms) = (dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth,
        dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute, dt.getMillisOfSecond)
      assert(y === 2012)
      assert(m === 11)
      assert(d === 10)
      assert(hh === 3)
      assert(mm === 14)
      assert((0 to 59).contains(ss), "ss " + dt)
      assert((0 to 999).contains(ms), "ms " + dt)
    }
  }

  test("formatting") {
    val res = Dates()
              .dateAndTime
              .from(y = 2012, m = 1, d = 1)
              .step(hh = 4, mm = 30)
              .format("yyyy.MM.dd HH:mm:ss")
              .sequential
              .getStrings(10)
    val expect = List("2012.01.01 00:00:00", "2012.01.01 04:30:00", "2012.01.01 09:00:00", "2012.01.01 13:30:00",
      "2012.01.01 18:00:00", "2012.01.01 22:30:00", "2012.01.02 03:00:00", "2012.01.02 07:30:00",
      "2012.01.02 12:00:00", "2012.01.02 16:30:00")
    assert(res === expect)
  }

  test("unique list") {
    val  res= Unique(Dates().from(y = 2012, m=12, d=1).to(y = 2012, m=12, d=30)).get(30)
    for (i<-1 to 30) assert(res contains new DateTime(2012,12,i,0,0,0,0), i)
  }

  test("unique string list") {
    val  res= Unique(Dates().from(y = 2012, m=12, d=1).to(y = 2012, m=12, d=30).format("d")).getStrings(30)
    for (i<-1 to 30) assert(res contains i+"", i)
  }

  test("unique long list") {
    val res= Unique(Dates().from(y=2000).to(y=2010)).get(500).toSet
    assert(res.size==500)
  }

  test("java.util.Date") {
    val start= new Date()
    val end= new Date(start.getTime+100000000L)
    val res= Dates().from(start).to(end).dateAndTime.getJavaDates(500)
    res.foreach { dt=> assert(dt.getTime >= start.getTime && dt.getTime <= end.getTime, dt) }
    assert(res.size==500)
  }
}
