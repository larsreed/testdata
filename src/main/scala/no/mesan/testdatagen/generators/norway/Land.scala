package no.mesan.testdatagen.generators.norway

import no.mesan.testdatagen.ExtendedGenerator
import no.mesan.testdatagen.generators.FromFile

/**
 * Generate Norwegian country names.
 */
object Land {
  def apply(allLines:Boolean=true): ExtendedGenerator[String]=
    FromFile("land.txt", allLines)
}
