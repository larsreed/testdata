package net.kalars.testdatagen.generators.sample

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.generators.misc.Markov
import scala.language.postfixOps

object MarkovSample extends App {
  println(Markov.english() mkString 1000)
}