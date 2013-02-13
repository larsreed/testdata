package net.kalars.testgen.generators.norway

import net.kalars.testgen.ExtendedGenerator
import net.kalars.testgen.generators.FromFile

/**
 * Generate funny names....
 */
object RareNavnGenerator {
  def apply(allLines:Boolean=true): ExtendedGenerator[String] =
    FromFile("rareNavn.txt", allLines)
}
