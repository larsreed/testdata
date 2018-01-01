package no.netcompany.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.netcompany.testdatagen.aggreg.WeightedGenerator
import no.netcompany.testdatagen.{Generator, ExtendedImpl}

import scala.util.Random

/**
 * Generate longs.
 * Special methods: step(n) -- only used for sequential generation -- sets the step size (default 1),
 *                             with negative value steps from max down towards min
 * Default limits: 0 .. Long.MaxValue-1
 */
class Longs extends ExtendedImpl[Long] {

  filter(x=> lower match { case Some(low)=>  x>=low;  case _=> true })
  filter(x=> upper match { case Some(high)=> x<=high; case _=> true })

  private var stepSize: Long= 1
  /** Step size, used only for sequential values. */
  def step(step: Long): this.type= {
    require(step!=0, "Step cannot be 0")
    stepSize= step
    this
  }

  protected def getStream: Stream[Long] = {
    val min= lower.getOrElse(0L)
    val max= upper.getOrElse(Long.MaxValue-1)
    require(max>=min, "from >= to")
    require(max<Long.MaxValue, "max < " + Long.MaxValue)
    require(min>Long.MinValue, "min > " + Long.MinValue)

    val span= BigInt(max) - BigInt(min) +1
    val startAt= if (stepSize<0) max else min

    def sequentially(last: Long): Stream[Long]= {
      val possibly= last + stepSize
      val really= if (possibly>max) min else if (possibly<min) max else possibly
      really #:: sequentially(really)
    }

    def randomly(): Stream[Long]= (min + (BigInt(Random.nextLong()) mod span)).toLong #:: randomly

    if (isSequential) startAt #:: sequentially(startAt)
    else randomly()
  }
}

object Longs {
  def apply(from:Long=0, to:Long=Long.MaxValue-1, step:Long=1): Longs=
    new Longs() from from to to step step

  def negative(from:Long=0, to:Long=Long.MaxValue-1, step:Long=1): Generator[Long] =
    FromStream(apply(from, to, step).gen map(_ * -1))

  def anyLong: Generator[Long] = WeightedGenerator((1, apply()), (1, negative()))
}
