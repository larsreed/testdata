package net.kalars.testgen.generators.misc

import net.kalars.testgen.Generator
import net.kalars.testgen.aggreg.FieldConcatenator
import net.kalars.testgen.generators.{FromList, Strings}

/**
 * Generate URL-like strings...
 * Special methods:
 * Default limits: Pattern: http(s)://(www.)4-10.dom
 */
object Urls {
  def apply(): Generator[String] = {
    val chars = "aeiouaeiouabcdefghijklmnoprstuvyabcdefghijklmnopqrstuvwxyz"
    new FieldConcatenator().
                add(FromList("http://", "https://")).
                add(FromList("", "www.")).
                add(Strings().lengthBetween(4,10).chars(chars)).
                add(FromList(".com", ".no", ".org", ".net", ".co.uk", ".gov"))
  }
}
