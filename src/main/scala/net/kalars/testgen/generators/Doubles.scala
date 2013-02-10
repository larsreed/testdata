package net.kalars.testgen.generators

import scala.annotation.tailrec
import scala.util.Random

import net.kalars.testgen.SingleGenerator

/**
 * Generate doubles.
 * Special methods: step(n) -- only used for sequential generation -- sets the step size (default 1)
 * Default limits: Double.MIN/MAX_VALUE
 */
class Doubles extends SingleGenerator[Double] {

  filter(x=> lower match { case Some(low)=>  x>=low;  case _=> true })
  filter(x=> upper match { case Some(high)=> x<=high; case _=> true })

  private var stepSize: Double= 1
  /** Step size, implies sequential values. */
  def step(step: Double): Doubles= {
    require(step!=0, "Step cannot be 0")
    stepSize= math.abs(step)
    sequential
  }

  override def get(n: Int): List[Double] = {
    require(n>=0, "cannot get negative count")
    val min= lower.getOrElse(Double.MinValue)
    val max= upper.getOrElse(Double.MaxValue)
    require(max>=min, "from >= to")

    def getSequentially(): List[Double]= {
      var step= if (isReversed) -stepSize else stepSize
      @tailrec def next(last: Double, soFar:List[Double]): List[Double]=
        if (soFar.length>=n) soFar
        else {
          val k= if (last>max) min else if (last<min) max else last
          if ( filterAll(k) ) next(k+step, k::soFar)
          else next(k+step, soFar)
        }
      if (isReversed) next(max, Nil).reverse
      else next(min, Nil).reverse
    }

    @tailrec
    def getRandomly(soFar: List[Double]): List[Double]=
      if (soFar.length>=n) soFar
      else {
        val nxt= min + (max-min)*Random.nextDouble()
        if (filterAll(nxt) && (!isUnique || !(soFar contains nxt)))
          getRandomly(nxt::soFar)
        else getRandomly(soFar)
      }

    if (isSequential) getSequentially
    else getRandomly(Nil)
  }

}

object Doubles {
  def apply(): Doubles = new Doubles()
}
