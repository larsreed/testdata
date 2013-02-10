package net.kalars.testgen.generators.norway

import net.kalars.testgen.ExtendedGenerator
import net.kalars.testgen.generators.FromFileGenerator

/**
 * Generate Norwegian country names.
 * Special methods:
 */
object LandGenerator {
  val landnavn= "land.txt"

  def apply(allLines:Boolean=true): ExtendedGenerator[String] = {
    FromFileGenerator(landnavn, allLines)
  }
}
