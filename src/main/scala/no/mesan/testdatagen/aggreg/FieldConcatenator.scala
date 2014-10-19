package no.mesan.testdatagen.aggreg

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.{Generator, GeneratorImpl, StreamUtils}

/**
 * The FieldConcatenator is given a set of generators with the add method.
 * Its output is the concatenated output of 1 field from each of the generators in the same order
 * as the add calls).
 */
class FieldConcatenator(fieldSeparator: String= "") extends GeneratorImpl[String]
      with MultiGenerator[Any] with StreamUtils {
  def getStream: Stream[String] =  combineStringGens(generators) map { _.mkString(fieldSeparator)  }
}

object FieldConcatenator {
  def apply(gs: Generator[Any]*): FieldConcatenator =  new FieldConcatenator() add(gs:_*)
  def apply(fieldSeparator: String, gs: Generator[Any]*): FieldConcatenator =
    new FieldConcatenator(fieldSeparator) add(gs:_*)
}
