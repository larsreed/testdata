package net.kalars.testdatagen.generators.app

import net.kalars.testdatagen.dsl.DslLikeSyntax
import net.kalars.testdatagen.generators.misc.{Guids, Markov}
import net.kalars.testdatagen.generators.{FromList, Strings}
import net.kalars.testdatagen.generators.norway.Fnr

/** Prøve ting... */
object Tryit extends App with DslLikeSyntax {
  println(Fnr().boysOnly().standardFormat.getStrings(50))
  // println(guids getStrings (4))
  // println(Markov("c:/tools/ibmWordCloud/examples/togaf9.txt").getStream.take(255).toList.mkString(" "))
}