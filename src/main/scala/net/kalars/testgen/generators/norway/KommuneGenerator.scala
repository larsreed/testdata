package net.kalars.testgen.generators.norway

import net.kalars.testgen.{ExtendedGenerator, Generator}
import net.kalars.testgen.aggreg.TextWrapper
import net.kalars.testgen.generators.FromFileGenerator

/**
 * Generate Norwegian county codes -- format NNNN Name, where NNNN is an integer.
 */
object KommuneGenerator {
  val kommuner= "kommuner.txt"

  def apply(allLines:Boolean=true): ExtendedGenerator[String] = {
    FromFileGenerator(kommuner, allLines)
  }

  def kommunenr(allLines:Boolean=true): Generator[String] = {
    TextWrapper(apply(allLines)).substring(0, 4)
  }

  def kommunenavn(allLines:Boolean=true): Generator[String] = {
    TextWrapper(apply(allLines)).substring(5)
  }
}
