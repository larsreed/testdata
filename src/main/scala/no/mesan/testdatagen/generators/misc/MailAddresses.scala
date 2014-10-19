package no.mesan.testdatagen.generators.misc

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.Generator
import no.mesan.testdatagen.aggreg.{FieldConcatenator, WeightedGenerator}
import no.mesan.testdatagen.generators.{Fixed, FromList, Strings}

/** Generate mail-like strings... */
object MailAddresses {
  def apply(): Generator[String] = {
    val chars = "aeiouaeiouabcdefghijklmnoprstuvyabcdefghijklmnopqrstuvwxyz"

    val pfxGen= WeightedGenerator().
      add(10, Strings().lengthBetween(3, 8).chars(chars)).
      add(6, FieldConcatenator().add(Strings().lengthBetween(3, 8).chars(chars)).
                                 add(Fixed(".")).
                                 add(Strings().lengthBetween(4, 9).chars(chars)))
    FieldConcatenator().
                add(pfxGen).
                add(Fixed("@")).
                add(Strings().lengthBetween(4, 9).chars(chars)).
                add(FromList(".com", ".no", ".org", ".net", ".co.uk", ".gov"))
  }
}
