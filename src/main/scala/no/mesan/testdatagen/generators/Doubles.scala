package no.mesan.testdatagen.generators

// Copyright (C) 2014 Lars Reed -- GNU GPL 2.0 -- see LICENSE.txt

import no.mesan.testdatagen.ExtendedImpl

import scala.util.Random

/**
 * Generate doubles.
 * Default limits: 0..Double.MaxValue
 */
class Doubles extends ExtendedImpl[Double] {

  filter(x=> lower match { case Some(low)=>  x>=low;  case _=> true })
  filter(x=> upper match { case Some(high)=> x<=high; case _=> true })

  private var stepSize: Double= 1
  /** Step size, implies sequential values. */
  def step(step: Double): Doubles= {
    require(step!=0, "Step cannot be 0")
    stepSize= step
    sequential
  }

  def getStream: Stream[Double] = {
    val min= lower.getOrElse(0D)
    val max= upper.getOrElse(Double.MaxValue)
    require(max>=min, "from >= to")
    require(!(max-min).isInfinite, "too wide range")

    def next(curr: Double): Stream[Double]= {
      val k= if (curr>max) min else if (curr<min) max else curr
      k #:: next(k+stepSize)
    }

    def getRandomly: Stream[Double]=  (min + (max - min) * Random.nextDouble()) #:: getRandomly

    if (isRandom) getRandomly
    else if (stepSize < 0) next(max)
    else next(min)
  }

}

object Doubles {
  def apply(): Doubles = new Doubles()
}
