package no.netcompany.testdatagen.generators.app

import no.netcompany.testdatagen.dsl.DslLikeSyntax
import no.netcompany.testdatagen.generators.{FromList, Strings}
import no.netcompany.testdatagen.generators.misc.{Guids, Markov}
import no.netcompany.testdatagen.generators.norway.Fnr

/** Prøve ting... */
object Tryit extends App with DslLikeSyntax {
  gruble
  // println(Fnr().boysOnly().standardFormat.getStrings(50))
  // println(guids getStrings (4))
  // println(Markov("c:/tools/ibmWordCloud/examples/togaf9.txt").getStream.take(255).toList.mkString(" "))

  def gruble {
    val letters = Strings(1, "ABDEFGHIJKLMNOPRSTUVYÆØÅ").distinct.get(5).sorted
    val cats = FromList("Firmaer",
      "Filmer",
      "Drikker",
      "Forfattere",
      "Matretter",
      "Politikere",
      "Bøker",
      "Kongelige",
      "Spill",
      "Sanger",
      "Land",
      "Byer",
      "Bilmerker",
      "Artister",
      "TV-serier",
      "Frukt & grønt").distinct.get(5).sorted
    for (s<- letters) print("\t" + s)
    for (s<- cats) print("\n" +s)
  }
}
