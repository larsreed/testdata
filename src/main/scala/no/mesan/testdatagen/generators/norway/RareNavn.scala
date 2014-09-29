package no.mesan.testdatagen.generators.norway

import no.mesan.testdatagen.{StreamGenerator, ExtendedGenerator}
import no.mesan.testdatagen.generators.FromFile

/**
 * Generate funny names....
 *
 * @author lre
 */
object RareNavn {
  def apply(): ExtendedGenerator[String] with StreamGenerator[String] = FromFile("rareNavn.txt", "ISO-8859-1")
}
