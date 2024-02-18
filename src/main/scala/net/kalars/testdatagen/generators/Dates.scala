package net.kalars.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import java.util.Date

import com.github.nscala_time.time.Imports._
import net.kalars.testdatagen.ExtendedImpl
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.joda.time.{DateTime, Period}

import scala.annotation.tailrec
import scala.language.postfixOps
import scala.util.Random

/**
 * Generate dates.
 * Default limits: 1 day sequential steps
 *                 dates between 1753.01.01 and 9999.12.31
 */
class Dates extends ExtendedImpl[DateTime] {

  from()
  to()
  filter(x=> lower match { case Some(low)=>  x>=low;  case _=> true })
  filter(x=> upper match { case Some(high)=> x<=high; case _=> true })

  private var stepPeriod: Option[Period]= None
  private var showDate= true
  private var showTime= false

  /** Show only time, set period to 1 second if sequential. */
  def timeOnly: this.type= {
    showTime= true
    showDate= false
    if (stepPeriod.isEmpty) stepInternal(ss=1)
    this
  }
  /** Show date and time, set period to 1 hour if sequential. */
  def dateAndTime: this.type= {
    showTime= true
    showDate= true
    if (stepPeriod.isEmpty) stepInternal(hh=1)
    this
  }

  private val now= new DateTime()
  private var stdYear= now.getYear
  private var stdMonth= now.getMonthOfYear
  private var stdDay= now.getDayOfMonth
  private var (stdHour, stdMin, stdSec, stdMilli)= (0, 0, 0, 0)
  private var isReversed= false

  /** Set default time for "date only". */
  def setStdTime(h: Int =0, min:Int = 0, s:Int = 0, ms:Int = 0):this.type = {
    stdHour= h
    stdMin= min
    stdSec= s
    stdMilli= ms
    this
  }

  /** Set default date for "time only". */
  def setStdDate(y: Int, m:Int, d:Int):this.type = {
    stdYear= y
    stdMonth= m
    stdDay= d
    this
  }

  /** From a java.util.date. */
  def from(d: Date): this.type= from(new DateTime(d))
  /** To a java.util.date. */
  def to(d: Date): this.type= to(new DateTime(d))

  /** From date+time -- "December 30th 1998" is given as (1998, 12, 30).*/
  def from(y: Int=1753, m: Int=1, d:Int=1,
           hh: Int=0, mm: Int=0, ss:Int=0, ms: Int=0):this.type =
    { from(new DateTime(y,m,d,hh,mm,ss,ms)); this }

  /** TO date+time -- "March 24th 2003" is given as (2003,03,24). */
  def to(y: Int=9999, m: Int=12, d:Int= -31,
           hh: Int=23, mm: Int=59, ss:Int=59, ms: Int=999):this.type = {
    if ( d<0 ) { // Must avoid situation where unspecified d needs a value less than 31
      val fixD= new DateTime(y,m,28,0,0,0,0).dayOfMonth().getMaximumValue
      to(new DateTime(y,m,fixD,hh,mm,ss,ms))
    }
    else to(new DateTime(y,m,d,hh,mm,ss,ms))
    this
  }

  private def stepInternal(y: Int=0, m: Int=0, d:Int=0,
                           hh: Int=0, mm: Int=0, ss:Int=0, ms:Int=0): this.type = {
    // The private method does not fiddle with the sequential setting
    require(y!=0 || m!=0 || d!=0 || hh!=0 || mm!=0 || ss!=0 || ms!=0,
        "at leat one step must be non-zero")
    stepPeriod= Some(new Period(y, m, 0, d, hh, mm, ss, ms))
    this
  }

  /** Steps for sequential dates. */
  def step(y: Int=0, m: Int=0, d:Int=0, hh: Int=0, mm: Int=0, ss:Int=0, ms:Int=0): this.type = {
    stepInternal(y, m, d, hh, mm, ss, ms)
    sequential
  }

