package net.kalars.testdatagen.generators.app

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.aggreg.TwoFromFunction
import net.kalars.testdatagen.generators.{Dates, FromList, Ints}
import net.kalars.testdatagen.recordgen.ToCsv

import scala.language.{existentials, postfixOps}

object Patterns extends App {
  val nRecs= 1000
  val (_,minGen)= TwoFromFunction(Ints() from 1 to 24, (n:Int) => 30*n).asListGens(nRecs)

  val tot= ToCsv(withHeaders = false)
    .add("User", FromList("jorgenj", "stiano", "oysteins", "larsr"))
    .add("date", Dates() from(y = 2014, m = 1) to(y = 2014, m = 4) format "yyyy-MM-dd")
    .add("akt", Ints() from 1000 to 2000)
    .add("min", minGen)
  val tot2= ToCsv(withHeaders = false)
    .add("User", FromList("A", "B"))
    .add("date", Dates() from(y = 2014, m = 1) to(y = 2014, m = 2) format "yyyy-MM-dd")
    .add("akt", Ints() from 1000 to 1002)
    .add("min", minGen)
  var i=0
  for (rec <- tot.get(10)) {
    if (i%2==0) print(rec + "\t,\t")
    else println(rec + "\t,")
    i+= 1
  }
}