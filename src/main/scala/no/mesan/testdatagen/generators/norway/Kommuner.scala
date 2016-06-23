package no.mesan.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.aggreg.TextWrapper
import no.mesan.testdatagen.generators.FromFile
import no.mesan.testdatagen.{ExtendedGenerator, Generator}

/** Generate Norwegian county codes -- format NNNN Name, where NNNN is an integer. */
object Kommuner {
  def apply(): ExtendedGenerator[String] = FromFile.iso88591("kommuner.txt")

  /** Return the number part part only. */
  def kommunenr(): Generator[String]= TextWrapper(apply()).substring(0, 4)

  /** Return the name part part only. */
  def kommunenavn(): Generator[String]=  TextWrapper(apply()).substring(5)
}
