package no.mesan.testdatagen.generators.norway

import no.mesan.testdatagen.ExtendedGenerator
import no.mesan.testdatagen.generators.FromFile

/**
 * Generate funny names...
 *
 * @author lre
 */
object RareNavn {
  def apply(): ExtendedGenerator[String] = FromFile("rareNavn.txt", "ISO-8859-1")
}
