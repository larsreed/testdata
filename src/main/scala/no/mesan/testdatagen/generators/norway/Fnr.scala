package no.mesan.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen._
import no.mesan.testdatagen.generators.{Dates, Ints}
import org.joda.time.DateTime

import scala.annotation.tailrec

/**
 * Generate Norwegian "foedselsnummer" (social registration numbers).
 * Special methods: withDnr -- add an amount of "D numbers" (see below)
 *                  boys/girlsOnly
 */
class Fnr(dateGenerator: ExtendedGenerator[DateTime]) extends GeneratorImpl[String] with Percentage {

  private def isOdd(i: Int)= i%2 == 1
  private def isEven(i: Int)= !isOdd(i)

  private val intGen= Ints().from(0).to(9)
  private lazy val boyGen= Ints().from(0).to(9).filter(isOdd)
  private lazy val girlGen= Ints().from(0).to(9).filter(isEven)

  private var dnrFactor= 0
  /**
   * To generate D-numbers (for foreigners, with 40 added to the day of month),
   * call this with a percentage.  If the factor is 100, DNRs are always generated,
   * otherwise approximately (up to the random generator) n% of the dates has
   * 40 added to it.
   */
  def withDnr(percent: Int): this.type = { dnrFactor= percent; this }

  private var boys= true
  private var girls= true
  /** As the name implies... */
  def boysOnly(): this.type = { boys=true; girls=false;  this }
  /** As the name implies... */
  def girlsOnly(): this.type = { boys=false; girls=true; this }

  private def get3: List[Int]= intGen.get(2) ++ (if (!boys) girlGen.get(1)
    else if (!girls) boyGen.get(1)
    else intGen.get(1))

  def getStream: Stream[String]= {
    // Not taken into account:
    //    if ( aar<1900 ) 500 to 749
    //    else if (aar< 2000) 0 to 749
    //    else 500 to 999

    def genPnr(s: String): String = {
      val orgAsInt= s.map(c=> c-'0').toList
      // Transform to DNR
      val first6= if (hit(dnrFactor)) 4+orgAsInt(0) :: orgAsInt.drop(1)
                  else orgAsInt

      @tailrec
      def genNext(soFar: List[Int], fakt:List[Int]): List[Int]= {
        val all= first6 ++ soFar
        if (all.length>=11) soFar
        else {
          val nxt= all.zip(fakt).foldLeft(0)((sum,par)=> sum + (par._1*par._2)) % 11
          if (nxt==1) genNext(get3, Fnr.fakt1) // cannot work, restart
          else if (nxt==0) genNext(soFar :+ nxt, Fnr.fakt2) // Either next or done
          else genNext(soFar :+ (11-nxt), Fnr.fakt2)
        }
      }
      val res= first6 ++ genNext(get3, Fnr.fakt1)
      res.foldLeft("")((s, i)=> s + (i+""))
    }

    dateGenerator.formatWith(Dates.dateFormatter("ddMMyy")).genStrings map genPnr
  }
}

object Fnr {
  def apply(): Fnr = new Fnr(Dates().from(y=1855).to(new DateTime()))
  def apply(g: ExtendedGenerator[DateTime]): Fnr = new Fnr(g)

  val fakt1= List(3, 7, 6, 1, 8, 9, 4, 5, 2)
  val fakt2= List(5, 4, 3, 2, 7, 6, 5, 4, 3, 2)

  /** Check that a given Fnr is valid. */
  def sjekkFnr(fnr: String): Boolean = {
    @tailrec def sjekk(fnr: List[Int], fakt: List[Int], sum: Int): Boolean =
      if (fakt.isEmpty) {
        val mod11 = sum % 11
        (mod11 == 0 && fnr.head == 0) || (fnr.head == 11 - mod11)
      }
      else
        sjekk(fnr.tail, fakt.tail, sum + (fnr.head * fakt.head))
    val fnrList = fnr.toList map (_ - '0')
    sjekk(fnrList, fakt1, 0) && sjekk(fnrList, fakt2, 0)
  }
}
