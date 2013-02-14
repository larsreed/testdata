package no.mesan.testdatagen.generators

import no.mesan.testdatagen.ExtendedGenerator
import no.mesan.testdatagen.ExtendedDelegate

/**
 * Generate Chars.
 * Special methods: chars(seq) -- accepted characters
 * Defaults: length 1, from a(s) to z(s)
 *
 */
class Chars extends ExtendedGenerator[Char] with ExtendedDelegate[String, Char] {

  var generator: ExtendedGenerator[String]= Strings(1)

  /** Set character range. */
  def chars(seq: Seq[Char]): this.type = { generator.asInstanceOf[Strings].chars(seq); this }

  override def conv2gen(f: Char): String= f+""
  override def conv2result(f: String): Char= f(0)
}

object Chars {
  def apply():Chars = new Chars()
  def apply(chars:Seq[Char]):Chars = new Chars().chars(chars)
}
