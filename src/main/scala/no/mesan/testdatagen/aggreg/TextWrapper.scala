package no.mesan.testdatagen.aggreg

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import scala.collection.immutable.List

import no.mesan.testdatagen.{Generator, GeneratorImpl}

/**
 * This generator takes any other generator as input, always uses its
 * genStrings as input, thus acting as "text converter", and adds methods to
 * manipulate the resulting text.
 */
class TextWrapper(generator:Generator[_]) extends GeneratorImpl[String] {

  private var transformers: List[(String=>String)]= List(s => s)

  /** Add pre- and/or suffix to string. */
  def surroundWith(prefix:String="", suffix:String=""): this.type = {
    if (prefix!="") transform(s => prefix + s)
    if (suffix!="") transform(s => s + suffix)
    this
  }

  /** Add a given string transformation. */
  def transform(f: String=>String): this.type = {
    transformers ::= f
    this
  }

  /** Take a substring. */
  def substring(from:Int, to:Int= -1): this.type =
    if ( to>=0 ) transform { s => s.substring(from, to)}
    else transform { s => s.substring(from)}

  /** trim the string. */
  def trim(): this.type = transform {s=> s.trim }

  /** Convert to upper case. */
  def toUpper: this.type = transform { s=> s.toUpperCase }

  /** Convert to lower case. */
  def toLower: this.type = transform { s=> s.toLowerCase }

  /** Substitute text. */
  def substitute(fromRegex:String, to:String): this.type = transform { s=> s.replaceAll(fromRegex, to) }

  private def composed = transformers reduce(_ compose _)

  private def transformAll(s:List[String]): List[String]= {
    val func= transformers reduce(_ compose _)
    s map func
  }

  def getStream: Stream[String]= generator.genStrings.map(composed)

}

object TextWrapper {
  def apply(generator: Generator[_]): TextWrapper=  new TextWrapper(generator)
}
