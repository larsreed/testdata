package no.mesan.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.Generator
import no.mesan.testdatagen.aggreg.FieldConcatenator
import no.mesan.testdatagen.generators.{Ints, Strings}

/**
 * Generate Norwegian car number plates.
 * Always random, the letters IMOQ are never used.
 */
object Kjennemerker {
  def apply(): Generator[String] = new FieldConcatenator().
         add(Strings().length(2).chars(('A' to 'Z').filter(c=> ! ("IMOQ" contains c)))).
         add(Ints().from(10000).to(99999))
}
