package no.mesan.testdatagen.generators.norway

import no.mesan.testdatagen.ExtendedGenerator
import no.mesan.testdatagen.generators.FromFile

/**
 * Generate Norwegian country names.
 *
 * @author lre
 */
object Land {
  def apply(): ExtendedGenerator[String] = FromFile("land.txt", "ISO-8859-1")
}
