package no.mesan.testdatagen.generators.norway

import no.mesan.testdatagen.ExtendedGenerator
import no.mesan.testdatagen.generators.FromFile

/**
 * Generate funny names....
 */
object RareNavn {
  def apply(allLines:Boolean=true): ExtendedGenerator[String] =
    FromFile("rareNavn.txt", allLines)
}
