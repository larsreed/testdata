package no.mesan.testdatagen.aggreg

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.{Generator, GeneratorImpl, Percentage}

/**
 * This generator takes another generator and a percentage as input.
 * It gets its values from the input generator, and then replaces approximately N % of the
 * occurrences (decided by a random generator) with null.  N==0 means no nulls,
 * N==100 means only nulls, N==50 50% nulls etc.
 */
class SomeNulls[T](gen: Generator[T]) extends GeneratorImpl[T] with Percentage {

  private var nullPct= 0
  /** insert null in n% of the cases. */
  def nullFactor(percent: Int): SomeNulls[T]= { nullPct= percent; this }

  private def kill = hit(nullPct)

  def getStream: Stream[T] = gen.gen.map(if (kill) null.asInstanceOf[T] else _)
  override def genStrings: Stream[String] = getStream.map{v=> if (v==null) null else formatOne(v)}
}

object SomeNulls {
  def apply[T](percent:Int, generator: Generator[T]): SomeNulls[T] =
    new SomeNulls(generator).nullFactor(percent)
}
