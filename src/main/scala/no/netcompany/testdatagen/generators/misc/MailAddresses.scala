package no.netcompany.testdatagen.generators.misc

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.Generator
import no.netcompany.testdatagen.aggreg.{FieldConcatenator, WeightedGenerator}
import no.netcompany.testdatagen.generators.{Fixed, FromList, Strings}

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
