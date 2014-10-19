package no.mesan.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.{ExtendedDelegate, ExtendedGenerator}

/**
 * Generate Ints.
 * Special methods: step(n) -- only used for sequential generation -- sets the step size (default 1)
 * Default limits: Int.MinValue+1 .. Int.MaxValue-1
 */
class Ints extends ExtendedGenerator[Int] with ExtendedDelegate[Long, Int]  {

  private val embedded= Longs() from (Int.MinValue + 1).toLong to (Int.MaxValue - 1).toLong
  protected var generator: ExtendedGenerator[Long] = embedded

  override protected def conv2gen(f: Int): Long = f+0L
  override protected def conv2result(f: Long): Int= f.toInt

  /** Step size, used only for sequential values. */
  def step(s: Int): this.type= { embedded.step(s); this }

  // TODO: Delegate
  override def distinct: this.type = { embedded.distinct; this }
  override def genStrings: Stream[String] = embedded.genStrings
  override def gen: Stream[Int] = embedded.gen map conv2result
}

object Ints {
  def apply(from:Int=Int.MinValue+1, to:Int=Int.MaxValue-1, step:Int=1): Ints=
    new Ints().from(from).to(to).step(step)
}
