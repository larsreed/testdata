package no.mesan.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.aggreg.TextWrapper
import no.mesan.testdatagen.generators.FromFile
import no.mesan.testdatagen.{ExtendedGenerator, Generator}

/** Generate Norwegian postal codes -- format NNNN Name, where NNNN is an integer. */
object Poststeder {
  def apply(): ExtendedGenerator[String] = FromFile("postnr.txt", "ISO-8859-1")

  /** Return the number part part only. */
  def postnr(): Generator[String] = TextWrapper(apply()).substring(0, 4)

  /** Return the name part part only. */
  def poststed(): Generator[String] = TextWrapper(apply()).substring(5)
}
