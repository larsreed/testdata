package net.kalars.testgen.generators.norway

import net.kalars.testgen.ExtendedGenerator
import net.kalars.testgen.generators.FromFileGenerator

/**
 * .
 */
object RareNavnGenerator {
  def apply(allLines:Boolean=true): ExtendedGenerator[String] =
    FromFileGenerator("rareNavn.txt", allLines)
}
