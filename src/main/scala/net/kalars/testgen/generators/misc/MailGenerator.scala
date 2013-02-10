package net.kalars.testgen.generators.misc

import net.kalars.testgen.Generator
import net.kalars.testgen.aggreg.{FieldConcatenator, WeightedGenerator}
import net.kalars.testgen.generators.{FixedGenerator, ListGenerator, Strings}

/**
 * Generate mail-like strings...
 * Special methods:
 * Default limits: Pattern:
 */
object MailGenerator {
  def apply(): Generator[String] = {
    val chars = "aeiouaeiouabcdefghijklmnoprstuvyabcdefghijklmnopqrstuvwxyz"

    val pfxGen= WeightedGenerator().
      add(10, Strings().lengthBetween(3,8).chars(chars)).
      add(6, FieldConcatenator().add(Strings().lengthBetween(3,8).chars(chars)).
                                 add(FixedGenerator(".")).
                                 add(Strings().lengthBetween(4,9).chars(chars)))
    FieldConcatenator().
                add(pfxGen).
                add(FixedGenerator("@")).
                add(Strings().lengthBetween(4,9).chars(chars)).
                add(ListGenerator(List(".com", ".no", ".org", ".net", ".co.uk", ".gov")))
  }
}
