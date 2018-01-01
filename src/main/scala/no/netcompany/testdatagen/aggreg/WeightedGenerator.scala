package no.netcompany.testdatagen.aggreg

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.utils.{StreamUtils, RandomElem}

import scala.collection.immutable.List

import no.netcompany.testdatagen.{Generator, GeneratorImpl}

/**
 * This generator takes one or more generators as input, and selects randomly between
 * them for each value to generate.
 * Each generator is given a weight -- the probability for each one is its own weight
 * relative to the sum of all weights.
 */
class WeightedGenerator[T] extends GeneratorImpl[T]
      with MultiGeneratorWithWeight[T]
      with StreamUtils
      with RandomElem {

  lazy val drawFrom: List[Int] = generators.zipWithIndex flatMap {
    case ((weight, _), index) => List.fill(weight)(index)
  }

  private def streamBody[A](stream: Stream[Seq[A]])= {
    stream map { tuple =>
      tuple(randomFrom(drawFrom))
    }
  }

  def getStream: Stream[T]= streamBody(combineGens(generators.map(_._2)))

  override def genStrings: Stream[String]= streamBody(combineStringGens(generators.map(_._2)))
}

object WeightedGenerator {
  def apply[T](weighted: (Int, Generator[T])*): WeightedGenerator[T] = new WeightedGenerator().addWeighted(weighted:_*)
  def apply[T](gs: List[Generator[T]]): WeightedGenerator[T] = new WeightedGenerator().add(gs:_*)
}
