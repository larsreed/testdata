package net.kalars.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.annotation.tailrec
import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class OrgnrSpec extends FlatSpec {

  def sjekkOrgnr(orgNr: Int) = {
    val digits= orgNr.toString map(_.toString.toInt) toList
    @tailrec def sjekk(rest: List[Int], fakt: List[Int], sum: Int): Boolean =
      if (fakt.isEmpty) {
        val mod11 = sum % 11
        (mod11 == 0 && rest.head == 0) || (rest.head == 11 - mod11)
      }
      else
        sjekk(rest.tail, fakt.tail, sum + (rest.head * fakt.head))
    sjekk(digits, List(3, 2, 7, 6, 5, 4, 3, 2), 0)
  }

  "The Orgnr generator" should "generate correct values" in {
    val res= Orgnr() get 300
    for (nr<-res) assert(sjekkOrgnr(nr), ""+nr)
  }

  it should "also generate in sequential order" in {
    val res= Orgnr().sequential get 100
    for (nr<-res) assert(sjekkOrgnr(nr), ""+nr)
  }

  it should "also be able to handle different limits" in {
    val res= Orgnr() from 20000000 to 23456789 get 300
    for (nr<-res) assert(sjekkOrgnr(nr), ""+nr)
  }

  it should "reject illegal lower limits" in {
    for (v <- List(2, 999999990))
      intercept[IllegalArgumentException] {
        Orgnr() from v get 1
      }
  }

  it should "reject illegal upper limits" in {
    for (v <- List(2, 999999990))
      intercept[IllegalArgumentException] {
        Orgnr() from v get 1
      }
  }
}