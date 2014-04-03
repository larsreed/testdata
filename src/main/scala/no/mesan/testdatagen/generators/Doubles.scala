package no.mesan.testdatagen.generators

import scala.util.Random

import no.mesan.testdatagen.{StreamGeneratorImpl, ExtendedImpl}

/**
 * Generate doubles.
 * Default limits: Double.MIN/MAX_VALUE
 *
 * @author lre
 */
class Doubles extends ExtendedImpl[Double] with StreamGeneratorImpl[Double] {

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
    val min= lower.getOrElse(Double.MinValue)
    val max= upper.getOrElse(Double.MaxValue)
    require(max>=min, "from >= to")

    def next(curr: Double): Stream[Double]= {
      val k= if (curr>max) min else if (curr<min) max else curr
      Stream.cons(k, next(k+stepSize))
    }

    def getRandomly: Stream[Double]=
      Stream.cons(min + (max - min) * Random.nextDouble(), getRandomly)

    if (isRandom) getRandomly
    else if (stepSize < 0) next(max)
    else next(min)
  }

}

object Doubles {
  def apply(): Doubles = new Doubles()
}
