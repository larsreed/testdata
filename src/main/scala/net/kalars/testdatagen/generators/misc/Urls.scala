package net.kalars.testdatagen.generators.misc

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.Generator
import net.kalars.testdatagen.aggreg.FieldConcatenator
import net.kalars.testdatagen.generators.{FromList, Strings}

/**
 * Generate URL-like strings...
 * Pattern: http(s)://(www.)4-10.dom
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