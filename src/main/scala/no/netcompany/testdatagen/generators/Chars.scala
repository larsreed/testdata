package no.netcompany.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.{ExtendedDelegate, ExtendedGenerator}

/**
 * Generate Chars.
 * Defaults: length 1, from a to z
 */
class Chars extends ExtendedGenerator[Char] with ExtendedDelegate[String, Char, ExtendedGenerator[String]] {

  private val embedded= Strings()
  def delegate: ExtendedGenerator[String]= embedded // For the trait

  /** Set character range. */
  def chars(seq: Seq[Char]): this.type = { delegate.asInstanceOf[Strings].chars(seq); this }

  override def conv2gen(f: Char): String= f+""
  override def conv2result(f: String): Char= f(0)
}

object Chars {
  def apply():Chars = new Chars()
  def apply(chars:Seq[Char]):Chars = new Chars().chars(chars)
}