  /** Steps for sequential dates givens as a Joda Period. */
  def step(p: Period): this.type = { stepPeriod= Some(p); sequential }

  /** Go sequential descending. */
  def reversed(rev: Boolean=true): this.type = { isReversed= rev; sequential }

  /* Format using a Joda-Time formatter. */
  def format(f: DateTimeFormatter): this.type=
    formatWith((dt:DateTime) => f.print(dt))

  /* Format using a Joda-Time format string. */
  override def format(f: String): this.type= format(DateTimeFormat.forPattern(f))

  def getStream: Stream[DateTime] = {
    val minOrg= lower.getOrElse(new DateTime)
    val maxOrg= upper.getOrElse(new DateTime)
    val min= if (!showTime)
                minOrg.withHourOfDay(stdHour).withMinuteOfHour(stdMin).withSecondOfMinute(stdSec)
                  .withMillisOfSecond(stdMilli)
             else if (!showDate)
                minOrg.withYear(stdYear).withMonthOfYear(stdMonth).withDayOfMonth(stdDay)
             else minOrg
    val max= if (!showTime)
                maxOrg.withHourOfDay(stdHour).withMinuteOfHour(stdMin).withSecondOfMinute(stdSec)
                  .withMillisOfSecond(stdMilli)
             else if (!showDate)
                maxOrg.withYear(stdYear).withMonthOfYear(stdMonth).withDayOfMonth(stdDay)
             else maxOrg
    require(showTime||showDate, "either date or time must be shown")
    require(min<=max, "from must not be after to")
    val period= stepPeriod.getOrElse(new Period().withDays(1)) multipliedBy(if (isReversed) -1 else 1)

    def next(curr: DateTime): Stream[DateTime]= {
      val nextVal= (if (curr > max) min else if (curr < min) max else curr) + period
      curr #:: next(nextVal)
    }

    def getRandomly: Stream[DateTime]= {
      @tailrec
      def getAdate: DateTime = {
        var hasVariance= false // true as soon as we different upper/lower limits
        var (y,m,d,hh,mm,ss,ms)= (stdYear, stdMonth, stdDay, stdHour, stdMin, stdSec, stdMilli)
        def setOne(v1: Int, v2:Int, startAt:Int, maxRand:Int): Int =
          if (hasVariance || v1!=v2){
            val v= if (hasVariance) startAt + Random.nextInt(maxRand)
                   else v1 + Random.nextInt(v2-v1+1)
            hasVariance= true
            v
          }
          else v1
        if (showDate) {
          y= setOne(min.getYear, max.getYear, 0, 0)
          m= setOne(min.getMonthOfYear, max.getMonthOfYear, 1, 12)
          d= setOne(min.getDayOfMonth, max.getDayOfMonth, 1, 31)
        }
        if (showTime) {
          hh= setOne(min.getHourOfDay, max.getHourOfDay, 0, 23)
          mm= setOne(min.getMinuteOfHour, max.getMinuteOfHour, 0, 59)
          ss= setOne(min.getSecondOfMinute, max.getSecondOfMinute, 0, 59)
          ms= setOne(min.getMillisOfSecond, max.getMillisOfSecond, 0, 999)
        }
        try {
          new DateTime(y,m,d,hh,mm,ss,ms)
        }
        catch { // Catches illegal dates (like Nov 31st)
          case _: IllegalArgumentException => getAdate
        }
      }
      getAdate #:: getRandomly
    }

    if (isRandom) getRandomly
    else next(if (isReversed) max else min)
  }

  /** Get a list of JDK dates. */
  def getJavaDates(n:Int): List[Date]= genJavaDates take n toList

  /** Get a stream of JDK dates. */
  def genJavaDates: Stream[Date]= gen map {jd=> jd.toDate}
}

object Dates {
  /** A reusable date formatting function. */
  def dateFormatter(fmt: String="yyyy.MM.dd")(dt:DateTime):String =
    DateTimeFormat.forPattern(fmt).print(dt)

  def apply():Dates = new Dates()
}