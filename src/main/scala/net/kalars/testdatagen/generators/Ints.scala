package net.kalars.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import net.kalars.testdatagen.{ExtendedDelegate, ExtendedGenerator}

/**
 * Generate Ints.
 * Special methods: step(n) -- only used for sequential generation -- sets the step size (default 1)
 * Default limits: Int.MinValue+1 .. Int.MaxValue-1
 */
class Ints extends ExtendedGenerator[Int] with ExtendedDelegate[Long, Int, ExtendedGenerator[Long]]  {

  private val embedded= Longs() from (Int.MinValue + 1).toLong to (Int.MaxValue - 1).toLong
  def delegate: ExtendedGenerator[Long] = embedded // For the trait

  override protected def conv2gen(f: Int): Long = f+0L
  override protected def conv2result(f: Long): Int= f.toInt

  /** Step size, used only for sequential values. */
  def step(s: Int): this.type= { embedded.step(s); this }
}

object Ints {
  def apply(from:Int=Int.MinValue+1, to:Int=Int.MaxValue-1, step:Int=1): Ints=
    new Ints().from(from).to(to).step(step)
}