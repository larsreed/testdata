package no.mesan.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.ExtendedGenerator
import no.mesan.testdatagen.generators.FromFile

/** Generate funny names... */
object RareNavn {
  def apply(): ExtendedGenerator[String] = FromFile("rareNavn.txt", "ISO-8859-1")
}
