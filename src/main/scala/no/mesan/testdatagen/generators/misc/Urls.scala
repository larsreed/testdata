package no.mesan.testdatagen.generators.misc

import no.mesan.testdatagen.Generator
import no.mesan.testdatagen.aggreg.FieldConcatenator
import no.mesan.testdatagen.generators.{FromList, Strings}

/**
 * Generate URL-like strings...
 * Pattern: http(s)://(www.)4-10.dom
 *
 * @author lre
 */
object Urls {
  def apply(): Generator[String] = {
    val chars = "aeiouaeiouabcdefghijklmnoprstuvyabcdefghijklmnopqrstuvwxyz"
    new FieldConcatenator().
                add(FromList("http://", "https://")).
                add(FromList("", "www.")).
                add(Strings().lengthBetween(4, 10).chars(chars)).
                add(FromList(".com", ".no", ".org", ".net", ".co.uk", ".gov"))
  }
}
