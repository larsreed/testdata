package net.kalars.testgen.generators.misc

import net.kalars.testgen.Generator
import net.kalars.testgen.aggreg.FieldConcatenator
import net.kalars.testgen.generators.{ListGenerator, Strings}

/**
 * Generate URL-like strings...
 * Special methods:
 * Default limits: Pattern: http(s)://(www.)4-10.dom
 */
object UrlGenerator {
  def apply(): Generator[String] = {
    val chars = "aeiouaeiouabcdefghijklmnoprstuvyabcdefghijklmnopqrstuvwxyz"
    new FieldConcatenator().
                add(ListGenerator(List("http://", "https://"))).
                add(ListGenerator(List("", "www."))).
                add(Strings().lengthBetween(4,10).chars(chars)).
                add(ListGenerator(List(".com", ".no", ".org", ".net", ".co.uk", ".gov")))
  }
}
