package no.netcompany.testdatagen.generators.misc

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.Generator
import no.netcompany.testdatagen.aggreg.{WeightedGenerator, FieldConcatenator}
import no.netcompany.testdatagen.generators.Strings

/**
 * Creates "name-like" strings -- words containing A-Zs with random length between
 * 3 and 20 with an uppercase first letter.  This is only a wrapper object around
 * a Strings generator, its only parameter is an int telling how many
 * space-separated words to create in each string.
 * Default limits: name length from 3 to 18, first letter upper case
 */
object Names {
  private val pfxChars= "ABDEFGHIJKLMNOPRSTUVY"*12 +
                "CQWXZ"
  private val sfxChars= "abdefghijklmnoprstuvy" * 20 +
                "aeiouy" * 5
                "cqwxz" * 3 +
                "-'"
  def apply(n: Int): Generator[String] = {
    val gen= new FieldConcatenator()
    for ( i<- 0 until n) {
      if (i>0) gen.add(Strings().length(1).chars(" "))
      gen.add(Strings().length(1).chars(pfxChars))
      gen.add(Strings().lengthBetween(2, 19).chars(sfxChars))
    }
    gen
  }

  def apply(fromN: Int, toN: Int): Generator[String] = {
    val gen = WeightedGenerator[String]()
    for ( i<- fromN to toN ) gen.add(Names(i))
    gen
  }
}
