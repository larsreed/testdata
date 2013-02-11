package net.kalars.testgen.generators.norway

import net.kalars.testgen.ExtendedGenerator
import net.kalars.testgen.generators.FromFile

/**
 * Generate Norwegian country names.
 */
object Land {
  def apply(allLines:Boolean=true): ExtendedGenerator[String]=
    FromFile("land.txt", allLines)
}
