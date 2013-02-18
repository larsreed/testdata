package no.mesan.testdatagen.generators.norway

import no.mesan.testdatagen.Generator
import no.mesan.testdatagen.aggreg.{FieldConcatenator, WeightedGenerator}
import no.mesan.testdatagen.generators.{Chars, Fixed, FromList, Ints}

class Adresser(withNumbers:Boolean) {
  val streetGenerator=
    FieldConcatenator()
      .add(WeightedGenerator()
           .add(3, FieldConcatenator()
                   add(Poststeder poststed())
                   add(Fixed("s"))
                   add(FromList("gata", "svingen", "veien", "kroken"))
               )
           .add(5, FieldConcatenator()
                   add(NorskeNavn().kunEtternavn)
                   add(Fixed("s "))
                   add(FromList("gate", "vei", "plass"))
               )
          )
  val numberGenerator=
    FieldConcatenator()
       .add(Fixed(" "))
       .add(Ints() from(1) to(120))
       .add(WeightedGenerator()
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