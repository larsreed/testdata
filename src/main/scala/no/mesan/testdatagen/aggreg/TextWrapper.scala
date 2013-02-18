package no.mesan.testdatagen.aggreg

import scala.collection.immutable.List

import no.mesan.testdatagen.{Generator, GeneratorImpl}

/**
 * Transform to text output, optionally transform the result.
 */
class TextWrapper(generator:Generator[_]) extends GeneratorImpl[String] {

  private var transformers: List[(String=>String)]= List((s=>s))

  /** Add pre- and/or suffix to string. */
  def surroundWith(prefix:String="", suffix:String=""): this.type = {
    if (prefix!="") transform(s => prefix + s)
    if (suffix!="") transform(s => s + suffix)
    this
  }

  /** Take a substring. */
  def substring(from:Int, to:Int= -1): this.type =
    if ( to>=0 ) transform { s => s.substring(from, to)}
    else transform { s => s.substring(from)}

  /** Add a given string transformation. */
  def transform(f: String=>String): this.type = {
    transformers ::= f
    this
  }

  /** trim the string. */
  def trim(): this.type = transform {s=> s.trim }

  /** Convert to upper case. */
  def toUpper(): this.type = transform { s=> s.toUpperCase }

  /** Convert to lower case. */
  def toLower(): this.type = transform { s=> s.toLowerCase }

  private def transformAll(s:List[String]): List[String]= {
    val func= transformers reduce(_ compose _)
    s map func
  }

  override def get(n: Int): List[String] = getStrings(n)

  override def getStrings(n: Int): List[String]=
    transformAll(generator.getStrings(n))
}

object TextWrapper {
  def apply(generator: Generator[_]): TextWrapper=  new TextWrapper(generator)
}
