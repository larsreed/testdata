package net.kalars.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

import scala.language.existentials

@RunWith(classOf[JUnitRunner])
class FnrSpec extends FlatSpec {

  def sjekkFnr(fnr: String) = Fnr.sjekkFnr(fnr)

  "The Fnr generator" should "generate fnrs with correct checksums" in {
    val res= (Fnr() boysOnly() withDnr 100 get 100) ++
             (Fnr() girlsOnly() get 100) ++
             (Fnr() get 100)
    for (fnr<-res) assert(sjekkFnr(fnr), fnr)
  }

  it should "generate only dnrs on demand" in {
    val res= Fnr() withDnr 100 get 200
    for (fnr<-res) assert(fnr.matches("^[4-7].*"), fnr)
  }

  it should "generate no dnrs when requested" in {
    val res= Fnr() withDnr 0 get 200
    for (fnr<-res) assert(fnr.matches("^[0-3].*"), fnr)
  }

  it should "generate only female fnrs on demand" in {
    val res= Fnr() girlsOnly() get 200
    for (fnr<-res) assert(fnr.matches("^........[02468].*"), fnr)
  }

  it should "generate only male fnrs on demand" in {
    val res= Fnr() boysOnly() get 200
    for (fnr<-res) assert(fnr.matches("^........[13579].*"), fnr)
  }
}