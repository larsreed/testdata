package no.mesan.testdatagen.generators

import no.mesan.testdatagen.{ExtendedDelegate, ExtendedGenerator}

/**
 * Generate Chars.
 * Defaults: length 1, from a to z
 *
 * @author lre
 */
class Chars extends ExtendedGenerator[Char] with ExtendedDelegate[String, Char] {

  private val embedded= Strings(1)
  protected var generator: ExtendedGenerator[String]= embedded

  /** Set character range. */
  def chars(seq: Seq[Char]): this.type = { generator.asInstanceOf[Strings].chars(seq); this }

  override def conv2gen(f: Char): String= f+""
  override def conv2result(f: String): Char= f(0)


  // TODO: Delegate
  override def distinct: this.type = { embedded.distinct; this }
  override def genStrings: Stream[String] = embedded.genStrings
  override def gen: Stream[Char] = embedded.gen map conv2result
}

object Chars {
  def apply():Chars = new Chars()
  def apply(chars:Seq[Char]):Chars = new Chars().chars(chars)
}
