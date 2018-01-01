package no.netcompany.testdatagen.generators.app

import no.netcompany.testdatagen.dsl.DslLikeSyntax
import no.netcompany.testdatagen.generators.{FromList, Strings}
import no.netcompany.testdatagen.generators.misc.{Guids, Markov}
import no.netcompany.testdatagen.generators.norway.Fnr

/** Prøve ting... */
object Tryit extends App with DslLikeSyntax {
  println(Fnr().boysOnly().standardFormat.getStrings(50))
  // println(guids getStrings (4))
  // println(Markov("c:/tools/ibmWordCloud/examples/togaf9.txt").getStream.take(255).toList.mkString(" "))
}
