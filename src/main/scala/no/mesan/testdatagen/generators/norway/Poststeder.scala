package no.mesan.testdatagen.generators.norway

import no.mesan.testdatagen.{ExtendedGenerator, Generator}
import no.mesan.testdatagen.aggreg.TextWrapper
import no.mesan.testdatagen.generators.FromFile

/**
 * Generate Norwegian postal codes -- format NNNN Name, where NNNN is an integer.
 * Use TextWrapper to get at the code/name parts separately.
 */
object Poststeder {
  def apply(allLines:Boolean=true): ExtendedGenerator[String]=
    FromFile("postnr.txt", allLines)

  def postnr(allLines:Boolean=true): Generator[String] =
    TextWrapper(apply(allLines)).substring(0, 4)

  def poststed(allLines:Boolean=true): Generator[String] =
    TextWrapper(apply(allLines)).substring(5)
}
