package no.mesan.testdatagen.generators.norway

import no.mesan.testdatagen.{StreamGenerator, ExtendedGenerator, Generator}
import no.mesan.testdatagen.aggreg.TextWrapper
import no.mesan.testdatagen.generators.FromFile

/**
 * Generate Norwegian county codes -- format NNNN Name, where NNNN is an integer.
 *
 * @author lre
 */
object Kommuner {
  def apply(): ExtendedGenerator[String] with StreamGenerator[String] =
    FromFile("kommuner.txt", "ISO-8859-1")

  /** Return the number part part only. */ // TODO Stream
  def kommunenr(): Generator[String]= TextWrapper(apply()).substring(0, 4)

  /** Return the name part part only. */ // TODO Stream
  def kommunenavn(): Generator[String]=  TextWrapper(apply()).substring(5)
}
