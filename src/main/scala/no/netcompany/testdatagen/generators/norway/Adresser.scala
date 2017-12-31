package no.netcompany.testdatagen.generators.norway

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.Generator
import no.netcompany.testdatagen.aggreg.{FieldConcatenator, WeightedGenerator}
import no.netcompany.testdatagen.generators.{Chars, Fixed, FromList, Ints}

/**
 * A generator to create strings that look like Norwegian street addresses.
 * It uses surnames (from NorskeNavn.kunEtternavn) and places (from
 * Poststeder.poststed), and optionally a house number (sometimes with a letter suffix).
 */
class Adresser(withNumbers:Boolean) {
  private val streetGenerator=
    FieldConcatenator()
      .add(WeightedGenerator()
           .add(3, FieldConcatenator()
                   add(Poststeder poststed())
                   add Fixed("s")
                   add FromList("gata", "svingen", "veien", "kroken")
               )
           .add(5, FieldConcatenator()
                   add NorskeNavn().kunEtternavn
                   add Fixed("s ")
                   add FromList("gate", "vei", "plass")
               )
          )
  private val numberGenerator=
    FieldConcatenator()
       .add(Fixed(" "))
       .add(Ints() from 1 to 120)
       .add(WeightedGenerator[Any]()
            add(3, Fixed(""))
            add(1, Chars('A' to 'F'))
           )
  def generator : Generator[String]=
    if (withNumbers) streetGenerator.add(numberGenerator)
    else streetGenerator
}
object Adresser {
  def apply(withNumbers:Boolean= true): Generator[String] =
    new Adresser(withNumbers).generator
}
