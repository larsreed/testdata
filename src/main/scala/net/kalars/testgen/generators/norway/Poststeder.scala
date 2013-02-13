package net.kalars.testgen.generators.norway

import net.kalars.testgen.{ExtendedGenerator, Generator}
import net.kalars.testgen.aggreg.TextWrapper
import net.kalars.testgen.generators.FromFile

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
