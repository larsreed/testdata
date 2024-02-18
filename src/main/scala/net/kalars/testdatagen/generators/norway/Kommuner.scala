package net.kalars.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.{ExtendedGenerator, Generator}
import net.kalars.testdatagen.aggreg.TextWrapper
import net.kalars.testdatagen.generators.FromFile

/** Generate Norwegian county codes -- format NNNN Name, where NNNN is an integer. */
object Kommuner {
  def apply(): ExtendedGenerator[String] = FromFile.iso88591("kommuner.txt")

  /** Return the number part part only. */
  def kommunenr(): Generator[String]= TextWrapper(apply()).substring(0, 4)

  /** Return the name part part only. */
  def kommunenavn(): Generator[String]=  TextWrapper(apply()).substring(5)
}