package no.mesan.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.{GeneratorDelegate, Generator}
import no.mesan.testdatagen.aggreg.{FieldConcatenator, TextWrapper, WeightedGenerator}
import no.mesan.testdatagen.generators.{FromFile, Strings}

/**
 * Generate Norwegian names...
 *
 * First a bit about the background for this generator...
 * Over the years, I have "scraped" lists of names from the net --
 * tax lists, participants in conferences and sports events etc,
 * and tried to normalize and uniquify these lists (this bears the
 * risk of having first and last names mixed up, and wrong capitalization...).
 * Then I removed first and last names that only appeared once in any
 * combination, to avoid generating names that identifies a single person.
 * To these lists, I added the list from Norwegian SSB (Statistics Norway) of
 * the most popular names, from these I have extracted the lists of names in
 * "fornavn.txt" (a little short of 5000 first names) and "etternavn.txt"
 * (about 8300 last names).  The lists have no notion of gender, so you might
 * well end up with names like "Ann Abdul Hansen"...
 *
 * To generate names
 * 1. You start with the the apply method -- NorskeNavn(allLines:Boolean=true)
 *    (with allLines=true, all 12500 names are read at least once)
 * 2. and may optionally add forOgEtternavn (default, both first and last names,
 *    creating names with 1 or 2 of each), kunFornavn (single first names only)
 *    or kunEtternavn (single last names only)
 * 3. the standard filter (and formatWith) may also be used.
 */
class NorskeNavn extends Generator[String] with GeneratorDelegate[String, String, Generator[String]]{
  private val fornavn= "fornavn.txt"
  private val etternavn= "etternavn.txt"

  forOgEtternavn

  protected var generator: Generator[String]= _
  def delegate: Generator[String]= generator  // For the trait

  /** Return both first and last names (the default). */
  def forOgEtternavn: this.type = {
    generator= TextWrapper(
        WeightedGenerator().add(40, create(1, 1)).
                            add(20, create(1, 2)).
                            add(25, create(2, 1)).
                            add(2,  create(2, 2)).
                            add(5,  create(2, 2)))
    this
  }

  /** Return only first names. */
  def kunFornavn: this.type = {
    generator= TextWrapper(create(1, 0))
    this
  }

  /** Return only last names. */
  def kunEtternavn: this.type = {
    generator= TextWrapper(create(0, 1))
    this
  }

  private def create(antFor: Int, antEtter:Int): Generator[String] = {
   val gen= new FieldConcatenator()
   for ( i<- 0 to antFor-1) {
     if (i>0) gen.add(Strings().length(1).chars(" "))
     gen.add(FromFile(fornavn, "ISO-8859-1"))
   }
   for ( i<- 0+antFor to antFor+antEtter-1) {
     if (i>0) gen.add(Strings().length(1).chars(" "))
     gen.add(FromFile(etternavn, "ISO-8859-1"))
   }
   gen
  }
}

object NorskeNavn {
  def apply(): NorskeNavn = new NorskeNavn()
}
