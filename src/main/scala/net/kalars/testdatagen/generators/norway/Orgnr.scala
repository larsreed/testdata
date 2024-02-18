package net.kalars.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.{ExtendedImpl, GeneratorFilters}
import net.kalars.testdatagen.generators.Ints

import scala.language.postfixOps

/**
 * Generates legal "organisasjonsnummer", Norwegian "organization numbers"
 * (http://www.brreg.no/samordning/organisasjonsnummeret.html).
 * These are integers with 9 digits.
 */
class Orgnr() extends ExtendedImpl[Int] with GeneratorFilters[Int] {

  from(80000000)
  to(90000000)

  def getStream: Stream[Int]= {
    val low= lower.get
    val high= upper.get
    require(low >= 10000000 && low < 100000000, "illegal lower bound")
    require(high >= 10000000 && high < 100000000, "illegal upper bound")

    val fakt= List(3, 2, 7, 6, 5, 4, 3, 2)

    val baseGen= Ints() from low to high
    val intGen=  if (isSequential) baseGen sequential else baseGen

    def genOne(first8: Int): Option[Int]= {
      val nxt= first8.toString.map(_.toString.toInt)
                     .zip(fakt)
                     .foldLeft(0){ (sum, par)=> sum + (par._1 * par._2)} % 11
      if (nxt==1) None
      else if (nxt==0) Some(first8*10)
      else Some(first8*10 + (11-nxt))
    }

    intGen.gen map genOne filter {_.isDefined} map(_.get)
  }
}

object Orgnr {
  def apply(): Orgnr = new Orgnr()
}